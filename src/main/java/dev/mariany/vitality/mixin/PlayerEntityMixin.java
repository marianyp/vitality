package dev.mariany.vitality.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.mariany.vitality.entity.ClingingEntity;
import dev.mariany.vitality.entity.SoftLandingEntity;
import dev.mariany.vitality.util.VitalityConstants;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements ClingingEntity, SoftLandingEntity {
    @Unique
    private static final TrackedData<Boolean> CLINGING = DataTracker.registerData(
            PlayerEntityMixin.class,
            TrackedDataHandlerRegistry.BOOLEAN
    );

    @Unique
    private boolean willSoftLand;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public boolean vitality$isClinging() {
        return this.dataTracker.get(CLINGING);
    }

    @Override
    public void vitality$setIsClinging(boolean isClinging) {
        this.dataTracker.set(CLINGING, isClinging);
    }

    @Override
    public boolean vitality$willSoftLand() {
        return willSoftLand;
    }

    @Override
    public void vitality$setWillSoftLand(boolean value) {
        this.willSoftLand = value;
    }

    @Inject(method = "initDataTracker", at = @At(value = "TAIL"))
    protected void injectInitDataTracker(DataTracker.Builder builder, CallbackInfo ci) {
        builder.add(CLINGING, false);
    }

    @WrapOperation(
            method = "handleFallDamage",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/PlayerLikeEntity;handleFallDamage(DFLnet/minecraft/entity/damage/DamageSource;)Z"
            )
    )
    public boolean injectHandleFallDamage(
            PlayerEntity player,
            double fallDistance,
            float damageMultiplier,
            DamageSource damageSource,
            Operation<Boolean> original
    ) {
        World world = player.getEntityWorld();

        if (willSoftLand) {
            if (!world.isClient()) {
                willSoftLand = false; // Client will reset this value after animation plays
            }

            if (player.fallDistance <= VitalityConstants.MAX_SOFT_LAND_HEIGHT) {
                return false;
            }

            damageMultiplier = Math.min(damageMultiplier, VitalityConstants.SOFT_LAND_DAMAGE_MULTIPLIER);
        }

        return original.call(player, fallDistance, damageMultiplier, damageSource);
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    shift = At.Shift.AFTER,
                    target = "Lnet/minecraft/entity/PlayerLikeEntity;tick()V"
            )
    )
    protected void injectTick(CallbackInfo ci) {
        if (this.vitality$isClinging()) {
            PlayerEntity player = (PlayerEntity) (Object) this;
            player.setIgnoreFallDamageFromCurrentExplosion(false);
            this.fallDistance = 0;
        }
    }

    @Inject(method = "handleFallDamage", at = @At(value = "HEAD"), cancellable = true)
    public void injectHandleFallDamage(
            double fallDistance,
            float damagePerDistance,
            DamageSource damageSource,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (this.vitality$isClinging()) {
            cir.setReturnValue(false);
        }
    }
}
