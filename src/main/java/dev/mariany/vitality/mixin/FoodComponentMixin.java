package dev.mariany.vitality.mixin;

import dev.mariany.vitality.util.VitalityUtils;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodComponent.class)
public class FoodComponentMixin {
    @Inject(method = "onConsume", at = @At(value = "HEAD"))
    public void injectOnConsume(World world, LivingEntity entity, ItemStack stack, ConsumableComponent consumable,
                                CallbackInfo ci) {
        if (entity instanceof PlayerEntity player) {
            VitalityUtils.addToFoodHistory(player, stack);
        }
    }
}
