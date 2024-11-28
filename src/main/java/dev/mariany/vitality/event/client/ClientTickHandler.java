package dev.mariany.vitality.event.client;

import dev.mariany.vitality.buff.BuffHandlers;
import dev.mariany.vitality.packet.serverbound.DoubleJumpPacket;
import dev.mariany.vitality.util.VitalityUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

@Environment(EnvType.CLIENT)
public class ClientTickHandler {
    private static boolean canDoubleJump;
    private static boolean hasReleasedJumpKey;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickHandler::onClientTick);
    }

    private static void onClientTick(MinecraftClient instance) {
        ClientPlayerEntity player = instance.player;
        if (player != null && player.input != null) {
            handleDoubleJumpInput(player);
        }
    }

    private static void handleDoubleJumpInput(ClientPlayerEntity player) {
        if (!player.isSubmergedInWater() && !player.isClimbing() && player.isOnGround()) {
            hasReleasedJumpKey = false;
            canDoubleJump = true;
        } else if (!player.input.jumping) {
            hasReleasedJumpKey = true;
        } else if (!player.getAbilities().flying && canDoubleJump && hasReleasedJumpKey && !player.isSubmergedInWater() && !player.isClimbing()) {
            canDoubleJump = false;
            if (VitalityUtils.canDoubleJump(player)) {
                ClientPlayNetworking.send(new DoubleJumpPacket());
                BuffHandlers.doubleJump(player);
            }
        }
    }
}
