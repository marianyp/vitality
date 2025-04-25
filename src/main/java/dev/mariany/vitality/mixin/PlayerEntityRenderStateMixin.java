package dev.mariany.vitality.mixin;

import dev.mariany.vitality.client.render.entity.state.ClingingPlayerEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerEntityRenderState.class)
public class PlayerEntityRenderStateMixin implements ClingingPlayerEntityRenderState {
    @Unique
    boolean isClinging = false;

    @Override
    public boolean vitality$isClinging() {
        return isClinging;
    }

    @Override
    public void vitality$setClinging(boolean flag) {
        this.isClinging = flag;
    }
}
