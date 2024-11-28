package dev.mariany.vitality.gamerule;

import dev.mariany.vitality.Vitality;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;

public class VitalityGamerules {
    public static final GameRules.Key<GameRules.IntRule> HEALTHY_EATING_WINDOW = GameRuleRegistry.register(
            "healthyEatingWindow", GameRules.Category.MISC, GameRuleFactory.createIntRule(10, 0));

    public static void registerModGamerules() {
        Vitality.LOGGER.info("Registering Mod Gamerules for " + Vitality.MOD_ID);
    }
}
