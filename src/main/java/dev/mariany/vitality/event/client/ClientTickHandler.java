package dev.mariany.vitality.event.client;

import dev.mariany.vitality.logic.DoubleJumpLogic;
import dev.mariany.vitality.logic.WallJumpLogic;
import dev.mariany.vitality.packet.serverbound.ClingPacket;
import dev.mariany.vitality.packet.serverbound.DoubleJumpPacket;
import dev.mariany.vitality.packet.serverbound.WallJumpPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class ClientTickHandler {
    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickHandler::onClientTick);
    }

    private static void onClientTick(MinecraftClient instance) {
        ClientPlayerEntity player = instance.player;
        if (player != null && player.input != null) {
            handleWallJump(player);
            handleDoubleJump(player);
        }
    }

    private static void handleWallJump(ClientPlayerEntity clientPlayer) {
        Consumer<PlayerEntity> onWallJump = (player) -> {
            player.fallDistance = 0;
            ClientPlayNetworking.send(new WallJumpPacket());
        };

        Consumer<PlayerEntity> onCling = player -> {
            player.fallDistance = 0;
            ClientPlayNetworking.send(new ClingPacket());
        };

        WallJumpLogic.handleWallJumpInput(clientPlayer, clientPlayer.input.movementForward,
                clientPlayer.input.movementSideways, clientPlayer.input.sneaking, onWallJump, onCling);
    }

    private static void handleDoubleJump(ClientPlayerEntity player) {
        if (DoubleJumpLogic.handleDoubleJumpInput(player, player.input.jumping)) {
            ClientPlayNetworking.send(new DoubleJumpPacket());
            WallJumpLogic.resetCling();
        }
    }
}
