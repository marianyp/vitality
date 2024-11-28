package dev.mariany.vitality.sound;

import dev.mariany.vitality.Vitality;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class VitalitySoundEvents {
    public static final SoundEvent DOUBLE_JUMP = register("double_jump");

    private static SoundEvent register(String name) {
        Identifier id = Vitality.id(name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void registerModSoundEvents() {
        Vitality.LOGGER.info("Registering Mod Sound Events for " + Vitality.MOD_ID);
    }
}
