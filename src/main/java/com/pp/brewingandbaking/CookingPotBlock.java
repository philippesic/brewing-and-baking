package com.pp.brewingandbaking;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

import java.util.EnumSet;
import java.util.Optional;

public class CookingPotBlock extends BaseEntityBlock {
    public static final MapCodec<CookingPotBlock> CODEC = simpleCodec(CookingPotBlock::new);

    public CookingPotBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CookingPotBlockEntity(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected InteractionResult useItemOn(
            ItemStack itemStack, BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult hitResult) {

        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        if (!FoodTagRegistry.INSTANCE.hasTag(itemStack.getItem())) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        if (!level.isClientSide()) {
            CookingPotBlockEntity be = getBlockEntity(level, pos);
            if (be == null || !be.addIngredient(itemStack)) {
                return InteractionResult.FAIL;
            }
            itemStack.shrink(1);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return InteractionResult.CONSUME;
        }

        CookingPotBlockEntity be = getBlockEntity(level, pos);
        if (be == null || be.isEmpty()) {
            return InteractionResult.PASS;
        }

        EnumSet<FoodTag> tagSet = EnumSet.noneOf(FoodTag.class);
        double rawFoodPoints = 0.0;
        for (ItemStack stack : be.getIngredients()) {
            tagSet.addAll(FoodTagRegistry.INSTANCE.getTagsFor(stack.getItem()));
            rawFoodPoints += FoodPoints.pointsFor(stack);
        }

        if (tagSet.isEmpty()) {
            return InteractionResult.PASS;
        }

        Optional<MealRecipe> recipe = MealRecipeRegistry.INSTANCE.resolve(tagSet);
        if (recipe.isEmpty()) {
            return InteractionResult.PASS;
        }

        var mealItem = ModMeals.get(recipe.get().result());
        if (mealItem == null) {
            return InteractionResult.PASS;
        }

        double foodPoints = rawFoodPoints * 1.5;
        ItemStack result = new ItemStack(mealItem.get());
        result.set(ModDataComponents.FOOD_POINTS.get(), foodPoints);

        Block.popResource(level, pos.above(), result);
        be.clearIngredients();

        return InteractionResult.SUCCESS;
    }

    private static @Nullable CookingPotBlockEntity getBlockEntity(Level level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        return be instanceof CookingPotBlockEntity pot ? pot : null;
    }
}
