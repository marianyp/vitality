package dev.mariany.vitality;

import dev.mariany.vitality.event.server.ServerTickHandler;
import dev.mariany.vitality.gamerule.VitalityGamerules;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Vitality implements ModInitializer {
    public static final String MOD_ID = "vitality";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        VitalityGamerules.registerModGamerules();
        ServerTickEvents.END_SERVER_TICK.register(ServerTickHandler::onServerTick);
    }

    public static Identifier id(String resource) {
        return Identifier.of(MOD_ID, resource);
    }
}