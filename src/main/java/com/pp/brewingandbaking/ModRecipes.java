package com.pp.brewingandbaking;

import com.pp.brewingandbaking.recipe.CookingPotRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModRecipes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, BrewingandBaking.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, BrewingandBaking.MODID);

    public static final Supplier<RecipeType<CookingPotRecipe>> COOKING_TYPE =
            RECIPE_TYPES.register("cooking", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return BrewingandBaking.MODID + ":cooking";
                }
            });

    public static final Supplier<RecipeSerializer<CookingPotRecipe>> COOKING_SERIALIZER =
            RECIPE_SERIALIZERS.register("cooking",
                    () -> new RecipeSerializer<>(CookingPotRecipe.CODEC, CookingPotRecipe.STREAM_CODEC));
}
