package com.pp.brewingandbaking;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CookingPotBlockEntity extends BlockEntity {
    public static final int MAX_INGREDIENTS = 9;

    private final List<ItemStack> ingredients = new ArrayList<>();

    public CookingPotBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COOKING_POT.get(), pos, state);
    }

    public boolean addIngredient(ItemStack stack) {
        if (ingredients.size() >= MAX_INGREDIENTS) {
            return false;
        }
        ingredients.add(stack.copyWithCount(1));
        setChanged();
        return true;
    }

    public List<ItemStack> getIngredients() {
        return Collections.unmodifiableList(ingredients);
    }

    public void clearIngredients() {
        ingredients.clear();
        setChanged();
    }

    public boolean isEmpty() {
        return ingredients.isEmpty();
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        ingredients.clear();
        input.listOrEmpty("ingredients", ItemStack.CODEC).forEach(ingredients::add);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        var list = output.list("ingredients", ItemStack.CODEC);
        for (ItemStack stack : ingredients) {
            list.add(stack);
        }
    }
}
