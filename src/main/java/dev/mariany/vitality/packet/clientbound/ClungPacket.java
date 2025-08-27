package dev.mariany.vitality.packet.clientbound;

import dev.mariany.vitality.Vitality;
import dev.mariany.vitality.entity.ClingingEntity;
import dev.mariany.vitality.entity.SoftLandingEntity;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record ClungPacket(int entityId, boolean isClinging) implements CustomPayload {
    public static final Id<ClungPacket> ID = new Id<>(Vitality.id("clung"));
    public static final PacketCodec<RegistryByteBuf, ClungPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT, ClungPacket::entityId,
            PacketCodecs.BOOLEAN, ClungPacket::isClinging,
            ClungPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void handle(ClungPacket packet, ClientPlayNetworking.Context context) {
        ClientPlayerEntity player = context.player();
        ClientWorld world = player.clientWorld;

        Entity entity = world.getEntityById(packet.entityId);

        if (entity instanceof ClingingEntity clingingEntity) {
            clingingEntity.vitality$setIsClinging(packet.isClinging);
        }

        if (entity instanceof SoftLandingEntity softLandingEntity) {
            softLandingEntity.vitality$setWillSoftLand(false);
        }
    }
}
