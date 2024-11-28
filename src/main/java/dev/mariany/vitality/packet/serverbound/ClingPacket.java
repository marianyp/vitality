package dev.mariany.vitality.packet.serverbound;

import dev.mariany.vitality.Vitality;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;

public record ClingPacket() implements CustomPayload {
    public static final Id<ClingPacket> ID = new Id<>(Vitality.id("cling"));
    public static final PacketCodec<RegistryByteBuf, ClingPacket> CODEC = PacketCodec.unit(new ClingPacket());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void handle(ClingPacket packet, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        player.fallDistance = 0;
    }
}
