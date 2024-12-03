package dev.mariany.vitality.packet;

import dev.mariany.vitality.packet.clientbound.*;
import dev.mariany.vitality.packet.serverbound.*;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryByteBuf;

public class Packets {
    public static void register() {
        clientbound(PayloadTypeRegistry.playS2C());
        serverbound(PayloadTypeRegistry.playC2S());
    }

    private static void clientbound(PayloadTypeRegistry<RegistryByteBuf> registry) {
        registry.register(FoodHistorySyncPacket.ID, FoodHistorySyncPacket.CODEC);
        registry.register(DoubleJumpedPacket.ID, DoubleJumpedPacket.CODEC);
        registry.register(ClingedPacket.ID, ClingedPacket.CODEC);
        registry.register(SoftLandedPacket.ID, SoftLandedPacket.CODEC);
        registry.register(BooleanGameruleUpdatePacket.ID, BooleanGameruleUpdatePacket.CODEC);
        registry.register(IntGameruleUpdatePacket.ID, IntGameruleUpdatePacket.CODEC);
    }

    private static void serverbound(PayloadTypeRegistry<RegistryByteBuf> registry) {
        registry.register(DoubleJumpPacket.ID, DoubleJumpPacket.CODEC);
        registry.register(WallJumpPacket.ID, WallJumpPacket.CODEC);
        registry.register(ClingPacket.ID, ClingPacket.CODEC);
        registry.register(TriggerSoftLandPacket.ID, TriggerSoftLandPacket.CODEC);
        registry.register(CompletedSoftLandPacket.ID, CompletedSoftLandPacket.CODEC);
        registry.register(RequestFoodHistorySync.ID, RequestFoodHistorySync.CODEC);
        registry.register(RequestGamerulesSync.ID, RequestGamerulesSync.CODEC);
    }
}
