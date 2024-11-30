package dev.mariany.vitality.logic;

import dev.mariany.vitality.util.VitalityConstants;
import dev.mariany.vitality.util.VitalityUtils;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class WallJumpLogic {
    private static int ticksWallClinged;
    private static int wallJumpCount;
    private static int ticksKeyDown;
    private static double clingX, clingZ;

    private static Set<Direction> walls = new HashSet<>();
    private static Set<Direction> staleWalls = new HashSet<>();

    public static void resetCling() {
        staleWalls.clear();
        wallJumpCount = 0;
    }

    public static void handleWallJumpInput(PlayerEntity player, float forward, float left, boolean sneaking,
                                           Consumer<PlayerEntity> onWallJump,
                                           BiConsumer<PlayerEntity, Integer> onCling) {
        World world = player.getWorld();
        BlockPos blockPos = player.getBlockPos();

        boolean onGround = player.isOnGround();
        boolean flying = player.getAbilities().flying;
        boolean isRiding = player.hasVehicle();
        FluidState fluidState = world.getFluidState(blockPos);

        if (onGround || flying || !fluidState.isEmpty() || isRiding) {
            ticksWallClinged = 0;
            clingX = Double.NaN;
            clingZ = Double.NaN;
            staleWalls.clear();
            wallJumpCount = 0;
        } else if (VitalityUtils.canWallJump(player)) {
            attemptWallJump(player, forward, left, sneaking, onWallJump, onCling);
        }
    }

    private static void attemptWallJump(PlayerEntity player, float forward, float left, boolean sneaking,
                                        Consumer<PlayerEntity> onWallJump, BiConsumer<PlayerEntity, Integer> onCling) {
        updateWalls(player);
        ticksKeyDown = sneaking ? ticksKeyDown + 1 : 0;
        BlockPos wallPos = getWallPos(player);

        if (ticksWallClinged < 1) {
            if (ticksKeyDown > 0 && ticksKeyDown < 4 && !walls.isEmpty() && canClingConsideringWalls(player)) {
                ticksWallClinged = 1;
                clingX = player.getX();
                clingZ = player.getZ();


                playHitSound(player, wallPos);
                spawnWallParticle(player, wallPos);
            }

            return;
        }

        if (canCling(player, sneaking)) {
            player.setPos(clingX, player.getY(), clingZ);

            double motionY = player.getVelocity().y;

            if (motionY > 0) {
                motionY = 0;
            } else if (motionY < -0.6) {
                motionY = motionY + 0.2;
                spawnWallParticle(player, wallPos);
            } else if (ticksWallClinged++ > VitalityConstants.WALL_SLIDE_DELAY) {
                motionY = -0.1;
                spawnWallParticle(player, wallPos);
            } else {
                motionY = 0;
            }

            onCling.accept(player, ticksWallClinged);

            player.setVelocity(0, motionY, 0);
            player.velocityDirty = true;
        } else {
            if (ticksWallClinged != 0) {
                onCling.accept(player, 0);
                ticksWallClinged = 0;
            }

            if ((forward != 0 || left != 0) && !player.isOnGround() && !walls.isEmpty()) {
                onWallJump.accept(player);
                wallJump(player, forward, left, VitalityConstants.WALL_JUMP_HEIGHT);

                if (wallJumpCount >= VitalityConstants.MAX_WALL_JUMPS) {
                    staleWalls = new HashSet<>(walls);
                }
            }
        }
    }

    public static boolean canCling(PlayerEntity player) {
        return canCling(player, player.isSneaking());
    }

    private static boolean canCling(PlayerEntity player, boolean sneaking) {
        World world = player.getWorld();
        BlockPos blockPos = player.getBlockPos();
        FluidState fluidState = world.getFluidState(blockPos);
        return sneaking && !player.isOnGround() && fluidState.isEmpty() && !walls.isEmpty();
    }

    private static boolean canClingConsideringWalls(PlayerEntity player) {
        if (player.isClimbing() || player.getVelocity().y > 0.1) {
            return false;
        }

        if (collidesWithBlock(player.getWorld(), player.getBoundingBox().offset(0, -0.8, 0))) {
            return false;
        }

        return !staleWalls.containsAll(walls);
    }

    private static boolean collidesWithBlock(World world, Box box) {
        return !world.isSpaceEmpty(box);
    }

    private static void updateWalls(Entity entity) {
        Vec3d pos = entity.getPos();
        Box box = new Box(pos.x - 0.001, pos.y, pos.z - 0.001, pos.x + 0.001,
                pos.y + entity.getEyeHeight(entity.getPose()), pos.z + 0.001);

        double dist = (entity.getWidth() / 2) + (ticksWallClinged > 0 ? 0.1 : 0.06);
        Box[] axes = {box.expand(0, 0, dist), box.expand(-dist, 0, 0), box.expand(0, 0, -dist), box.expand(dist, 0, 0)};

        int i = 0;
        Direction direction;
        WallJumpLogic.walls = new HashSet<>();
        for (Box axis : axes) {
            direction = Direction.fromHorizontal(i++);

            if (collidesWithBlock(entity.getWorld(), axis)) {
                walls.add(direction);
                entity.horizontalCollision = true;
            }
        }
    }

    private static Direction getClingDirection(Entity entity) {
        for (int i = 0; i <= entity.getHeight() + 1; i++) {
            Direction clingDirection = getClingDirection(entity, i);
            if (clingDirection != null) {
                return clingDirection;
            }
        }

        return Direction.UP;
    }

    @Nullable
    private static Direction getClingDirection(Entity entity, int offset) {
        Vec3d eyePos = entity.getEyePos().offset(Direction.DOWN, offset);

        Map<Direction, BlockPos> blockPositions = new HashMap<>();

        for (Direction direction : walls) {
            BlockPos blockPos = BlockPos.ofFloored(eyePos).offset(direction);
            blockPositions.put(direction, blockPos);
        }

        Direction closestDirection = null;
        double closestDistance = Double.MAX_VALUE;

        for (Map.Entry<Direction, BlockPos> entry : blockPositions.entrySet()) {
            Direction direction = entry.getKey();
            BlockPos pos = entry.getValue();

            double distanceSquared = entity.getPos().squaredDistanceTo(pos.toCenterPos());

            if (distanceSquared < closestDistance) {
                closestDistance = distanceSquared;
                closestDirection = direction;
            }
        }

        return closestDirection;

    }

    private static BlockPos getWallPos(Entity entity) {
        World world = entity.getWorld();
        Vec3d eyePos = entity.getEyePos();
        BlockPos blockPos = BlockPos.ofFloored(eyePos).offset(getClingDirection(entity));

        for (int i = 0; i <= entity.getHeight() + 1; i++) {
            BlockPos iterationBlockPos = blockPos.down(i);
            if (!world.getBlockState(iterationBlockPos).isAir()) {
                return iterationBlockPos;
            }
        }

        return blockPos;
    }

    private static void wallJump(LivingEntity entity, float foward, float left, float up) {
        float strafe = Math.signum(left) * up * up;
        float forward = Math.signum(foward) * up * up;

        float f = (float) (1.0F / Math.sqrt(strafe * strafe + up * up + forward * forward));
        strafe = strafe * f;
        forward = forward * f;

        float yaw = entity.getYaw();

        float f1 = (float) (Math.sin(yaw * 0.017453292F) * 0.45F);
        float f2 = (float) (Math.cos(yaw * 0.017453292F) * 0.45F);

        int jumpBoostLevel = 0;

        StatusEffectInstance jumpBoostEffect = entity.getStatusEffect(StatusEffects.JUMP_BOOST);

        if (jumpBoostEffect != null) {
            jumpBoostLevel = jumpBoostEffect.getAmplifier() + 1;
        }

        Vec3d motion = entity.getVelocity();
        entity.setVelocity(motion.x + (strafe * f2 - forward * f1), up + (jumpBoostLevel * 0.125),
                motion.z + (forward * f2 + strafe * f1));
        entity.velocityDirty = true;

        BlockPos wallPos = getWallPos(entity);

        playHitSound(entity, wallPos);
        spawnWallParticle(entity, wallPos);
        wallJumpCount++;
    }

    private static void playHitSound(Entity entity, BlockPos blockPos) {
        BlockState state = entity.getWorld().getBlockState(blockPos);
        if (!state.isAir()) {
            BlockSoundGroup soundGroup = state.getSoundGroup();
            entity.playSound(soundGroup.getHitSound(), soundGroup.getVolume() * 0.25F, soundGroup.getPitch());
        }
    }

    private static void spawnWallParticle(Entity entity, BlockPos blockPos) {
        World world = entity.getWorld();
        BlockState state = world.getBlockState(blockPos);
        if (state.getRenderType() != BlockRenderType.INVISIBLE) {
            Vec3d pos = entity.getPos();
            Vector3f motion = getClingDirection(entity).getUnitVector();

            world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, state), pos.x, pos.y, pos.z,
                    motion.x * -1.0D, -1.0D, motion.z * -1.0D);
        }
    }
}
