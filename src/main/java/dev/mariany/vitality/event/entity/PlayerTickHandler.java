package dev.mariany.vitality.event.entity;

import dev.mariany.vitality.util.VitalityUtils;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class PlayerTickHandler {
    public static void onServerWorldTick(ServerWorld world) {
        if (world.getTime() % 20 == 0) {
            for (ServerPlayerEntity player : world.getPlayers()) {
                onPlayerTick(player);
            }
        }
    }

    private static void onPlayerTick(ServerPlayerEntity player) {
        if (VitalityUtils.getDietRating(player) <= 0) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 20 * 10, 0, false, true, true));
        }
    }
}
