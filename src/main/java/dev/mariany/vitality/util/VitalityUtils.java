package dev.mariany.vitality.util;

import dev.mariany.vitality.Vitality;
import dev.mariany.vitality.attachment.VitalityAttachmentTypes;
import dev.mariany.vitality.tag.VitalityTags;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.Difficulty;

import java.util.LinkedList;
import java.util.List;

public class VitalityUtils {
    public static List<Item> getFoodHistory(LivingEntity entity) {
        List<Item> items = new LinkedList<>();
        if (entity.hasAttached(VitalityAttachmentTypes.FOOD_HISTORY)) {
            List<RegistryEntry<Item>> entries = entity.getAttachedOrElse(
                    VitalityAttachmentTypes.FOOD_HISTORY,
                    new LinkedList<>()
            );
            items.addAll(entries.stream().map(RegistryEntry::value).toList());
        }
        return items;
    }

    public static int getMaxDietRating() {
        return Vitality.CONFIG.healthyEatingWindow();
    }

    public static int getDietRating(LivingEntity entity) {
        List<Item> foodHistory = VitalityUtils.getFoodHistory(entity);
        int foodHistorySize = foodHistory.size();
        int maxDietRating = getMaxDietRating();

        if (entity.getEntityWorld().getDifficulty().equals(Difficulty.PEACEFUL)) {
            return maxDietRating;
        }

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
        boolean hasMovementBuffs = hasMovementBuffs(player);

        List<Item> foodHistory = VitalityUtils.getFoodHistory(player);
        foodHistory.addFirst(stack.getItem());

        if (foodHistory.size() > getMaxDietRating()) {
            foodHistory.removeLast();
        }

        player.setAttached(
                VitalityAttachmentTypes.FOOD_HISTORY,
                foodHistory.stream().map(Item::getRegistryEntry).map(reference -> (RegistryEntry<Item>) reference)
                        .toList()
        );


        if (Vitality.CONFIG.regenerationFromImprovedDiet() && !hasMovementBuffs && hasMovementBuffs(player)) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 20 * 5, 0, false, true, true));
        }
    }

    public static boolean hasMovementBuffs(PlayerEntity player) {
        float ratio = (float) getDietRating(player) / getMaxDietRating();
        return ratio >= VitalityConstants.MIN_BUFF_RATIO && player.canSprintAsVehicle();
    }

    public static boolean canWallJump(PlayerEntity player) {
        return hasMovementBuffs(player) && Vitality.CONFIG.allowWallJump() && areGeneralConditionsMet(player);
    }

    public static boolean canDoubleJump(PlayerEntity player) {
        return hasMovementBuffs(player) && Vitality.CONFIG.allowDoubleJump() && areGeneralConditionsMet(player);
    }

    public static boolean canSoftLand(PlayerEntity player) {
        return hasMovementBuffs(player) && Vitality.CONFIG.allowSoftLand() && areGeneralConditionsMet(player);
    }

    private static boolean isHungerSatisfied(PlayerEntity player) {
        return player.getHungerManager().getFoodLevel() > 6F;
    }

    private static boolean areGeneralConditionsMet(PlayerEntity player) {
        PlayerAbilities abilities = player.getAbilities();

        if (abilities.flying || abilities.allowFlying) {
            return false;
        }

        if (player.isSpectator()) {
            return false;
        }

        if (player.hasVehicle()) {
            return false;
        }

        if (player.isInFluid()) {
            return false;
        }

        if (player.inPowderSnow) {
            return false;
        }

        if (player.isCrawling()) {
            return false;
        }

        if (player.isGliding()) {
            return false;
        }

        if (player.isUsingRiptide()) {
            return false;
        }

        if (player.isClimbing()) {
            return false;
        }

        if (player.hasStatusEffect(StatusEffects.LEVITATION)) {
            return false;
        }

        return isHungerSatisfied(player);
    }

    public static void exhaust(PlayerEntity player, float min, float max) {
        HungerManager hungerManager = player.getHungerManager();

        float exhaustion = min;

        if (hungerManager.getFoodLevel() >= 18) {
            exhaustion = max;
        }

        hungerManager.addExhaustion(exhaustion);
    }
}
