package dev.mariany.vitality.packet.clientbound;

import dev.mariany.vitality.Vitality;
import dev.mariany.vitality.client.animation.AnimatablePlayer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.Vec3d;

public record DoubleJumpedPacket(int entityId, double x, double y, double z) implements CustomPayload {
    public static final Id<DoubleJumpedPacket> ID = new Id<>(Vitality.id("double_jumped"));
    public static final PacketCodec<RegistryByteBuf, DoubleJumpedPacket> CODEC = PacketCodec.tuple(PacketCodecs.VAR_INT,
            DoubleJumpedPacket::entityId, PacketCodecs.DOUBLE, DoubleJumpedPacket::x, PacketCodecs.DOUBLE,
            DoubleJumpedPacket::y, PacketCodecs.DOUBLE, DoubleJumpedPacket::z, DoubleJumpedPacket::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void handle(DoubleJumpedPacket packet, ClientPlayNetworking.Context context) {
        ClientPlayerEntity player = context.player();
        ClientWorld world = player.clientWorld;
        int entityId = packet.entityId;

        if (world.getEntityById(entityId) instanceof AnimatablePlayer animatablePlayer) {
            if (player.getId() != entityId) {
                animatablePlayer.vitality$playRollAnimation(new Vec3d(packet.x, packet.y, packet.z));
            }
        }
    }
}
