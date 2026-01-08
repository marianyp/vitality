package dev.mariany.vitality.config;

import blue.endless.jankson.Comment;
import dev.mariany.vitality.Vitality;
import dev.mariany.vitality.util.VitalityConstants;
import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Sync;

@SuppressWarnings("unused")
@Config(name = Vitality.MOD_ID, wrapperName = "VitalityConfig")
@Sync(value = Option.SyncMode.OVERRIDE_CLIENT)
public class VitalityConfigModel {
    @Comment("Determines the amount of food to keep track of.")
    public int healthyEatingWindow = VitalityConstants.HEALTHY_EATING_WINDOW;

    @Comment("Enables/disables wall jumping.")
    public boolean allowWallJump = true;

    @Comment("Enables/disables double jumping.")
    public boolean allowDoubleJump = true;

    @Comment("Enables/disables soft landing (pressing spacebar before taking fall damage to negate it).")
    public boolean allowSoftLand = true;

    @Comment("Controls whether players receive regeneration when their diet goes up to 'good'.")
    public boolean regenerationFromImprovedDiet = true;

    @Comment("When disabled, no animation will be played when double jumping, soft landing, or wall clinging.")
    public boolean playAnimation = true;
}
