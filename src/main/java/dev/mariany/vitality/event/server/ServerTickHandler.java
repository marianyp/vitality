package dev.mariany.vitality.event.server;

import dev.mariany.vitality.event.entity.PlayerTickHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

import java.util.List;
import java.util.function.Consumer;

public class ServerTickHandler {
    private static final List<Consumer<ServerWorld>> HANDLERS = List.of(PlayerTickHandler::onServerWorldTick);

    public static void onServerTick(MinecraftServer server) {
        for (ServerWorld world : server.getWorlds()) {
            for (Consumer<ServerWorld> handler : HANDLERS) {
                handler.accept(world);
            }
        }
    }
}

