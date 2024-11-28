package dev.mariany.vitality.packet.serverbound;

import dev.mariany.vitality.Vitality;
import dev.mariany.vitality.attachment.ModAttachmentTypes;
import dev.mariany.vitality.packet.clientbound.FoodHistorySyncPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.Item;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.LinkedList;
import java.util.List;

public record RequestFoodHistorySync() implements CustomPayload {
    public static final CustomPayload.Id<RequestFoodHistorySync> ID = new CustomPayload.Id<>(
            Vitality.id("request_food_history"));
    public static final PacketCodec<RegistryByteBuf, RequestFoodHistorySync> CODEC = PacketCodec.unit(
            new RequestFoodHistorySync());

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void handle(RequestFoodHistorySync packet, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        List<RegistryEntry<Item>> foodHistory = player.getAttachedOrElse(ModAttachmentTypes.FOOD_HISTORY,
                new LinkedList<>());
        context.responseSender().sendPacket(new FoodHistorySyncPacket(foodHistory));
    }
}
