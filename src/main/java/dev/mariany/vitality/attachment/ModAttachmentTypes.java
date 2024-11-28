package dev.mariany.vitality.attachment;

import dev.mariany.vitality.Vitality;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.LinkedList;
import java.util.List;

public class ModAttachmentTypes {
    public static final AttachmentType<List<RegistryEntry<Item>>> FOOD_HISTORY = AttachmentRegistry.<List<RegistryEntry<Item>>>builder()
            .persistent(ItemStack.ITEM_CODEC.listOf()).initializer(LinkedList::new)
            .buildAndRegister(Vitality.id("food_history"));
}
