package dev.mariany.vitality;

import dev.mariany.vitality.gamerule.VitalityGamerules;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Vitality implements ModInitializer {
    public static final String MOD_ID = "vitality";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        VitalityGamerules.registerModGamerules();
    }

    public static Identifier id(String resource) {
        return Identifier.of(MOD_ID, resource);
    }
}