package com.pp.brewingandbaking;

import java.util.ArrayList;
import java.util.Optional;

import com.pp.brewingandbaking.core.ModBrewing;
import com.pp.brewingandbaking.core.ModItems;
import com.pp.brewingandbaking.core.ModPotions;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.neoforge.common.brewing.IBrewingRecipe;

public final class CoffeeBrewingRecipe implements IBrewingRecipe {

    @Override
    public boolean isInput(ItemStack input) {
        if (!input.is(Items.POTION)) return false;

        PotionContents pc = input.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        return pc.is(Potions.WATER);
    }

    @Override
    public boolean isIngredient(ItemStack ingredient) {
        return ingredient.is(ModItems.ROASTED_COFFEE_BEANS);
    }

    @Override
    public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
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
