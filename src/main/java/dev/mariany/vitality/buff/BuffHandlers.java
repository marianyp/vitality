package dev.mariany.vitality.buff;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BuffHandlers {
    private static final float JUMP_MULTIPLIER = 0.25F;
    private static final float SPRINT_MULTIPLIER = 0.25F;

    public static void doubleJump(PlayerEntity player) {
        player.fallDistance = 0;
        player.setIgnoreFallDamageFromCurrentExplosion(true);
        player.currentExplosionImpactPos = player.getPos();

        double upwardsMotion = 0.5;

        StatusEffectInstance jumpBoostEffect = player.getStatusEffect(StatusEffects.JUMP_BOOST);

        if (jumpBoostEffect != null) {
            upwardsMotion += 0.1 * (jumpBoostEffect.getAmplifier() + 1);
        }

        if (player.isSprinting()) {
            upwardsMotion *= 1 + JUMP_MULTIPLIER;
        }

        Vec3d motion = player.getVelocity();

        float yaw = (float) Math.toRadians(player.getYaw());

        double forwardX = -MathHelper.sin(yaw) * SPRINT_MULTIPLIER;
        double forwardZ = MathHelper.cos(yaw) * SPRINT_MULTIPLIER;

        player.addVelocity(forwardX, upwardsMotion - motion.y, forwardZ);

        player.incrementStat(Stats.JUMP);

        HungerManager hungerManager = player.getHungerManager();

        if (player.isSprinting()) {
            hungerManager.addExhaustion(0.2F);
        } else {
            hungerManager.addExhaustion(0.05F);
        }

        World world = player.getWorld();

        if (!world.isClient) {
            world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_WOOL_FALL, SoundCategory.PLAYERS, 1F,
                    0.9F + player.getRandom().nextFloat() * 0.2F);
        }
    }
}
