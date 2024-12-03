package dev.mariany.vitality.client.animation;

import net.minecraft.util.math.Vec3d;

public interface AnimatablePlayer {
    boolean vitality$isAnimating();
    void vitality$playRollAnimation(Vec3d direction);
    void vitality$playRollAnimation(Vec3d direction, float speedMultiplier);
}
