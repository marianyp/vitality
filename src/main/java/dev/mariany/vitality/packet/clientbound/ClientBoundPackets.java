package dev.mariany.vitality.packet.clientbound;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ClientBoundPackets {
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(FoodHistorySyncPacket.ID, FoodHistorySyncPacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(DoubleJumpedPacket.ID, DoubleJumpedPacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(ClingedPacket.ID, ClingedPacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(SoftLandedPacket.ID, SoftLandedPacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(BooleanGameruleUpdatePacket.ID,
                BooleanGameruleUpdatePacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(IntGameruleUpdatePacket.ID, IntGameruleUpdatePacket::handle);
    }
}
