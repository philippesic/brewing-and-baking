package com.pp.brewingandbaking.compat.jei;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public record CookingPotJeiRecipe(List<List<ItemStack>> inputSlots, ItemStack output) {}
