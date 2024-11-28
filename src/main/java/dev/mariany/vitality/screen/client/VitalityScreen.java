package dev.mariany.vitality.screen.client;

import dev.mariany.vitality.screen.VitalityPlayerScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface VitalityScreen {
    VitalityPlayerScreenHandler vitality$getHandler();
    int vitality$getX();
    int vitality$getY();
}
