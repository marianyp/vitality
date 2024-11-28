package dev.mariany.vitality.packet.serverbound;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class ServerBoundPackets {
    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(DoubleJumpPacket.ID, DoubleJumpPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(RequestFoodHistorySync.ID, RequestFoodHistorySync::handle);
    }
}
