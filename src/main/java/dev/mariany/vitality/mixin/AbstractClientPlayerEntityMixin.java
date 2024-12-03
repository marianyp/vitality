package dev.mariany.vitality.mixin;

import com.mojang.authlib.GameProfile;
import dev.kosmx.playerAnim.api.layered.AnimationStack;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier;
import dev.kosmx.playerAnim.api.layered.modifier.AdjustmentModifier;
import dev.kosmx.playerAnim.api.layered.modifier.SpeedModifier;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.core.util.Ease;
import dev.kosmx.playerAnim.core.util.Vec3f;
import dev.kosmx.playerAnim.impl.IAnimatedPlayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import dev.mariany.vitality.Vitality;
import dev.mariany.vitality.client.animation.AnimatablePlayer;
import dev.mariany.vitality.util.VitalityConstants;
import dev.mariany.vitality.util.VitalityUtils;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin extends PlayerEntity implements AnimatablePlayer {
    public AbstractClientPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void injectInit(ClientWorld world, GameProfile profile, CallbackInfo ci) {
        AnimationStack stack = ((IAnimatedPlayer) this).getAnimationStack();
        base.addModifier(createAdjustmentModifier(), 0);
        base.addModifier(speedModifier, 0);
        speedModifier.speed = 1.2f;
        stack.addAnimLayer(1000, base);
    }

    @Unique
    private final ModifierLayer<KeyframeAnimationPlayer> base = new ModifierLayer<>(null);
    @Unique
    private final SpeedModifier speedModifier = new SpeedModifier();
    @Unique
    private Vec3d lastRollDirection;

    public boolean vitality$isAnimating() {
        return base.isActive();
    }

    @Override
    public void vitality$playRollAnimation(Vec3d direction) {
        vitality$playRollAnimation(direction, 1);
    }

    @Override
    public void vitality$playRollAnimation(Vec3d direction, float speedMultiplier) {
        KeyframeAnimation animation = (KeyframeAnimation) PlayerAnimationRegistry.getAnimation(Vitality.id("roll"));
        KeyframeAnimation.AnimationBuilder copy = animation.mutableCopy();

        lastRollDirection = direction;

        // Attempt to smoothen initial transition
        copy.beginTick += 4;

        int fadeIn = copy.beginTick;
        float length = copy.endTick;

        speedModifier.speed = length / (VitalityConstants.ROLL_DURATION * speedMultiplier);

        base.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(fadeIn, Ease.INOUTELASTIC),
                new KeyframeAnimationPlayer(copy.build(), 0));
    }

    @Unique
    private AdjustmentModifier createAdjustmentModifier() {
        return new AdjustmentModifier((partName) -> {
            if (partName.equals("body")) {
                if (lastRollDirection != null) {
                    Vec3d initialOrientation = new Vec3d(0, 0, 1).rotateY((float) Math.toRadians(-1 * this.bodyYaw));

                    Vec3d targetOrientation = lastRollDirection.normalize();

                    KeyframeAnimationPlayer animation = base.getAnimation();

                    if (animation != null) {
                        float currentTick = animation.getCurrentTick();
                        float stopTick = animation.getStopTick();
                        float progress = MathHelper.clamp(currentTick / stopTick, 0, 1);

                        Vec3d currentOrientation = VitalityUtils.slerp(initialOrientation, targetOrientation, progress);

                        Vec3d planeNormal = initialOrientation.crossProduct(targetOrientation).normalize();

                        float horizontalAngle = (float) VitalityUtils.angleWithSignBetween(initialOrientation,
                                currentOrientation, planeNormal);
                        float xRot = (float) Math.toRadians(horizontalAngle);

                        AdjustmentModifier.PartModifier modifier = new AdjustmentModifier.PartModifier(
                                new Vec3f(xRot, 0, 0), new Vec3f(0, 0, 0));

                        return Optional.of(modifier);
                    }
                }
            }

            return Optional.empty();
        });
    }
}
