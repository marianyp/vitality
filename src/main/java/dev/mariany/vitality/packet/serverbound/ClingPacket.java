package dev.mariany.vitality.packet.serverbound;

import dev.mariany.vitality.Vitality;
import dev.mariany.vitality.client.model.Clingable;
import dev.mariany.vitality.packet.clientbound.ClingedPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public record ClingPacket(int wallClingTicks) implements CustomPayload {
    public static final Id<ClingPacket> ID = new Id<>(Vitality.id("cling"));
    public static final PacketCodec<RegistryByteBuf, ClingPacket> CODEC = PacketCodec.tuple(PacketCodecs.INTEGER,
            ClingPacket::wallClingTicks, ClingPacket::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void handle(ClingPacket packet, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        ServerWorld world = player.getServerWorld();
        int wallClingTicks = packet.wallClingTicks;

        if (wallClingTicks > 0) {
            player.fallDistance = 0;
        }

        if (player instanceof Clingable clingable) {
            clingable.vitality$updateWallClingedTicks(packet.wallClingTicks);

            for (ServerPlayerEntity otherPlayer : world.getPlayers()) {
                if (!otherPlayer.equals(player)) {
                    ServerPlayNetworking.send(otherPlayer, new ClingedPacket(player.getId(), wallClingTicks));
                }
            }
        }
    }
}
