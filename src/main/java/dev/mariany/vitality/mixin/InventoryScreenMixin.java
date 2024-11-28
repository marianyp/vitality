package dev.mariany.vitality.mixin;

import dev.mariany.vitality.screen.VitalityPlayerScreenHandler;
import dev.mariany.vitality.screen.client.VitalityScreen;
import dev.mariany.vitality.screen.client.VitalityScreenManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractInventoryScreen<PlayerScreenHandler> implements VitalityScreen {
    public InventoryScreenMixin(PlayerScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }

    @Inject(at = @At("HEAD"), method = "init")
    private void init(CallbackInfo info) {
        VitalityScreenManager.init(this);
    }

    @Inject(at = @At("HEAD"), method = "render")
    private void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo info) {
        VitalityScreenManager.update(mouseX, mouseY);
    }

    @Inject(at = @At("RETURN"), method = "drawBackground")
    private void drawBackground(DrawContext context, float delta, int mouseX, int mouseY, CallbackInfo info) {
        VitalityScreenManager.drawDiet(context);
    }

    @Override
    public VitalityPlayerScreenHandler vitality$getHandler() {
        return (VitalityPlayerScreenHandler) this.handler;
    }

    @Override
    public int vitality$getX() {
        return this.x;
    }

    @Override
    public int vitality$getY() {
        return this.y;
    }
}
