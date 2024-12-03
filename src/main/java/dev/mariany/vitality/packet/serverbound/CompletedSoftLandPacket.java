package dev.mariany.vitality.packet.serverbound;

import dev.mariany.vitality.Vitality;
import dev.mariany.vitality.packet.clientbound.SoftLandedPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientCommonPacketListener;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public record CompletedSoftLandPacket(double x, double y, double z) implements CustomPayload {
    public static final Id<CompletedSoftLandPacket> ID = new Id<>(Vitality.id("completed_soft_land"));
    public static final PacketCodec<RegistryByteBuf, CompletedSoftLandPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.DOUBLE, CompletedSoftLandPacket::x, PacketCodecs.DOUBLE, CompletedSoftLandPacket::y,
            PacketCodecs.DOUBLE, CompletedSoftLandPacket::z, CompletedSoftLandPacket::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void handle(CompletedSoftLandPacket packet, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        ServerWorld world = player.getServerWorld();

        Packet<ClientCommonPacketListener> softLandedPacket = ServerPlayNetworking.createS2CPacket(
                new SoftLandedPacket(player.getId(), packet.x, packet.y, packet.z));

        world.getChunkManager().sendToOtherNearbyPlayers(player, softLandedPacket);

    }
}
