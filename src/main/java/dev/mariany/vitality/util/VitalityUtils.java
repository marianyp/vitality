package dev.mariany.vitality.util;

import dev.mariany.vitality.attachment.ModAttachmentTypes;
import dev.mariany.vitality.gamerule.VitalityGamerules;
import dev.mariany.vitality.tag.VitalityTags;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;

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

        if (entity.getWorld().getDifficulty().equals(Difficulty.PEACEFUL)) {
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

        if (foodHistory.size() > getMaxDietRating(player)) {
            foodHistory.removeLast();
        }

        player.setAttached(ModAttachmentTypes.FOOD_HISTORY,
                foodHistory.stream().map(Item::getRegistryEntry).map(reference -> (RegistryEntry<Item>) reference)
                        .toList());

        if (!player.getWorld().isClient) {
            if (!hasMovementBuffs && hasMovementBuffs(player)) {
                player.addStatusEffect(
                        new StatusEffectInstance(StatusEffects.REGENERATION, 20 * 5, 0, false, true, true));
            }
        }
    }

    public static boolean hasMovementBuffs(PlayerEntity player) {
        float ratio = (float) getDietRating(player) / getMaxDietRating(player);
        return ratio >= VitalityConstants.MIN_BUFF_RATIO;
    }

    public static boolean canWallJump(PlayerEntity player) {
        return hasMovementBuffs(player) && player.getWorld().getGameRules().get(VitalityGamerules.ALLOW_WALL_JUMP)
                .get();
    }

    public static boolean canDoubleJump(PlayerEntity player) {
        return hasMovementBuffs(player) && player.getWorld().getGameRules().get(VitalityGamerules.ALLOW_DOUBLE_JUMP)
                .get();
    }

    public static Vec3d slerp(Vec3d a, Vec3d b, float t) {
        a = a.normalize();
        b = b.normalize();

        double dot = MathHelper.clamp(a.dotProduct(b), -1.0, 1.0);
        double theta = Math.acos(dot) * t;

        Vec3d relativeVec = b.subtract(a.multiply(dot)).normalize();
        return a.multiply(Math.cos(theta)).add(relativeVec.multiply(Math.sin(theta)));
    }

    public static double angleWithSignBetween(Vec3d a, Vec3d b, Vec3d planeNormal) {
        a = a.normalize();
        b = b.normalize();

        double cosineTheta = MathHelper.clamp(a.dotProduct(b), -1, 1);
        double angle = Math.toDegrees(Math.acos(cosineTheta));
        Vec3d cross = a.crossProduct(b);
        double sign = cross.dotProduct(planeNormal);

        return angle * Math.signum(sign);
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
