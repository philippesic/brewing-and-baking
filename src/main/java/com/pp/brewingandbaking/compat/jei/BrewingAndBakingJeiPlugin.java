package com.pp.brewingandbaking.compat.jei;

import com.pp.brewingandbaking.core.BrewingandBaking;
import com.pp.brewingandbaking.core.ModBrewing;
import com.pp.brewingandbaking.core.ModItems;
import com.pp.brewingandbaking.core.ModPotions;

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
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
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
private static final Identifier UID =
    Identifier.fromNamespaceAndPath(BrewingandBaking.MODID, "jei_plugin");

    // UID
    private static final Identifier COFFEE_BREWING_UID =
            Identifier.fromNamespaceAndPath(BrewingandBaking.MODID, "brewing/coffee");

    @Override
    public Identifier getPluginUid() {
        return UID;
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        IVanillaRecipeFactory factory = registration.getVanillaRecipeFactory();

        // Water bottle input
        ItemStack water = new ItemStack(Items.POTION);
        water.set(() -> DataComponents.POTION_CONTENTS,
                new PotionContents(
                        Optional.of(Potions.WATER),
                        Optional.empty(),
                        List.of(),
                        Optional.empty()
                )
        );

        // Ingredient
        ItemStack ingredient = new ItemStack(ModItems.ROASTED_COFFEE_BEANS.get());

        // Output
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
        ItemStack coffeePotion = ModBrewing.makeCoffeePotionStack();

        var focusFactory = runtime.getJeiHelpers().getFocusFactory();
        var recipeManager = runtime.getRecipeManager();

        // Find every brewing recipe where coffeePotion is an input
        var focus = focusFactory.createFocus(RecipeIngredientRole.INPUT, VanillaTypes.ITEM_STACK, coffeePotion);

        var toHide = new ArrayList<>(recipeManager
            .createRecipeLookup(RecipeTypes.BREWING)
            .limitFocus(java.util.List.of(focus))
            .get()
            .toList());

        // Dedup
        toHide = new ArrayList<>(new HashSet<>(toHide));

        recipeManager.hideRecipes(RecipeTypes.BREWING, toHide);
    }

    private static ItemStack makeCoffeeStackOf(Item baseItem) { // Keep for JEI debugging
        ItemStack stack = new ItemStack(baseItem);

        PotionContents contents = new PotionContents(
                Optional.of(ModPotions.COFFEE),
                Optional.of(ModBrewing.COFFEE_COLOR),
                List.of(),
                Optional.empty()
        );

        stack.set(() -> DataComponents.POTION_CONTENTS, contents);
        return stack;
    }
}
