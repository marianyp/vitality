package dev.mariany.vitality.mixin;

import dev.mariany.vitality.client.model.Clingable;
import dev.mariany.vitality.logic.WallJumpLogic;
import dev.mariany.vitality.util.VitalityUtils;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin implements Clingable {
    @Unique
    private int wallClingedTicks;

    @Inject(method = "eatFood", at = @At(value = "HEAD"))
    public void injectEatFood(World world, ItemStack stack, FoodComponent foodComponent,
                              CallbackInfoReturnable<ItemStack> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        VitalityUtils.addToFoodHistory(player, stack);
    }

    @Override
    public boolean vitality$isClinging() {
        PlayerEntity player = (PlayerEntity) (Object) this;
        return WallJumpLogic.canCling(player) && wallClingedTicks > 0;
    }

    @Override
    public void vitality$updateWallClingedTicks(int value) {
        this.wallClingedTicks = value;
    }
}
