package com.pp.brewingandbaking.inventory;

import com.pp.brewingandbaking.FoodTagRegistry;
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
    private final ContainerData data;

    public CookingPotMenu(int containerId, Inventory playerInv, FriendlyByteBuf buf) {
        this(containerId, playerInv, getBE(playerInv, buf), new SimpleContainerData(3));
    }

    public CookingPotMenu(int containerId, Inventory playerInv, CookingPotBlockEntity be, ContainerData data) {
        super(ModMenuTypes.COOKING_POT_MENU.get(), containerId);
        this.container = be;
        this.data = data;
        checkContainerSize(container, 6);
        checkContainerDataCount(data, 3);
        this.level = playerInv.player.level();
        this.addSlot(new CookingPotOutSlot(container, 0, 144, 35));
        this.addSlot(new CookingPotFoodSlot(container, 1, 12, 35));
        this.addSlot(new CookingPotFoodSlot(container, 2, 30, 35));
        this.addSlot(new CookingPotFoodSlot(container, 3, 48, 35));
        this.addSlot(new CookingPotFoodSlot(container, 4, 66, 35));
        this.addSlot(new CookingPotFoodSlot(container, 5, 84, 35));
        this.addStandardInventorySlots(playerInv, 8, 84);
        this.addDataSlots(data);
    }

    private static CookingPotBlockEntity getBE(Inventory playerInv, FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        BlockEntity e = playerInv.player.level().getBlockEntity(pos);
        if (e instanceof CookingPotBlockEntity be) return be;
        throw new IllegalStateException("Expected CookingPotBlockEntity at " + pos + ", got: " + e);
    }

    private static final int OUTPUT_SLOT = 0;
    private static final int INPUT_START = 1;
    private static final int INPUT_END = 6;
    private static final int PLAYER_START = 6;
    private static final int HOTBAR_START = 33;
    private static final int PLAYER_END = 42;

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();
            if (slotIndex == OUTPUT_SLOT) {
                if (!this.moveItemStackTo(stack, PLAYER_START, PLAYER_END, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotIndex < INPUT_END) {
                if (!this.moveItemStackTo(stack, PLAYER_START, PLAYER_END, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                boolean moved = FoodTagRegistry.INSTANCE.isCookable(stack.getItem())
                        && this.moveItemStackTo(stack, INPUT_START, INPUT_END, false);
                if (!moved) {
                    if (slotIndex < HOTBAR_START) {
                        if (!this.moveItemStackTo(stack, HOTBAR_START, PLAYER_END, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (!this.moveItemStackTo(stack, PLAYER_START, HOTBAR_START, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }

            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (stack.getCount() == result.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, stack);
        }
        return result;
    }

    public boolean isHeated() {
        return this.data.get(0) != 0;
    }

    /** Cooking progress scaled to {@code pixels} (0 when nothing is cooking). */
    public int getCookProgressScaled(int pixels) {
        int total = this.data.get(2);
        if (total <= 0) {
            return 0;
        }
        return Math.min(this.data.get(1) * pixels / total, pixels);
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }
}
