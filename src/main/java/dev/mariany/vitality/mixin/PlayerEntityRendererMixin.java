package dev.mariany.vitality.mixin;

import dev.mariany.vitality.client.render.entity.state.ClingingPlayerEntityRenderState;
import dev.mariany.vitality.entity.ClingingEntity;
import dev.mariany.vitality.logic.WallJumpLogic;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {
    @Inject(
            method = "updateRenderState(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;F)V",
            at = @At(value = "TAIL")
    )
    public void updateRenderState(
            AbstractClientPlayerEntity abstractClientPlayerEntity,
            PlayerEntityRenderState playerEntityRenderState,
            float f,
            CallbackInfo ci
    ) {
        ClingingPlayerEntityRenderState clingingPlayerEntityRenderState =
                (ClingingPlayerEntityRenderState) playerEntityRenderState;

        if (abstractClientPlayerEntity instanceof ClingingEntity clingingEntity) {
            clingingPlayerEntityRenderState.vitality$setClinging(
                    clingingEntity.vitality$isClinging() && WallJumpLogic.canCling(abstractClientPlayerEntity)
            );
        }
    }
}
