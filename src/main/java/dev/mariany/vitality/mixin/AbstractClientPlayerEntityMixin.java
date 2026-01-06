package dev.mariany.vitality.mixin;

import com.mojang.authlib.GameProfile;
import com.zigythebird.playeranim.api.PlayerAnimationAccess;
import com.zigythebird.playeranimcore.animation.layered.IAnimation;
import dev.mariany.vitality.client.animation.AnimatablePlayer;
import dev.mariany.vitality.client.animation.RollAnimationController;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin extends PlayerEntity implements AnimatablePlayer {
    public AbstractClientPlayerEntityMixin(World world, GameProfile gameProfile) {
        super(world, gameProfile);
    }

    @Override
    public void vitality$playRollAnimation(Vec3d direction) {
        vitality$playRollAnimation(direction, 1);
    }

    @Override
    public void vitality$playRollAnimation(Vec3d direction, float speedMultiplier) {
        AbstractClientPlayerEntity player = (AbstractClientPlayerEntity) (Object) this;

        IAnimation controller = PlayerAnimationAccess.getPlayerAnimationLayer(player, RollAnimationController.ID);

        if (controller instanceof RollAnimationController rollAnimationController) {
            rollAnimationController.playRoll(direction, speedMultiplier);
        }
    }
}
