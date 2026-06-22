package com.pp.brewingandbaking.compat.jei;

import com.pp.brewingandbaking.BrewingandBaking;
import com.pp.brewingandbaking.FoodTag;
import com.pp.brewingandbaking.FoodTagRegistry;
import com.pp.brewingandbaking.MealRecipe;
import com.pp.brewingandbaking.MealRecipeRegistry;
import com.pp.brewingandbaking.ModBrewing;
import com.pp.brewingandbaking.ModItems;
import com.pp.brewingandbaking.ModMeals;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.vanilla.IJeiBrewingRecipe;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@JeiPlugin
public final class BrewingAndBakingJeiPlugin implements IModPlugin {
    private static final Identifier UID =
            Identifier.fromNamespaceAndPath(BrewingandBaking.MODID, "jei_plugin");
    private static final Identifier COFFEE_BREWING_UID =
            Identifier.fromNamespaceAndPath(BrewingandBaking.MODID, "brewing/coffee");

    @Override
    public Identifier getPluginUid() {
        return UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new CookingPotRecipeCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registerCoffeeBrewingRecipe(registration);
        registerCookingPotRecipes(registration);
    }

    private static void registerCoffeeBrewingRecipe(IRecipeRegistration registration) {
        IVanillaRecipeFactory factory = registration.getVanillaRecipeFactory();

        ItemStack water = new ItemStack(Items.POTION);
        water.set(() -> DataComponents.POTION_CONTENTS,
                new PotionContents(
                        Optional.of(Potions.WATER),
                        Optional.empty(),
                        List.of(),
                        Optional.empty()
                )
        );

        IJeiBrewingRecipe coffeeRecipe = factory.createBrewingRecipe(
                List.of(new ItemStack(ModItems.ROASTED_COFFEE_BEANS.get())),
                water,
                ModBrewing.makeCoffeePotionStack(),
                COFFEE_BREWING_UID
        );

        registration.addRecipes(RecipeTypes.BREWING, List.of(coffeeRecipe));
    }

    private static void registerCookingPotRecipes(IRecipeRegistration registration) {
        List<CookingPotJeiRecipe> recipes = buildCookingPotRecipes();
        if (!recipes.isEmpty()) {
            registration.addRecipes(CookingPotRecipeCategory.RECIPE_TYPE, recipes);
        }
    }

    private static List<CookingPotJeiRecipe> buildCookingPotRecipes() {
        Map<FoodTag, List<ItemStack>> tagItems = new HashMap<>();
        for (FoodTag tag : FoodTag.values()) {
            List<ItemStack> stacks = FoodTagRegistry.INSTANCE.getItemsWithTag(tag)
                    .stream()
                    .map(ItemStack::new)
                    .toList();
            tagItems.put(tag, stacks);
        }

        List<CookingPotJeiRecipe> result = new ArrayList<>();

        for (MealRecipe recipe : MealRecipeRegistry.INSTANCE.getAll()) {
            if (recipe.junkOverride()) {
                continue;
            }

            DeferredItem<Item> mealItem = ModMeals.get(recipe.result());
            if (mealItem == null) {
                continue;
            }
            ItemStack output = new ItemStack(mealItem.get());

            for (EnumSet<FoodTag> combination : recipe.combinations()) {
                List<List<ItemStack>> inputSlots = new ArrayList<>();
                for (FoodTag tag : combination) {
                    List<ItemStack> items = tagItems.get(tag);
                    if (items != null && !items.isEmpty()) {
                        inputSlots.add(items);
                    }
                }
                if (!inputSlots.isEmpty()) {
                    result.add(new CookingPotJeiRecipe(inputSlots, output));
                }
            }
        }

        MealRecipeRegistry.INSTANCE.getJunkOverride().ifPresent(junkRecipe -> {
            DeferredItem<Item> mealItem = ModMeals.get(junkRecipe.result());
            if (mealItem == null) {
                return;
            }
            List<ItemStack> junkItems = tagItems.getOrDefault(FoodTag.JUNK, List.of());
            if (!junkItems.isEmpty()) {
                result.add(new CookingPotJeiRecipe(List.of(junkItems), new ItemStack(mealItem.get())));
            }
        });

        return result;
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime) {
        ItemStack coffeePotion = ModBrewing.makeCoffeePotionStack();

        var focusFactory = runtime.getJeiHelpers().getFocusFactory();
        var recipeManager = runtime.getRecipeManager();

        var focus = focusFactory.createFocus(RecipeIngredientRole.INPUT, VanillaTypes.ITEM_STACK, coffeePotion);

        var toHide = new ArrayList<>(recipeManager
                .createRecipeLookup(RecipeTypes.BREWING)
                .limitFocus(List.of(focus))
                .get()
                .toList());

        recipeManager.hideRecipes(RecipeTypes.BREWING, new ArrayList<>(new HashSet<>(toHide)));
    }
}
