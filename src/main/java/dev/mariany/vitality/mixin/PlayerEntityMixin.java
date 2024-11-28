package dev.mariany.vitality.mixin;

import dev.mariany.vitality.util.VitalityUtils;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Inject(method="eatFood", at=@At(value = "HEAD"))
    public void injectEatFood(World world, ItemStack stack, FoodComponent foodComponent,
                        CallbackInfoReturnable<ItemStack> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        VitalityUtils.addToFoodHistory(player, stack);
    }
}
