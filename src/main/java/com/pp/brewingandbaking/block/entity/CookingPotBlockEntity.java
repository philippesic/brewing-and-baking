package com.pp.brewingandbaking.block.entity;

import com.pp.brewingandbaking.MealRecipe;
import com.pp.brewingandbaking.ModBlockEntityTypes;
import com.pp.brewingandbaking.cooking.MealAssembler;
import com.pp.brewingandbaking.inventory.CookingPotMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CookingPotBlockEntity extends BaseContainerBlockEntity {

    private static final Component DEFAULT_NAME = Component.translatable("container.brewingandbaking.cooking_pot");
    protected NonNullList<ItemStack> items;
    protected final ContainerData dataAccess;
    public int heated;
    public int cookingDuration;
    public int cookTotal;

    public CookingPotBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.COOKING_POT_BLOCK_ENTITY.get(), pos, state);
        this.items = NonNullList.withSize(6, ItemStack.EMPTY);

        this.dataAccess = new ContainerData() {
            public int get(final int dataId) {
                return switch (dataId) {
                    case 0 -> heated;
                    case 1 -> cookingDuration;
                    default -> cookTotal;
                };
            }

            public void set(final int dataId, final int value) {
                switch (dataId) {
                    case 0 -> heated = value;
                    case 1 -> cookingDuration = value;
                    case 2 -> cookTotal = value;
                }
            }

            public int getCount() {
                return 3;
            }
        };

    }

    @Override
    protected void loadAdditional(final ValueInput input) {
        super.loadAdditional(input);
        items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, items);
        heated = input.getShortOr("heated", (short) 0);
        cookingDuration = input.getShortOr("cookingDuration", (short) 0);
        cookTotal = input.getShortOr("cookTotal", (short) 0);

    }

    @Override
    protected void saveAdditional(final ValueOutput output) {
        super.saveAdditional(output);
        output.putShort("heated", (short) heated);
        output.putShort("cookingDuration", (short) cookingDuration);
        output.putShort("cookTotal", (short) cookTotal);
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

    public static void serverTick(Level level, BlockPos pos, BlockState state, CookingPotBlockEntity be) {
        if (!(level instanceof ServerLevel server)) {
            return;
        }

        be.heated = level.getBlockState(pos.below()).is(Blocks.LAVA_CAULDRON) ? 1 : 0;

        int prevDuration = be.cookingDuration;
        int prevTotal = be.cookTotal;
        boolean itemsChanged = false;

        List<ItemStack> inputs = new ArrayList<>(be.items.size() - 1);
        for (int slot = 1; slot < be.items.size(); slot++) {
            inputs.add(be.items.get(slot));
        }

        Optional<MealRecipe> match = MealAssembler.findRecipe(inputs);
        if (match.isEmpty()) {
            be.cookingDuration = 0;
            be.cookTotal = 0;
        } else {
            MealRecipe recipe = match.get();
            int cookTime = MealAssembler.cookTime(inputs);
            be.cookTotal = cookTime;

            // Only advance while the output slot can take the finished meal, so a blocked output
            // stalls cooking at the start rather than completing silently in the background.
            boolean canOutput = canAccept(be.items.get(0), MealAssembler.resultItem(recipe));

            if (be.heated == 1 && canOutput) {
                be.cookingDuration++;
            }

            if (canOutput && be.cookingDuration >= cookTime) {
                ItemStack result = MealAssembler.assemble(recipe, inputs, server.getRandom());
                for (int slot = 1; slot < be.items.size(); slot++) {
                    ItemStack ingredient = be.items.get(slot);
                    if (!ingredient.isEmpty()) {
                        ingredient.shrink(1);
                    }
                }
                ItemStack output = be.items.get(0);
                if (output.isEmpty()) {
                    be.items.set(0, result);
                } else {
                    output.grow(result.getCount());
                }
                be.cookingDuration = 0;
                itemsChanged = true;
            }
        }

        if (itemsChanged || be.cookingDuration != prevDuration || be.cookTotal != prevTotal) {
            be.setChanged();
        }
    }

    private static boolean canAccept(ItemStack output, ItemStack result) {
        if (output.isEmpty()) {
            return true;
        }
        if (!ItemStack.isSameItemSameComponents(output, result)) {
            return false;
        }
        return output.getCount() + result.getCount() <= output.getMaxStackSize();
    }

}
