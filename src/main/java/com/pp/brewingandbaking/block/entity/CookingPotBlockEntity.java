package com.pp.brewingandbaking.block.entity;

import com.pp.brewingandbaking.ModBlockEntityTypes;
import com.pp.brewingandbaking.inventory.CookingPotMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class CookingPotBlockEntity extends BaseContainerBlockEntity {

    private static final Component DEFAULT_NAME = Component.translatable("container.brewingandbaking.cooking_pot");
    protected NonNullList<ItemStack> items;
    protected final ContainerData dataAccess;
    public int heated;
    public int cooking_duration;

    public CookingPotBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.COOKING_POT_BLOCK_ENTITY.get(), pos, state);
        this.items = NonNullList.withSize(6, ItemStack.EMPTY);

        this.dataAccess = new ContainerData() {
            public int get(final int dataId) {
                if (dataId == 0) {
                    return heated;
                }
                return cooking_duration;
            }

            public void set(final int dataId, final int value) {
                if (dataId == 0) {
                    heated = value;
                }
                if (dataId == 1) {
                    cooking_duration = value;
                }

            }

            public int getCount() {
                return 2;
            }
        };

    }

    @Override
    protected void loadAdditional(final ValueInput input) {
        super.loadAdditional(input);
        items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, items);
        heated = input.getShortOr("heated", (short) 0);
        cooking_duration = input.getShortOr("cooking_duration", (short) 0);

    }

    @Override
    protected void saveAdditional(final ValueOutput output) {
        super.saveAdditional(output);
        output.putShort("heated", (short) heated);
        output.putShort("cooking_duration", (short) cooking_duration);
        ContainerHelper.saveAllItems(output, items);
    }

    @Override
    protected Component getDefaultName() {
        return DEFAULT_NAME;
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    protected AbstractContainerMenu createMenu(final int containerId, Inventory inventory) {
        return new CookingPotMenu(containerId, inventory, this, dataAccess);
    }

    private static int getNumNonEmptyStacks(NonNullList<ItemStack> items) {
        int numNonEmptyStacks = 0;
        for (ItemStack item : items) {
            if (item.isEmpty()) {
                numNonEmptyStacks++;
            }
        }
        return numNonEmptyStacks;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, CookingPotBlockEntity be) {
        be.heated = level.getBlockState(pos.below()).is(Blocks.LAVA_CAULDRON) ? 1 : 0;
    }

}
