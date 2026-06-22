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
    public int cooking_duration;
    public int cook_total;

    public CookingPotBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.COOKING_POT_BLOCK_ENTITY.get(), pos, state);
        this.items = NonNullList.withSize(6, ItemStack.EMPTY);

        this.dataAccess = new ContainerData() {
            public int get(final int dataId) {
                if (dataId == 0) {
                    return heated;
                }
                if (dataId == 1) {
                    return cooking_duration;
                }
                return cook_total;
            }

            public void set(final int dataId, final int value) {
                if (dataId == 0) {
                    heated = value;
                }
                if (dataId == 1) {
                    cooking_duration = value;
                }
                if (dataId == 2) {
                    cook_total = value;
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
        cooking_duration = input.getShortOr("cooking_duration", (short) 0);
        cook_total = input.getShortOr("cook_total", (short) 0);

    }

    @Override
    protected void saveAdditional(final ValueOutput output) {
        super.saveAdditional(output);
        output.putShort("heated", (short) heated);
        output.putShort("cooking_duration", (short) cooking_duration);
        output.putShort("cook_total", (short) cook_total);
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

        int prevDuration = be.cooking_duration;
        int prevTotal = be.cook_total;
        boolean itemsChanged = false;

        List<ItemStack> inputs = new ArrayList<>(be.items.size() - 1);
        for (int slot = 1; slot < be.items.size(); slot++) {
            inputs.add(be.items.get(slot));
        }

        Optional<MealRecipe> match = MealAssembler.findRecipe(inputs);
        if (match.isEmpty()) {
            be.cooking_duration = 0;
            be.cook_total = 0;
        } else {
            MealRecipe recipe = match.get();
            int cookTime = MealAssembler.cookTime(inputs);
            be.cook_total = cookTime;

            // Only advance while the output slot can take the finished meal, so a blocked output
            // stalls cooking at the start rather than completing silently in the background.
            boolean canOutput = canAccept(be.items.get(0), MealAssembler.resultItem(recipe));

            if (be.heated == 1 && canOutput) {
                be.cooking_duration++;
            }

            if (canOutput && be.cooking_duration >= cookTime) {
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
                be.cooking_duration = 0;
                itemsChanged = true;
            }
        }

        if (itemsChanged || be.cooking_duration != prevDuration || be.cook_total != prevTotal) {
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
