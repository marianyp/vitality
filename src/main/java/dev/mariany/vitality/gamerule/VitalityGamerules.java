package dev.mariany.vitality.gamerule;

import dev.mariany.vitality.Vitality;
import dev.mariany.vitality.util.VitalityConstants;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;

public class VitalityGamerules {
    public static final GameRules.Key<GameRules.IntRule> HEALTHY_EATING_WINDOW = GameRuleRegistry.register(
            "healthyEatingWindow", GameRules.Category.MISC,
            GameRuleFactory.createIntRule(VitalityConstants.HEALTHY_EATING_WINDOW, 0));

    public static final GameRules.Key<GameRules.BooleanRule> ALLOW_WALL_JUMP = GameRuleRegistry.register(
            "allowWallJump", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));
    public static final GameRules.Key<GameRules.BooleanRule> ALLOW_DOUBLE_JUMP = GameRuleRegistry.register(
            "allowDoubleJump", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));

    public static void registerModGamerules() {
        Vitality.LOGGER.info("Registering Mod Gamerules for " + Vitality.MOD_ID);
    }
}
