package dev.mariany.vitality;

import dev.mariany.vitality.event.client.ClientTickHandler;
import dev.mariany.vitality.packet.clientbound.ClientBoundPackets;
import dev.mariany.vitality.packet.serverbound.RequestFoodHistorySync;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;

@Environment(EnvType.CLIENT)
public class VitalityClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientBoundPackets.init();
        ClientPlayConnectionEvents.JOIN.register(VitalityClient::onJoin);
        ClientTickHandler.register();
    }

    private static void onJoin(ClientPlayNetworkHandler handler, PacketSender sender, MinecraftClient client) {
        sender.sendPacket(new RequestFoodHistorySync());
    }
}
