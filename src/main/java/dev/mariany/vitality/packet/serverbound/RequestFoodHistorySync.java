package dev.mariany.vitality.packet.serverbound;

import dev.mariany.vitality.Vitality;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record RequestFoodHistorySync() implements CustomPayload {
    public static final CustomPayload.Id<RequestFoodHistorySync> ID = new CustomPayload.Id<>(
            Vitality.id("request_food_history"));
    public static final PacketCodec<RegistryByteBuf, RequestFoodHistorySync> CODEC = PacketCodec.unit(
            new RequestFoodHistorySync());

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
