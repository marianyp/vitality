package dev.mariany.vitality.tag;

import dev.mariany.vitality.Vitality;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class VitalityTags {
    public static class Items {
        public static TagKey<Item> HEALTHY = createTag("healthy");

        private static TagKey<Item> createTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, Vitality.id(name));
        }
    }
}
