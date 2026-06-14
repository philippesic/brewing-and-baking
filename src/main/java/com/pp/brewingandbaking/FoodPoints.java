package com.pp.brewingandbaking;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.core.component.DataComponents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Food point values for the health-regen hunger replacement.
 *
 * 1 food point = 1/2 heart (= 1.0 health). Fractional points are a chance to
 * grant an additional 1/2 heart. Values mirror the "Base Food Point Values"
 * table in food-tags.md; unlisted edibles fall back to a fraction of their
 * vanilla nutrition.
 */
public final class FoodPoints {
    private FoodPoints() {}

    /** Health granted per 1/2 heart awarded. */
    private static final float HEALTH_PER_HALF_HEART = 1.0F;

    /** Fallback for unlisted foods: vanilla nutrition scaled down, capped low. */
    private static final double FALLBACK_DIVISOR = 8.0;
    private static final double FALLBACK_CAP = 1.5;

    private static final Map<Item, Double> VALUES = new HashMap<>();

    static {
        // Raw meats (cooking ingredients, weak eaten raw)
        VALUES.put(Items.BEEF, 1.0);
        VALUES.put(Items.PORKCHOP, 1.0);
        VALUES.put(Items.RABBIT, 0.5);
        VALUES.put(Items.MUTTON, 0.5);
        VALUES.put(Items.CHICKEN, 0.5);

        // Cooked meats (~1.5x raw, capped at 1.5)
        VALUES.put(Items.COOKED_BEEF, 1.5);
        VALUES.put(Items.COOKED_PORKCHOP, 1.5);
        VALUES.put(Items.COOKED_RABBIT, 0.75);
        VALUES.put(Items.COOKED_MUTTON, 0.75);
        VALUES.put(Items.COOKED_CHICKEN, 0.75);

        // Fish (abundant, kept low)
        VALUES.put(Items.COD, 0.5);
        VALUES.put(Items.SALMON, 0.5);
        VALUES.put(Items.COOKED_COD, 0.75);
        VALUES.put(Items.COOKED_SALMON, 0.75);
        VALUES.put(Items.TROPICAL_FISH, 0.25);

        // Vegetables
        VALUES.put(Items.CARROT, 0.75);
        VALUES.put(Items.POTATO, 0.5);
        VALUES.put(Items.BEETROOT, 0.25);
        VALUES.put(Items.BAKED_POTATO, 0.75);

        // Fruit (abundant, deliberately tiny)
        VALUES.put(Items.APPLE, 0.5);
        VALUES.put(Items.CHORUS_FRUIT, 0.5);
        VALUES.put(Items.MELON_SLICE, 0.25);
        VALUES.put(Items.SWEET_BERRIES, 0.25);
        VALUES.put(Items.GLOW_BERRIES, 0.25);

        // Grain / sweetener / processed staples
        VALUES.put(Items.BREAD, 0.5);
        VALUES.put(Items.HONEY_BOTTLE, 0.5);
        VALUES.put(Items.COOKIE, 0.25);
        VALUES.put(Items.DRIED_KELP, 0.25);

        // Junk (poor / risky)
        VALUES.put(Items.ROTTEN_FLESH, 0.5);
        VALUES.put(Items.SPIDER_EYE, 0.25);
        VALUES.put(Items.POISONOUS_POTATO, 0.25);
        VALUES.put(Items.PUFFERFISH, 0.25);

        // Vanilla composite meals (nerfed like trivially-cooked food)
        VALUES.put(Items.RABBIT_STEW, 1.0);
        VALUES.put(Items.BEETROOT_SOUP, 0.75);
        VALUES.put(Items.MUSHROOM_STEW, 0.75);
        VALUES.put(Items.SUSPICIOUS_STEW, 0.75);
        VALUES.put(Items.PUMPKIN_PIE, 0.75);
        VALUES.put(Items.CAKE, 0.25);

        // Luxury (gold-gated tier, above the plain cap)
        VALUES.put(Items.GOLDEN_CARROT, 1.5);
        VALUES.put(Items.GOLDEN_APPLE, 2.0);
        VALUES.put(Items.ENCHANTED_GOLDEN_APPLE, 3.0);
    }

    /** Food point value for a stack: per-stack component, else table entry, else scaled vanilla nutrition, else 0. */
    public static double pointsFor(ItemStack stack) {
        Double assigned = stack.get(ModDataComponents.FOOD_POINTS.get());
        if (assigned != null) {
            return assigned;
        }
        Double explicit = VALUES.get(stack.getItem());
        if (explicit != null) {
            return explicit;
        }
        FoodProperties food = stack.get(DataComponents.FOOD);
        if (food != null) {
            return Math.min(food.nutrition() / FALLBACK_DIVISOR, FALLBACK_CAP);
        }
        return 0.0;
    }

    /**
     * Rolls the health to grant for a food point value. The whole part is
     * awarded outright; the fractional part is a chance for one more 1/2 heart.
     */
    public static float rollHealth(double points, RandomSource random) {
        int halfHearts = solidHalfHearts(points);
        if (hasChanceHalfHeart(points) && random.nextDouble() < (points - halfHearts)) {
            halfHearts++;
        }
        return halfHearts * HEALTH_PER_HALF_HEART;
    }

    /** Guaranteed 1/2 hearts (the whole part of the food point value). */
    public static int solidHalfHearts(double points) {
        return (int) Math.floor(points);
    }

    /** Whether there is a fractional 1/2 heart awarded by chance. */
    public static boolean hasChanceHalfHeart(double points) {
        return points - Math.floor(points) > 0.0;
    }
}
