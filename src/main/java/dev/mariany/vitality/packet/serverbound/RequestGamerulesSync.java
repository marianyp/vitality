package dev.mariany.vitality.packet.serverbound;

import dev.mariany.vitality.Vitality;
import dev.mariany.vitality.gamerule.VitalityGamerules;
import dev.mariany.vitality.packet.clientbound.BooleanGameruleUpdatePacket;
import dev.mariany.vitality.packet.clientbound.IntGameruleUpdatePacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;

public record RequestGamerulesSync() implements CustomPayload {
    public static final Id<RequestGamerulesSync> ID = new Id<>(Vitality.id("request_gamerules_sync"));
    public static final PacketCodec<RegistryByteBuf, RequestGamerulesSync> CODEC = PacketCodec.unit(
            new RequestGamerulesSync());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void handle(RequestGamerulesSync packet, ServerPlayNetworking.Context context) {
        MinecraftServer server = context.server();

        for (GameRules.Key<GameRules.BooleanRule> booleanRule : VitalityGamerules.BOOLEAN_RULES) {
            context.responseSender().sendPacket(getBoolPacket(server, booleanRule));
        }

        for (GameRules.Key<GameRules.IntRule> intRule : VitalityGamerules.INT_RULES) {
            context.responseSender().sendPacket(getIntPacket(server, intRule));
        }
    }

    private static CustomPayload getBoolPacket(MinecraftServer server, GameRules.Key<GameRules.BooleanRule> key) {
        GameRules gameRules = server.getGameRules();
        return new BooleanGameruleUpdatePacket(key.getName(), gameRules.get(key).get());
    }

    private static CustomPayload getIntPacket(MinecraftServer server, GameRules.Key<GameRules.IntRule> key) {
        GameRules gameRules = server.getGameRules();
        return new IntGameruleUpdatePacket(key.getName(), gameRules.get(key).get());
    }
}
