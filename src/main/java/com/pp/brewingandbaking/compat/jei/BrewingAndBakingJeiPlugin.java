package com.pp.brewingandbaking.compat.jei;

import com.pp.brewingandbaking.BrewingandBaking;
import com.pp.brewingandbaking.ModBrewing;
import com.pp.brewingandbaking.ModItems;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.vanilla.IJeiBrewingRecipe;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@JeiPlugin
public final class BrewingAndBakingJeiPlugin implements IModPlugin {
    private static final ResourceLocation UID =
            ResourceLocation.fromNamespaceAndPath(BrewingandBaking.MODID, "jei_plugin");
    private static final ResourceLocation COFFEE_BREWING_UID =
            ResourceLocation.fromNamespaceAndPath(BrewingandBaking.MODID, "brewing/coffee");

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        IVanillaRecipeFactory factory = registration.getVanillaRecipeFactory();

        ItemStack water = new ItemStack(Items.POTION);
        water.set(DataComponents.POTION_CONTENTS,
                new PotionContents(Optional.of(Potions.WATER), Optional.empty(), List.of()));

        ItemStack ingredient = new ItemStack(ModItems.ROASTED_COFFEE_BEANS.get());
        ItemStack output = ModBrewing.makeCoffeePotionStack();

        IJeiBrewingRecipe coffeeRecipe = factory.createBrewingRecipe(
                List.of(ingredient),
                water,
                output,
                COFFEE_BREWING_UID
        );

        registration.addRecipes(RecipeTypes.BREWING, List.of(coffeeRecipe));
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime) {
        // Hide the auto-generated brewing recipes that use the coffee potion as an input
        // (gunpowder/dragon's breath/arrow variants we intentionally block in ModBrewing).
        ItemStack coffeePotion = ModBrewing.makeCoffeePotionStack();

        var focusFactory = runtime.getJeiHelpers().getFocusFactory();
        var recipeManager = runtime.getRecipeManager();

        var focus = focusFactory.createFocus(RecipeIngredientRole.INPUT, VanillaTypes.ITEM_STACK, coffeePotion);

        var toHide = new ArrayList<>(recipeManager
                .createRecipeLookup(RecipeTypes.BREWING)
                .limitFocus(List.of(focus))
                .get()
                .toList());

        toHide = new ArrayList<>(new HashSet<>(toHide));

        recipeManager.hideRecipes(RecipeTypes.BREWING, toHide);
    }
}
