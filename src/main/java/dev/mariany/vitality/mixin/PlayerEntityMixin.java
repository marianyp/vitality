package dev.mariany.vitality.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.mariany.vitality.entity.ClingingEntity;
import dev.mariany.vitality.entity.SoftLandingEntity;
import dev.mariany.vitality.util.VitalityConstants;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements ClingingEntity, SoftLandingEntity {
    @Unique
    private int wallClingedTicks;

    @Unique
    private boolean willSoftLand;

    @WrapOperation(method = "handleFallDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;handleFallDamage(DFLnet/minecraft/entity/damage/DamageSource;)Z"))
    public boolean injectHandleFallDamage(PlayerEntity player, double fallDistance, float damageMultiplier,
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
