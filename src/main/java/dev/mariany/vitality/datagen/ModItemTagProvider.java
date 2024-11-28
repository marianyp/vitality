package dev.mariany.vitality.datagen;

import dev.mariany.vitality.tag.VitalityTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {
    private static final List<Item> HEALTHY_ITEMS = List.of(Items.APPLE, Items.BEETROOT, Items.BEETROOT_SOUP,
            Items.CARROT, Items.GLOW_BERRIES, Items.MELON_SLICE, Items.RABBIT_STEW, Items.SWEET_BERRIES);

    public ModItemTagProvider(FabricDataOutput output,
                              CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        FabricTagProvider<Item>.FabricTagBuilder builder = getOrCreateTagBuilder(VitalityTags.Items.HEALTHY);
        for (Item healthyItem : HEALTHY_ITEMS) {
            builder.add(healthyItem);
        }
    }
}
