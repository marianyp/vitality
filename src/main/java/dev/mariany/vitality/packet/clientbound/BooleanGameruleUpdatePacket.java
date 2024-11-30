package dev.mariany.vitality.packet.clientbound;

import dev.mariany.vitality.Vitality;
import dev.mariany.vitality.gamerule.VitalityGamerules;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record BooleanGameruleUpdatePacket(String gamerule, boolean value) implements CustomPayload {
    public static final Id<BooleanGameruleUpdatePacket> ID = new Id<>(Vitality.id("boolean_gamerule_updated"));
    public static final PacketCodec<RegistryByteBuf, BooleanGameruleUpdatePacket> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, BooleanGameruleUpdatePacket::gamerule, PacketCodecs.BOOL,
            BooleanGameruleUpdatePacket::value, BooleanGameruleUpdatePacket::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void handle(BooleanGameruleUpdatePacket packet, ClientPlayNetworking.Context context) {
        ClientPlayerEntity player = context.player();
        ClientWorld world = player.clientWorld;
        String gameruleName = packet.gamerule();
        boolean value = packet.value;

        VitalityGamerules.BOOLEAN_RULES.stream().filter(key -> key.getName().equals(gameruleName)).findFirst()
                .ifPresent(gamerule -> world.getGameRules().get(gamerule).set(value, null));

    }
}
