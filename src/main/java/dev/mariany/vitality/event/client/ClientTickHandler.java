package dev.mariany.vitality.event.client;

import dev.mariany.vitality.client.animation.AnimatablePlayer;
import dev.mariany.vitality.entity.ClingingEntity;
import dev.mariany.vitality.entity.SoftLandingEntity;
import dev.mariany.vitality.logic.DoubleJumpLogic;
import dev.mariany.vitality.logic.SoftLandingLogic;
import dev.mariany.vitality.logic.WallJumpLogic;
import dev.mariany.vitality.packet.serverbound.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.function.BiConsumer;
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
            handleSoftLanding(player);
            handleDoubleJump(player);
        }
    }

    private static void handleWallJump(ClientPlayerEntity clientPlayer) {
        Consumer<PlayerEntity> onWallJump = (player) -> {
            player.fallDistance = 0;
            ClientPlayNetworking.send(new WallJumpPacket());
        };

        BiConsumer<PlayerEntity, Integer> onCling = (player, ticks) -> {
            if (ticks > 0) {
                player.fallDistance = 0;
            }
            if (player instanceof ClingingEntity clingingEntity) {
                clingingEntity.vitality$updateWallClingedTicks(ticks);
            }
            if (player instanceof SoftLandingEntity softLandingEntity) {
                softLandingEntity.vitality$setWillSoftLand(false);
            }
            ClientPlayNetworking.send(new ClingPacket(ticks));
        };

        WallJumpLogic.handleInput(clientPlayer, clientPlayer.input.movementForward, clientPlayer.input.movementSideways,
                clientPlayer.input.sneaking, onWallJump, onCling);
    }

    private static void handleDoubleJump(ClientPlayerEntity player) {
        Vec3d direction = DoubleJumpLogic.handleInput(player, player.input.jumping);

        if (direction != null) {
            ClientPlayNetworking.send(new DoubleJumpPacket());
            playVisuals(player, direction);
            WallJumpLogic.resetCling();
        }
    }

    private static void handleSoftLanding(ClientPlayerEntity clientPlayer) {
        Consumer<PlayerEntity> onTriggerSoftLand = (player) -> {
            ClientPlayNetworking.send(new TriggerSoftLandPacket()); // Update willSoftLand state on server
        };

        Vec3d direction = SoftLandingLogic.handleInput(clientPlayer, clientPlayer.input.jumping,
                clientPlayer.input.movementForward, clientPlayer.input.movementSideways, onTriggerSoftLand);

        if (direction != null) {
            ClientPlayNetworking.send(new CompletedSoftLandPacket(direction.x, direction.y,
                    direction.z)); // Display animation to other players on server
            playVisuals(clientPlayer, direction, 1 + (SoftLandingLogic.DISTANCE / 2));
        }
    }

    private static void playVisuals(ClientPlayerEntity player, Vec3d direction) {
        ((AnimatablePlayer) player).vitality$playRollAnimation(direction);
    }

    private static void playVisuals(ClientPlayerEntity player, Vec3d direction, float speedMultiplier) {
        ((AnimatablePlayer) player).vitality$playRollAnimation(direction, speedMultiplier);
    }
}
