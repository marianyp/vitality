package dev.mariany.vitality.client;

import dev.mariany.vitality.tag.VitalityTags;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class Tooltips {
    public static void register() {
        ItemTooltipCallback.EVENT.register((stack, context, tooltipType, lines) -> {
            if (stack.isIn(VitalityTags.Items.HEALTHY)) {
                lines.add(Text.translatable("item.vitality.healthy").formatted(Formatting.GREEN));
            }
        });
    }
}
