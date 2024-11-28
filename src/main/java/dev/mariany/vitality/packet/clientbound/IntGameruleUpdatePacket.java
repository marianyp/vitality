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
import net.minecraft.world.GameRules;

public record IntGameruleUpdatePacket(String gamerule, int value) implements CustomPayload {
    public static final Id<IntGameruleUpdatePacket> ID = new Id<>(Vitality.id("int_gamerule_updated"));
    public static final PacketCodec<RegistryByteBuf, IntGameruleUpdatePacket> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, IntGameruleUpdatePacket::gamerule, PacketCodecs.INTEGER,
            IntGameruleUpdatePacket::value, IntGameruleUpdatePacket::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void handle(IntGameruleUpdatePacket packet, ClientPlayNetworking.Context context) {
        ClientPlayerEntity player = context.player();
        ClientWorld world = player.clientWorld;
        String gameruleName = packet.gamerule();
        int value = packet.value;

        GameRules.Key<GameRules.IntRule> gamerule = null;

        switch (gameruleName) {
            case "healthyEatingWindow" -> gamerule = VitalityGamerules.HEALTHY_EATING_WINDOW;
        }

        if (gamerule != null) {
            world.getGameRules().get(gamerule).set(value, null);
        }
    }
}
