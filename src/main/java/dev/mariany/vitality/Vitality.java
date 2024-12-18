package dev.mariany.vitality;

import dev.mariany.vitality.attachment.ModAttachmentTypes;
import dev.mariany.vitality.event.server.ServerTickHandler;
import dev.mariany.vitality.gamerule.VitalityGamerules;
import dev.mariany.vitality.packet.Packets;
import dev.mariany.vitality.packet.serverbound.ServerBoundPackets;
import dev.mariany.vitality.sound.VitalitySoundEvents;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Vitality implements ModInitializer {
    public static final String MOD_ID = "vitality";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        Packets.register();
        ServerBoundPackets.init();

        ModAttachmentTypes.registerAttachmentTypes();
        VitalityGamerules.registerModGamerules();
        ServerTickHandler.register();
        VitalitySoundEvents.registerModSoundEvents();
    }

    public static Identifier id(String resource) {
        return Identifier.of(MOD_ID, resource);
    }
}