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

public record ClingedPacket(int entityId, int wallClingTicks) implements CustomPayload {
    public static final Id<ClingedPacket> ID = new Id<>(Vitality.id("clinged"));
    public static final PacketCodec<RegistryByteBuf, ClingedPacket> CODEC = PacketCodec.tuple(PacketCodecs.VAR_INT,
            ClingedPacket::entityId, PacketCodecs.VAR_INT, ClingedPacket::wallClingTicks, ClingedPacket::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void handle(ClingedPacket packet, ClientPlayNetworking.Context context) {
        ClientPlayerEntity player = context.player();
        ClientWorld world = player.clientWorld;
        int entityId = packet.entityId;

        Entity entity = world.getEntityById(entityId);

        if (entity instanceof ClingingEntity clingingEntity) {
            clingingEntity.vitality$updateWallClingedTicks(packet.wallClingTicks);
        }

        if (entity instanceof SoftLandingEntity softLandingEntity) {
            softLandingEntity.vitality$setWillSoftLand(false);
        }
    }
}
