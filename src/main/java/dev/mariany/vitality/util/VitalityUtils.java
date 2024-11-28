package dev.mariany.vitality.util;

import dev.mariany.vitality.attachment.ModAttachmentTypes;
import dev.mariany.vitality.gamerule.VitalityGamerules;
import dev.mariany.vitality.tag.VitalityTags;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.LinkedList;
import java.util.List;

public class VitalityUtils {
    public static List<Item> getFoodHistory(LivingEntity entity) {
        List<Item> items = new LinkedList<>();
        if (entity.hasAttached(ModAttachmentTypes.FOOD_HISTORY)) {
            List<RegistryEntry<Item>> entries = entity.getAttachedOrElse(ModAttachmentTypes.FOOD_HISTORY,
                    new LinkedList<>());
            items.addAll(entries.stream().map(RegistryEntry::value).toList());
        }
        return items;
    }

    public static int getMaxDietRating(LivingEntity entity) {
        return entity.getWorld().getGameRules().get(VitalityGamerules.HEALTHY_EATING_WINDOW).get();
    }

    public static int getDietRating(LivingEntity entity) {
        List<Item> foodHistory = VitalityUtils.getFoodHistory(entity);
        int foodHistorySize = foodHistory.size();
        int maxDietRating = getMaxDietRating(entity);

        for (int i = 0; i < foodHistorySize; i++) {
            if (isHealthy(foodHistory.get(i))) {
                return maxDietRating - i;
            }
        }

        if (foodHistorySize < maxDietRating) {
            return 2;
        }

        return 0;
    }

    public static boolean isHealthy(Item item) {
        return item.getRegistryEntry().isIn(VitalityTags.Items.HEALTHY);
    }

    public static void addToFoodHistory(PlayerEntity player, ItemStack stack) {
        List<Item> foodHistory = VitalityUtils.getFoodHistory(player);
        foodHistory.addFirst(stack.getItem());

        if (foodHistory.size() > getMaxDietRating(player)) {
            foodHistory.removeLast();
        }

        player.setAttached(ModAttachmentTypes.FOOD_HISTORY,
                foodHistory.stream().map(Item::getRegistryEntry).map(reference -> (RegistryEntry<Item>) reference)
                        .toList());
    }

    public static boolean hasMovementBuffs(PlayerEntity player) {
        float ratio = (float) getDietRating(player) / getMaxDietRating(player);
        return ratio >= VitalityConstants.MIN_BUFF_RATIO;
    }

    public static boolean canDoubleJump(PlayerEntity player) {
        return hasMovementBuffs(player) && player.getWorld().getGameRules().get(VitalityGamerules.ALLOW_DOUBLE_JUMP).get();
    }
}
