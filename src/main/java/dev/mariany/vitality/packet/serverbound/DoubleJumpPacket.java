package dev.mariany.vitality.packet.serverbound;

import dev.mariany.vitality.Vitality;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record DoubleJumpPacket() implements CustomPayload {
    public static final CustomPayload.Id<DoubleJumpPacket> ID = new CustomPayload.Id<>(Vitality.id("double_jump"));
    public static final PacketCodec<RegistryByteBuf, DoubleJumpPacket> CODEC = PacketCodec.unit(
            new DoubleJumpPacket());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
