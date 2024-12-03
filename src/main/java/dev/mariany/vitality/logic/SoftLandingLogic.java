package dev.mariany.vitality.logic;

import dev.mariany.vitality.entity.SoftLandingEntity;
import dev.mariany.vitality.util.VitalityUtils;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class SoftLandingLogic {
    public static final float DISTANCE = 0.5F;
    private static final int MAX_GROUND_DISTANCE = 7;

    private static boolean locked = false;
    private static boolean descending = false;
    private static float previousFallDistance = 0;
    private static Double previousExplosionY = null;

    @Nullable
    public static Vec3d handleInput(PlayerEntity player, boolean jumping, float forward, float sideways,
                                    Consumer<PlayerEntity> onTriggerSoftLand) {
        if (player instanceof SoftLandingEntity softLandingEntity) {
            boolean willSoftLand = softLandingEntity.vitality$willSoftLand();
            boolean onGround = player.isOnGround();
            boolean canTrigger = canTriggerSoftLand(player, jumping);

            if (!jumping) {
                locked = false;
            }

            if (player.fallDistance > previousFallDistance) {
                previousFallDistance = player.fallDistance;
                descending = true;
            } else {
                descending = false;
            }

            if (player.currentExplosionImpactPos != null) {
                previousExplosionY = player.currentExplosionImpactPos.y;
            }

            if (!willSoftLand && !locked && jumping && canTrigger && descending) {
                softLandingEntity.vitality$setWillSoftLand(true);
                onTriggerSoftLand.accept(player);
            }

            if (onGround) {
                boolean softLandNecessary = isSoftLandNecessary(player);

                previousExplosionY = null;
                previousFallDistance = 0;
                descending = false;

                if (willSoftLand) {
                    locked = true;
                    softLandingEntity.vitality$setWillSoftLand(false);
                    if (VitalityUtils.canSoftLand(player) && softLandNecessary) {
                        return softLand(player, forward, sideways);
                    }
                }
            }
        }

        return null;
    }

    private static boolean isSoftLandNecessary(PlayerEntity player) {
        double safeFallDistance = player.getAttributeValue(EntityAttributes.GENERIC_SAFE_FALL_DISTANCE);

        if (player.isInFluid()) {
            return false;
        }

        float fallDistance = previousFallDistance;

        if (previousExplosionY != null) {
            double playerY = player.getY();

            if (previousExplosionY < playerY) {
                return false;
            }

            fallDistance = Math.min(fallDistance, (float) (previousExplosionY - playerY));
        }

        return fallDistance > safeFallDistance;
    }

    private static boolean canTriggerSoftLand(PlayerEntity player, boolean jumping) {
        World world = player.getWorld();

        if (jumping && !player.getAbilities().allowFlying) {
            for (int i = 0; i < MAX_GROUND_DISTANCE; i++) {
                Box box = player.getDimensions(player.getPose()).getBoxAt(player.getSteppingPos().toBottomCenterPos());
                if (!world.isSpaceEmpty(box.offset(0, -i, 0))) {
                    return true;
                }
            }
        }

        return false;
    }

    public static Vec3d softLand(PlayerEntity player, float forward, float sideways) {
        World world = player.getWorld();
        Vec3d direction;

        if (forward == 0 && sideways == 0) {
            direction = new Vec3d(0, 0, 1);
        } else {
            direction = new Vec3d(sideways, 0, forward).normalize();
        }

        direction = direction.rotateY((float) Math.toRadians((-1) * player.getYaw())).multiply(DISTANCE);

        Block block = world.getBlockState(player.getBlockPos().down()).getBlock();
        float slipperiness = block.getSlipperiness();
        float defaultSlipperiness = Blocks.GRASS_BLOCK.getSlipperiness();

        if (slipperiness > defaultSlipperiness) {
            float multiplier = defaultSlipperiness / slipperiness;
            direction = direction.multiply(multiplier * multiplier);
        }

        player.addVelocity(direction.x, direction.y, direction.z);

        return direction;
    }
}
