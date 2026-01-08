package dev.mariany.vitality.client.animation;

import com.zigythebird.playeranim.animation.PlayerAnimResources;
import com.zigythebird.playeranim.animation.PlayerAnimationController;
import com.zigythebird.playeranimcore.animation.Animation;
import com.zigythebird.playeranimcore.animation.layered.modifier.AdjustmentModifier;
import com.zigythebird.playeranimcore.animation.layered.modifier.SpeedModifier;
import com.zigythebird.playeranimcore.math.Vec3f;
import dev.mariany.vitality.Vitality;
import dev.mariany.vitality.util.VitalityConstants;
import net.minecraft.entity.PlayerLikeEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

public class RollAnimationController extends PlayerAnimationController {
    public static final Identifier ID = Vitality.id("roll");

    private final SpeedModifier speedModifier;
    private Vec3d lastRollDirection;

    public RollAnimationController(PlayerLikeEntity player, AnimationStateHandler animationHandler) {
        super(player, animationHandler);

        this.speedModifier = new SpeedModifier(1.2F);

        this.postInit();
    }

    private void postInit() {
        this.addModifier(this.speedModifier, 0);
        this.addModifierLast(createAdjustmentModifier());
    }

    public void playRoll(Vec3d direction, float speedMultiplier) {
        this.lastRollDirection = direction;

        Animation animation = PlayerAnimResources.getAnimation(ID);

        if (animation == null) {
            return;
        }

        this.speedModifier.speed = animation.length() / (VitalityConstants.ROLL_DURATION * speedMultiplier);

        if (Vitality.CONFIG.playAnimation()) {
            this.triggerAnimation(animation);
        }
    }

    private AdjustmentModifier createAdjustmentModifier() {
        return new AdjustmentModifier((partName) -> {
            if (!partName.equals("body")) {
                return Optional.empty();
            }

            if (this.lastRollDirection == null) {
                return Optional.empty();
            }

            PlayerLikeEntity player = this.getAvatar();

            Vec3d initialOrientation = new Vec3d(0, 0, 1).rotateY(
                    (float) Math.toRadians(-1F * player.getYaw())
            );

            Vec3d targetOrientation = this.lastRollDirection.normalize();

            float currentTick = this.tick;
            float stopTick = this.getAnimationTicks();
            float progress = MathHelper.clamp(currentTick / stopTick, 0, 1);

            Vec3d orientation = slerp(initialOrientation, targetOrientation, progress);

            Vec3d planeNormal = initialOrientation.crossProduct(targetOrientation).normalize();

            double angle = signedAngleBetween(
                    orientation,
                    this.lastRollDirection,
                    planeNormal
            );

            float rotationY = Math.abs(angle) > 100 ? (float) Math.toRadians(angle) : 0;

            return Optional.of(
                    new AdjustmentModifier.PartModifier(
                            new Vec3f(0, rotationY, 0),
                            new Vec3f(0, 0, 0)
                    )
            );
        });
    }

    private static Vec3d slerp(Vec3d from, Vec3d to, float progress) {
        from = from.normalize();
        to = to.normalize();

        double dot = MathHelper.clamp(from.dotProduct(to), -1.0, 1.0);
        double theta = Math.acos(dot) * progress;

        Vec3d relativeVec = to.subtract(from.multiply(dot)).normalize();

        return from.multiply(Math.cos(theta)).add(relativeVec.multiply(Math.sin(theta)));
    }

    private static double signedAngleBetween(Vec3d from, Vec3d to, Vec3d planeNormal) {
        from = from.normalize();
        to = to.normalize();

        double cosineTheta = MathHelper.clamp(from.dotProduct(to), -1, 1);
        double angle = Math.toDegrees(Math.acos(cosineTheta));
        Vec3d cross = from.crossProduct(to);
        double sign = cross.dotProduct(planeNormal);

        return angle * Math.signum(sign);
    }
}
