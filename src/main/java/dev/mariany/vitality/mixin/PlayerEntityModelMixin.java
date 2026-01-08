package dev.mariany.vitality.mixin;

import dev.mariany.vitality.Vitality;
import dev.mariany.vitality.client.render.entity.state.ClingingPlayerEntityRenderState;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.ArmPosing;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityModel.class)
public class PlayerEntityModelMixin extends BipedEntityModel<PlayerEntityRenderState> {

    public PlayerEntityModelMixin(ModelPart modelPart) {
        super(modelPart);
    }

    @Inject(
            method = "setAngles(Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;)V",
            at = @At(value = "TAIL")
    )
    private void injectSetAngles(PlayerEntityRenderState playerEntityRenderState, CallbackInfo ci) {
        if (!Vitality.CONFIG.playAnimation()) {
            return;
        }

        ClingingPlayerEntityRenderState clingingPlayerEntityRenderState =
                (ClingingPlayerEntityRenderState) playerEntityRenderState;

        if (clingingPlayerEntityRenderState.vitality$isClinging()) {
            ArmPosing.zombieArms(
                    this.leftArm,
                    this.rightArm,
                    true,
                    playerEntityRenderState.handSwingProgress,
                    playerEntityRenderState.age
            );
        }
    }
}
