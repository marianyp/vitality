package dev.mariany.vitality.logic;

import dev.mariany.vitality.entity.SoftLandingEntity;
import dev.mariany.vitality.sound.VitalitySoundEvents;
import dev.mariany.vitality.util.VitalityUtils;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DoubleJumpLogic {
    private static final float CROUCH_ADDITION = 0.175F;
    private static final float JUMP_MULTIPLIER = 0.25F;
    private static final float SPRINT_MULTIPLIER = 0.25F;
    private static final int MIDAIR_DELAY = 3;

    private static boolean canDoubleJump;
    private static boolean hasReleasedJumpKey;
    private static Integer midAirCooldown = null;

    private static boolean isOnGround(PlayerEntity player) {
        if (midAirCooldown == null) {
            return player.isOnGround();
        }
        return midAirCooldown != 0;
    }

    @Nullable
    public static Vec3d handleInput(PlayerEntity player, boolean jumping) {
        if (player.isOnGround()) {
            midAirCooldown = MIDAIR_DELAY;
        } else {
            int threshold = midAirCooldown == null ? 0 : midAirCooldown;
            midAirCooldown = Math.max(0, threshold - 1);
        }

        if (!player.isSubmergedInWater() && !player.isClimbing() && isOnGround(player)) {
            hasReleasedJumpKey = false;
            canDoubleJump = true;
        } else if (!jumping) {
            hasReleasedJumpKey = true;
        } else if (!player.getAbilities().flying && canDoubleJump && hasReleasedJumpKey && !player.isSubmergedInWater() && !player.isClimbing() && !player.isCreative()) {
            canDoubleJump = false;
            if (VitalityUtils.canDoubleJump(player)) {
                return doubleJump(player);
            }
        }
        return null;
    }

    public static Vec3d doubleJump(PlayerEntity player) {
        World world = player.getWorld();
        boolean sneaking = player.isSneaking();
        float exhaustMultiplier = 1F;

        player.fallDistance = 0;
        player.setIgnoreFallDamageFromCurrentExplosion(true);
        player.currentExplosionImpactPos = player.getPos();

        if (player instanceof SoftLandingEntity softLandingEntity) {
            softLandingEntity.vitality$setWillSoftLand(false);
        }

        double upwardsMotion = 0.5;

        StatusEffectInstance jumpBoostEffect = player.getStatusEffect(StatusEffects.JUMP_BOOST);

        if (jumpBoostEffect != null) {
            upwardsMotion += 0.1 * (jumpBoostEffect.getAmplifier() + 1);
        }

        if (player.isSprinting()) {
            upwardsMotion *= 1 + JUMP_MULTIPLIER;
        }

        if (sneaking) {
            upwardsMotion += CROUCH_ADDITION;
            exhaustMultiplier = 1.5F;
        }

        Vec3d motion = player.getVelocity();

        float yaw = (float) Math.toRadians(player.getYaw());

        double forwardX = -MathHelper.sin(yaw) * SPRINT_MULTIPLIER;
        double forwardZ = MathHelper.cos(yaw) * SPRINT_MULTIPLIER;

        Vec3d forwardMotion = new Vec3d(forwardX, upwardsMotion - motion.y, forwardZ);
        player.addVelocity(forwardMotion);

        if (!world.isClient) {
            player.incrementStat(Stats.JUMP);

            if (player.isSprinting()) {
                VitalityUtils.exhaust(player, 0.55F * exhaustMultiplier, 2.5F * exhaustMultiplier);
            } else {
                VitalityUtils.exhaust(player, 0.2F * exhaustMultiplier, 1.6F * exhaustMultiplier);
            }

            world.playSound(null, player.getBlockPos(), VitalitySoundEvents.DOUBLE_JUMP, SoundCategory.PLAYERS, 0.215F,
                    0.9F + player.getRandom().nextFloat() * 0.2F);
        }

        return forwardMotion;
    }
}
