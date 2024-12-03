package dev.mariany.vitality.packet.serverbound;

import dev.mariany.vitality.Vitality;
import dev.mariany.vitality.entity.SoftLandingEntity;
import dev.mariany.vitality.util.VitalityUtils;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;

public record TriggerSoftLandPacket() implements CustomPayload {
    public static final Id<TriggerSoftLandPacket> ID = new Id<>(Vitality.id("trigger_soft_land"));
    public static final PacketCodec<RegistryByteBuf, TriggerSoftLandPacket> CODEC = PacketCodec.unit(
            new TriggerSoftLandPacket());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void handle(TriggerSoftLandPacket packet, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();

        if (VitalityUtils.canSoftLand(player)) {
            if (player instanceof SoftLandingEntity softLandingEntity) {
                softLandingEntity.vitality$setWillSoftLand(true);
            }
        }
    }
}
