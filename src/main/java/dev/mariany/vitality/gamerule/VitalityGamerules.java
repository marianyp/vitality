package dev.mariany.vitality.gamerule;

import com.mojang.datafixers.util.Either;
import dev.mariany.vitality.Vitality;
import dev.mariany.vitality.util.VitalityConstants;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;

import java.util.ArrayList;
import java.util.List;

public class VitalityGamerules {
    public static final List<GameRules.Key<GameRules.BooleanRule>> BOOLEAN_RULES = new ArrayList<>();
    public static final List<GameRules.Key<GameRules.IntRule>> INT_RULES = new ArrayList<>();

    public static final GameRules.Key<GameRules.IntRule> HEALTHY_EATING_WINDOW = register("healthyEatingWindow",
            GameRules.Category.MISC, Either.right(VitalityConstants.HEALTHY_EATING_WINDOW));

    public static final GameRules.Key<GameRules.BooleanRule> ALLOW_WALL_JUMP = register("allowWallJump",
            GameRules.Category.MISC, Either.left(true));
    public static final GameRules.Key<GameRules.BooleanRule> ALLOW_DOUBLE_JUMP = register("allowDoubleJump",
            GameRules.Category.MISC, Either.left(true));
    public static final GameRules.Key<GameRules.BooleanRule> ALLOW_SOFT_LAND = register("allowSoftLand",
            GameRules.Category.MISC, Either.left(true));
    public static final GameRules.Key<GameRules.BooleanRule> IMPROVE_DIET_REGENERATION = register(
            "improveDietRegeneration", GameRules.Category.MISC, Either.left(true));

    private static <T extends GameRules.Rule<T>> GameRules.Key<T> register(String name, GameRules.Category category,
                                                                           Either<Boolean, Integer> defaultValue) {
        if (defaultValue.left().isPresent()) {
            boolean defaultBooleanValue = defaultValue.left().get();
            GameRules.Key<GameRules.BooleanRule> rule = GameRuleRegistry.register(name, category,
                    GameRuleFactory.createBooleanRule(defaultBooleanValue));
            BOOLEAN_RULES.add(rule);
            return (GameRules.Key<T>) rule;
        }

        if (defaultValue.right().isPresent()) {
            int defaultIntegerValue = defaultValue.right().get();
            GameRules.Key<GameRules.IntRule> rule = GameRuleRegistry.register(name, category,
                    GameRuleFactory.createIntRule(defaultIntegerValue, 0));
            INT_RULES.add(rule);
            return (GameRules.Key<T>) rule;
        }

        throw new IllegalStateException("Unhandled gamerule type");
    }

    public static void registerModGamerules() {
        Vitality.LOGGER.info("Registering Mod Gamerules for " + Vitality.MOD_ID);
    }
}
