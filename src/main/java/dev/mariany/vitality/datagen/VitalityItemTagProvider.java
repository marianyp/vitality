package dev.mariany.vitality.datagen;

import dev.mariany.vitality.tag.VitalityTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagBuilder;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class VitalityItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public VitalityItemTagProvider(
            FabricDataOutput output,
            CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture
    ) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        valueLookupBuilder(VitalityTags.Items.HEALTHY).add(
                Items.APPLE,
                Items.BEETROOT,
                Items.BEETROOT_SOUP,
                Items.CARROT,
                Items.GLOW_BERRIES,
                Items.MELON_SLICE,
                Items.RABBIT_STEW,
                Items.SWEET_BERRIES
        );

        supportExternalMod(VitalityTags.Items.HEALTHY, "genesis:healthy_stew");

        supportExternalMod(
                VitalityTags.Items.HEALTHY, "farmersdelight",
                "cabbage",
                "tomato",
                "onion",
                "tomato_sauce",
                "pumpkin_slice",
                "cabbage_leaf",
                "fruit_salad",
                "mixed_salad",
                "cabbage_rolls",
                "kelp_roll",
                "kelp_roll_slice",
                "vegetable_soup",
                "ratatouille",
                "chicken_soup",
                "mushroom_rice",
                "vegetable_noodles",
                "pumpkin_soup",
                "grilled_salmon",
                "baked_cod_stew",
                "fish_stew",
                "dumplings"
        );
    }

    private void supportExternalMod(TagKey<Item> tag, String item) {
        Identifier id = Identifier.of(item);
        supportExternalMod(tag, id.getNamespace(), id.getPath());
    }

    private void supportExternalMod(TagKey<Item> tag, String modName, String... items) {
        TagBuilder tagBuilder = getTagBuilder(tag);

        for (String item : items) {
            tagBuilder.addOptional(Identifier.of(modName, item));
        }
    }
}
