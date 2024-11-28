package dev.mariany.vitality.packet.clientbound;

import dev.mariany.vitality.attachment.ModAttachmentTypes;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ClientBoundPackets {
    public static void init() {
        // Food History Sync
        ClientPlayNetworking.registerGlobalReceiver(FoodHistorySyncPacket.ID, (payload, context) -> {
            context.player().setAttached(ModAttachmentTypes.FOOD_HISTORY, payload.foodHistory());
        });
    }
}
