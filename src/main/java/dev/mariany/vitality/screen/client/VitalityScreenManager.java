package dev.mariany.vitality.screen.client;

import dev.mariany.vitality.Vitality;
import dev.mariany.vitality.screen.VitalityPlayerScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class VitalityScreenManager {
    private static final Identifier DIET = Vitality.id("diet");
    private static final Identifier DIET_EMPTY = Vitality.id("diet_empty");

    private static final int SPRITE_WIDTH = 7;
    private static final int SPRITE_HEIGHT = 30;
    private static final int FILLED_SPRITE_HEIGHT = SPRITE_HEIGHT - 2;

    private static final int[] TOP_LEFT = {67, 47};

    private static final int TOTAL_STEPS = 4;
    private static final int STEP_HEIGHT = 7;

    public static VitalityScreen currentScreen;

    public static void init(VitalityScreen screen) {
        currentScreen = screen;
    }

    public static void update(int mouseX, int mouseY) {

    }

    public static void drawDiet(DrawContext context) {
        VitalityPlayerScreenHandler handler = currentScreen.vitality$getHandler();

        int maxScore = handler.vitality$getMaxScore();
        int score = handler.vitality$getScore();

        int x = currentScreen.vitality$getX();
        int y = currentScreen.vitality$getY();

        int xOffset = TOP_LEFT[0];
        int yOffset = TOP_LEFT[1];

        context.drawGuiTexture(DIET_EMPTY, x + xOffset, y + yOffset, SPRITE_WIDTH, SPRITE_HEIGHT);

        if (score > 0) {
            int revealedHeight;
            int steps = Math.round(TOTAL_STEPS * ((float) score / maxScore));

            if (score == 1) {
                revealedHeight = STEP_HEIGHT;
            } else {
                if (steps == 1) {
                    steps++;
                }
                revealedHeight = STEP_HEIGHT * steps;
            }

            context.drawGuiTexture(DIET, SPRITE_WIDTH, FILLED_SPRITE_HEIGHT, 0, FILLED_SPRITE_HEIGHT - revealedHeight,
                    x + xOffset, (y + yOffset + 1) + (FILLED_SPRITE_HEIGHT - revealedHeight), SPRITE_WIDTH,
                    revealedHeight);
        }
    }
}
