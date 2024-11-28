package dev.mariany.vitality.packet.serverbound;

import dev.mariany.vitality.Vitality;
import dev.mariany.vitality.util.VitalityConstants;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;

public record WallJumpPacket() implements CustomPayload {
    public static final Id<WallJumpPacket> ID = new Id<>(Vitality.id("wall_jump"));
    public static final PacketCodec<RegistryByteBuf, WallJumpPacket> CODEC = PacketCodec.unit(new WallJumpPacket());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void handle(WallJumpPacket packet, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        player.fallDistance = 0;
        player.getHungerManager().addExhaustion(VitalityConstants.WALL_JUMP_EXHAUSTION);
    }
}
