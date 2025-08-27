package dev.mariany.vitality;

import dev.mariany.vitality.attachment.VitalityAttachmentTypes;
import dev.mariany.vitality.config.VitalityConfig;
import dev.mariany.vitality.event.server.ServerTickHandler;
import dev.mariany.vitality.packet.VitalityPackets;
import dev.mariany.vitality.packet.serverbound.ServerBoundPackets;
import dev.mariany.vitality.sound.VitalitySoundEvents;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Vitality implements ModInitializer {
    public static final String MOD_ID = "vitality";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final VitalityConfig CONFIG = VitalityConfig.createAndLoad();

    @Override
    public void onInitialize() {
        VitalityPackets.register();
        ServerBoundPackets.init();

        VitalityAttachmentTypes.registerAttachmentTypes();
        ServerTickHandler.register();
        VitalitySoundEvents.registerModSoundEvents();
    }

    public static Identifier id(String resource) {
        return Identifier.of(MOD_ID, resource);
    }
}