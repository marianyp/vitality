package dev.mariany.vitality.mixin;

import dev.mariany.vitality.screen.VitalityPlayerScreenHandler;
import dev.mariany.vitality.util.VitalityUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PlayerScreenHandler.class)
public abstract class PlayerScreenHandlerMixin extends ScreenHandler implements VitalityPlayerScreenHandler {
    @Final
    @Shadow
    private PlayerEntity owner;

    protected PlayerScreenHandlerMixin() {
        super(null, 0);
    }

    @Override
    public int vitality$getScore() {
        return VitalityUtils.getDietRating(owner);
    }

    @Override
    public int vitality$getMaxScore() {
        return VitalityUtils.getMaxDietRating(owner);
    }
}
