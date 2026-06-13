package com.pp.brewingandbaking.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class CookingPotFoodSlot extends Slot {

    public CookingPotFoodSlot(final Container container, int slot, int x, int y) {
        super(container, slot, x, y);
    }

    public boolean mayPlace(final ItemStack itemStack) {
        return true;
    }

}
