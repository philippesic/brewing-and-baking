package com.pp.brewingandbaking.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.List;

/**
 * Wraps the cooking pot's input slots for recipe matching. Order does not matter for
 * cooking pot recipes — matching is done on the multiset of ingredient tags (see
 * {@link com.pp.brewingandbaking.cooking.CookingIngredients}) — but {@link RecipeInput}
 * still requires indexed access.
 */
public record CookingPotRecipeInput(List<ItemStack> items) implements RecipeInput {
    @Override
    public ItemStack getItem(int index) {
        return items.get(index);
    }

    @Override
    public int size() {
        return items.size();
    }
}
