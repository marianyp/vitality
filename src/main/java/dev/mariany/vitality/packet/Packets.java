package dev.mariany.vitality.packet;

import dev.mariany.vitality.packet.clientbound.FoodHistorySyncPacket;
import dev.mariany.vitality.packet.serverbound.ClingPacket;
import dev.mariany.vitality.packet.serverbound.DoubleJumpPacket;
import dev.mariany.vitality.packet.serverbound.RequestFoodHistorySync;
import dev.mariany.vitality.packet.serverbound.WallJumpPacket;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryByteBuf;

public class Packets {
    public static void register() {
        clientbound(PayloadTypeRegistry.playS2C());
        serverbound(PayloadTypeRegistry.playC2S());
    }

    private static void clientbound(PayloadTypeRegistry<RegistryByteBuf> registry) {
        registry.register(FoodHistorySyncPacket.ID, FoodHistorySyncPacket.CODEC);
    }

    private static void serverbound(PayloadTypeRegistry<RegistryByteBuf> registry) {
        registry.register(DoubleJumpPacket.ID, DoubleJumpPacket.CODEC);
        registry.register(WallJumpPacket.ID, WallJumpPacket.CODEC);
        registry.register(ClingPacket.ID, ClingPacket.CODEC);
        registry.register(RequestFoodHistorySync.ID, RequestFoodHistorySync.CODEC);
    }
}
