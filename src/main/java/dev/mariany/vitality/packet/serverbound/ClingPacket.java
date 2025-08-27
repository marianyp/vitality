package dev.mariany.vitality.packet.serverbound;

import dev.mariany.vitality.Vitality;
import dev.mariany.vitality.entity.ClingingEntity;
import dev.mariany.vitality.entity.SoftLandingEntity;
import dev.mariany.vitality.packet.clientbound.ClungPacket;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;

public record ClingPacket(boolean isClinging) implements CustomPayload {
    public static final Id<ClingPacket> ID = new Id<>(Vitality.id("cling"));
    public static final PacketCodec<RegistryByteBuf, ClingPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.BOOLEAN, ClingPacket::isClinging,
            ClingPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void handle(ClingPacket packet, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();

        boolean isClinging = packet.isClinging;

        if (player instanceof ClingingEntity clingingEntity) {
            clingingEntity.vitality$setIsClinging(isClinging);
        }

        if (player instanceof SoftLandingEntity softLandingEntity) {
            softLandingEntity.vitality$setWillSoftLand(false);
        }

        for (ServerPlayerEntity otherPlayer : PlayerLookup.tracking(player)) {
            if (!otherPlayer.equals(player)) {
                ServerPlayNetworking.send(otherPlayer, new ClungPacket(player.getId(), isClinging));
            }
        }
    }
}
