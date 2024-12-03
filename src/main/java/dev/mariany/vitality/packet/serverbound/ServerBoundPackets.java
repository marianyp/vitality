package dev.mariany.vitality.packet.serverbound;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class ServerBoundPackets {
    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(DoubleJumpPacket.ID, DoubleJumpPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(WallJumpPacket.ID, WallJumpPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(ClingPacket.ID, ClingPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(TriggerSoftLandPacket.ID, TriggerSoftLandPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(CompletedSoftLandPacket.ID, CompletedSoftLandPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(RequestFoodHistorySync.ID, RequestFoodHistorySync::handle);
        ServerPlayNetworking.registerGlobalReceiver(RequestGamerulesSync.ID, RequestGamerulesSync::handle);
    }
}
