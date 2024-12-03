package dev.mariany.vitality.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.mariany.vitality.entity.ClingingEntity;
import dev.mariany.vitality.entity.SoftLandingEntity;
import dev.mariany.vitality.util.VitalityConstants;
import dev.mariany.vitality.util.VitalityUtils;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements ClingingEntity, SoftLandingEntity {
    @Unique
    private int wallClingedTicks;

    @Unique
    private boolean willSoftLand;

    @Inject(method = "eatFood", at = @At(value = "HEAD"))
    public void injectEatFood(World world, ItemStack stack, FoodComponent foodComponent,
                              CallbackInfoReturnable<ItemStack> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        VitalityUtils.addToFoodHistory(player, stack);
    }

    @WrapOperation(method = "handleFallDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;handleFallDamage(FFLnet/minecraft/entity/damage/DamageSource;)Z"))
    public boolean injectHandleFallDamage(PlayerEntity player, float fallDistance, float damageMultiplier,
                                          DamageSource damageSource, Operation<Boolean> original) {
        World world = player.getWorld();

        if (willSoftLand) {
            if (!world.isClient) {
                willSoftLand = false; // Client will reset this value after animation plays
            }

            if (player.fallDistance <= VitalityConstants.MAX_SOFT_LAND_HEIGHT) {
                return false;
            }

            damageMultiplier = Math.min(damageMultiplier, VitalityConstants.SOFT_LAND_DAMAGE_MULTIPLIER);
        }

        return original.call(player, fallDistance, damageMultiplier, damageSource);
    }

    @Override
    public boolean vitality$isClinging() {
        return wallClingedTicks > 0;
    }

    @Override
    public void vitality$updateWallClingedTicks(int value) {
        this.wallClingedTicks = value;
    }

    @Override
    public boolean vitality$willSoftLand() {
        return willSoftLand;
    }

    @Override
    public void vitality$setWillSoftLand(boolean value) {
        this.willSoftLand = value;
    }
}
