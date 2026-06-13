package com.pp.brewingandbaking.inventory;

import com.pp.brewingandbaking.ModMenuTypes;
import com.pp.brewingandbaking.block.entity.CookingPotBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class CookingPotMenu extends AbstractContainerMenu {
    private final Container container;
    protected final Level level;
    private final CookingPotBlockEntity be;
    private final ContainerData data;
    private final BlockPos pos;

    public CookingPotMenu(int containerId, Inventory playerInv, FriendlyByteBuf buf) {
        this(containerId, playerInv, getBE(playerInv, buf), new SimpleContainerData(6));
    }

    public CookingPotMenu(int containerId, Inventory playerInv, CookingPotBlockEntity be, ContainerData data) {
        super(ModMenuTypes.COOKING_POT_MENU.get(), containerId);
        this.be = be;
        this.pos = be.getBlockPos();
        this.container = be;
        this.data = data;
        checkContainerSize(container, 6);
        checkContainerDataCount(data, 2);
        this.level = playerInv.player.level();
        this.addSlot(new CookingPotOutSlot(container, 0, 80, 60));
        this.addSlot(new CookingPotFoodSlot(container, 1, 30, 19));
        this.addSlot(new CookingPotFoodSlot(container, 2, 40, 19));
        this.addSlot(new CookingPotFoodSlot(container, 3, 50, 19));
        this.addSlot(new CookingPotFoodSlot(container, 4, 60, 19));
        this.addSlot(new CookingPotFoodSlot(container, 5, 70, 19));
        this.addStandardInventorySlots(playerInv, 8, 84);
        this.addDataSlots(data);
    }

    private static CookingPotBlockEntity getBE(Inventory playerInv, FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        BlockEntity e = playerInv.player.level().getBlockEntity(pos);
        if (e instanceof CookingPotBlockEntity be) return be;
        throw new IllegalStateException("Expected CookingPotBlockEnity at " + pos + ", got: " + e);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack clicked = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            clicked = stack.copy();
            if (slotIndex != 0 && slotIndex != 1) {
                slot.onTake(player, stack);
            }
        }
        return clicked;

    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }
}
