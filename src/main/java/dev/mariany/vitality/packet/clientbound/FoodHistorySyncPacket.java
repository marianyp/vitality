package dev.mariany.vitality.packet.clientbound;

import dev.mariany.vitality.Vitality;
import dev.mariany.vitality.attachment.ModAttachmentTypes;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.item.Item;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.List;

public record FoodHistorySyncPacket(List<RegistryEntry<Item>> foodHistory) implements CustomPayload {
    public static final CustomPayload.Id<FoodHistorySyncPacket> ID = new CustomPayload.Id<>(
            Vitality.id("food_history_sync"));
    public static final PacketCodec<RegistryByteBuf, FoodHistorySyncPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.registryEntry(RegistryKeys.ITEM).collect(PacketCodecs.toList()),
            FoodHistorySyncPacket::foodHistory, FoodHistorySyncPacket::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void handle(FoodHistorySyncPacket packet, ClientPlayNetworking.Context context) {
        context.player().setAttached(ModAttachmentTypes.FOOD_HISTORY, packet.foodHistory());
    }
}