package com.pp.brewingandbaking.cooking;

import com.pp.brewingandbaking.FoodPoints;
import com.pp.brewingandbaking.FoodTag;
import com.pp.brewingandbaking.FoodTagRegistry;
import com.pp.brewingandbaking.MealRecipe;
import com.pp.brewingandbaking.MealRecipeRegistry;
import com.pp.brewingandbaking.ModDataComponents;
import com.pp.brewingandbaking.ModMeals;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

/**
 * Turns the contents of a cooking pot into a finished meal: resolves the meal from the multiset of
 * {@linkplain FoodTag food tags} on the inputs, then derives its food points, luxury tier, and any
 * status effect. Matching is tag-driven (see {@link MealRecipeRegistry}); ingredient identity only
 * affects the numbers baked onto the result.
 */
public final class MealAssembler {
    private MealAssembler() {}

    /** Meal food points = (sum of ingredient food points) x this, plus one per luxury point. */
    private static final double FOOD_POINT_MULTIPLIER = 1.5;

    private static final int BASE_COOK_TICKS = 40;
    private static final int COOK_TICKS_PER_INGREDIENT = 20;

    /** Resolves the meal recipe for the given pot inputs, if any combination matches. */
    public static Optional<MealRecipe> findRecipe(List<ItemStack> inputs) {
        EnumSet<FoodTag> tags = EnumSet.noneOf(FoodTag.class);
        boolean any = false;
        for (ItemStack stack : inputs) {
            if (stack.isEmpty()) {
                continue;
            }
            if (!FoodTagRegistry.INSTANCE.isCookable(stack.getItem())) {
                return Optional.empty();
            }
            tags.addAll(FoodTagRegistry.INSTANCE.getTagsFor(stack.getItem()));
            any = true;
        }
        if (!any) {
            return Optional.empty();
        }
        return MealRecipeRegistry.INSTANCE.resolve(tags);
    }

    /** The meal item this recipe yields, with no per-cook data — used to test output-slot acceptance. */
    public static ItemStack resultItem(MealRecipe recipe) {
        return new ItemStack(ModMeals.get(recipe.result()).get());
    }

    /** Cook time in ticks, scaling with the number of ingredients placed. */
    public static int cookTime(List<ItemStack> inputs) {
        int count = 0;
        for (ItemStack stack : inputs) {
            if (!stack.isEmpty()) {
                count++;
            }
        }
        return BASE_COOK_TICKS + COOK_TICKS_PER_INGREDIENT * count;
    }

    /**
     * Builds the finished meal stack: base name from the recipe, food points derived from the
     * ingredients, and (for luxury cooks) a gold tier and rolled status effect.
     */
    public static ItemStack assemble(MealRecipe recipe, List<ItemStack> inputs, RandomSource random) {
        DeferredItem<Item> meal = ModMeals.get(recipe.result());
        ItemStack result = new ItemStack(meal.get());

        double base = 0.0;
        int luxury = 0;
        for (ItemStack stack : inputs) {
            if (stack.isEmpty()) {
                continue;
            }
            base += FoodPoints.pointsFor(stack);
            luxury += LuxuryEffects.luxuryFor(stack.getItem());
        }
        luxury = Math.min(LuxuryEffects.MAX_LUXURY, luxury);

        if (recipe.poisonous()) {
            result.set(ModDataComponents.FOOD_POINTS.get(), -(base * FOOD_POINT_MULTIPLIER));
            return result;
        }

        result.set(ModDataComponents.FOOD_POINTS.get(), base * FOOD_POINT_MULTIPLIER + luxury);

        if (luxury > 0 && recipe.goldVariant()) {
            result.set(ModDataComponents.LUXURY_POINTS.get(), luxury);
        }

        MobEffectInstance effect = LuxuryEffects.rollEffect(luxury, random);
        if (effect != null) {
            result.set(ModDataComponents.MEAL_EFFECT.get(), effect);
        }

        return result;
    }
}
