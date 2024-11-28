package dev.mariany.vitality.packet.serverbound;

import dev.mariany.vitality.attachment.ModAttachmentTypes;
import dev.mariany.vitality.buff.BuffHandlers;
import dev.mariany.vitality.packet.clientbound.FoodHistorySyncPacket;
import dev.mariany.vitality.util.VitalityUtils;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.LinkedList;
import java.util.List;

public class ServerBoundPackets {
    public static void init() {
        // Double Jump
        ServerPlayNetworking.registerGlobalReceiver(DoubleJumpPacket.ID, (payload, context) -> {
            ServerPlayerEntity player = context.player();
            ServerWorld world = player.getServerWorld();

            if (VitalityUtils.canDoubleJump(player)) {
                BuffHandlers.doubleJump(player);

                for (int i = 0; i < 8; ++i) {
                    double motionX = player.getRandom().nextGaussian() * 0.02;
                    double motionY = player.getRandom().nextGaussian() * 0.02 + 0.20;
                    double motionZ = player.getRandom().nextGaussian() * 0.02;

                    ParticleEffect particleType = player.isSubmergedInWater() ? ParticleTypes.BUBBLE : ParticleTypes.POOF;
                    world.spawnParticles(particleType, player.getX(), player.getY(), player.getZ(), 1, motionX, motionY,
                            motionZ, 0.15);
                }
            }
        });

        // Request Food History Sync
        ServerPlayNetworking.registerGlobalReceiver(RequestFoodHistorySync.ID, (payload, context) -> {
            ServerPlayerEntity player = context.player();
            List<RegistryEntry<Item>> foodHistory = player.getAttachedOrElse(ModAttachmentTypes.FOOD_HISTORY,
                    new LinkedList<>());
            context.responseSender().sendPacket(new FoodHistorySyncPacket(foodHistory));
        });
    }
}
