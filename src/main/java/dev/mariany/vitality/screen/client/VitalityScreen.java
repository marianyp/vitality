package dev.mariany.vitality.screen.client;

import dev.mariany.vitality.screen.VitalityPlayerScreenHandler;

public interface VitalityScreen {
    VitalityPlayerScreenHandler vitality$getHandler();
    int vitality$getX();
    int vitality$getY();
}
