package com.pp.brewingandbaking;

import java.util.ArrayList;
import java.util.Optional;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.neoforge.common.brewing.IBrewingRecipe;
import org.jspecify.annotations.NonNull;

public final class CoffeeBrewingRecipe implements IBrewingRecipe {
    @Override
    public boolean isInput(ItemStack input) {
        if (!input.is(Items.POTION)) return false;

        PotionContents pc = input.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        return pc.potion().isPresent() && pc.potion().get().is(Potions.WATER.key());
    }

    @Override
    public boolean isIngredient(ItemStack ingredient) {
        return ingredient.is(ModItems.ROASTED_COFFEE_BEANS.get());
    }

    @Override
    public @NonNull ItemStack getOutput(@NonNull ItemStack input, @NonNull ItemStack ingredient) {
        if (!isInput(input) || !isIngredient(ingredient)) return ItemStack.EMPTY;

        ItemStack out = new ItemStack(Items.POTION);

        PotionContents contents = new PotionContents(
                Optional.of(ModPotions.COFFEE),           // Holder<Potion>
                Optional.of(ModBrewing.COFFEE_COLOR),      // brown tint override
                new ArrayList<>(),
                Optional.empty()
        );

        out.set(() -> DataComponents.POTION_CONTENTS, contents);
        return out;
    }
}
