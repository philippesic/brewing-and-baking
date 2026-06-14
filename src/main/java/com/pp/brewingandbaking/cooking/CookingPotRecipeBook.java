package com.pp.brewingandbaking.cooking;

import com.pp.brewingandbaking.ModRecipes;
import com.pp.brewingandbaking.recipe.CookingPotRecipe;
import com.pp.brewingandbaking.recipe.CookingPotRecipeInput;
import net.minecraft.server.level.ServerLevel;

import java.util.Optional;

/**
 * Thin server-side lookup for cooking pot recipes. Delegates to the vanilla recipe manager, which
 * pre-filters candidates to {@link ModRecipes#COOKING_TYPE} and runs {@link CookingPotRecipe#matches}
 * (a tag-multiset comparison backed by {@link CookingIngredients}'s O(1) item→tag index). No custom
 * caching is needed: only the few cooking recipes are ever tested, and reloads are handled by the
 * recipe manager itself.
 */
public final class CookingPotRecipeBook {
    private CookingPotRecipeBook() {
    }

    public static Optional<CookingPotRecipe> find(ServerLevel level, CookingPotRecipeInput input) {
        return level.recipeAccess()
                .getRecipeFor(ModRecipes.COOKING_TYPE.get(), input, level)
                .map(holder -> holder.value());
    }
}
