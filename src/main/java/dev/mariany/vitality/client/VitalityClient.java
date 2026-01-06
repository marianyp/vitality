package dev.mariany.vitality.client;

import com.zigythebird.playeranim.api.PlayerAnimationFactory;
import com.zigythebird.playeranimcore.enums.PlayState;
import dev.mariany.vitality.client.animation.RollAnimationController;
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
        VitalityTooltips.register();

        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(
                RollAnimationController.ID,
                1000,
                player -> new RollAnimationController(
                        player,
                        (controller, state, animSetter) -> PlayState.STOP
                )
        );
    }

    private static void onJoin(ClientPlayNetworkHandler handler, PacketSender sender, MinecraftClient client) {
        sender.sendPacket(new RequestFoodHistorySync());
    }
}
