package com.pp.brewingandbaking;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.block.Block;

public class CoffeePlantBlock extends BushBlock { // BushBlock extends bonemealability of BoneMealableBlock
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 3);
    private static final VoxelShape SHAPE_STAGE_0; // defined in static below
    private static final VoxelShape SHAPE_STAGE_1;
    private static final VoxelShape SHAPE_DEFAULT;

    public CoffeePlantBlock(BlockBehaviour.Properties props) {
        super(props);
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> b) {
        b.add(AGE);
    }

    @Override
    protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
        VoxelShape shape;
        switch (state.getValue(AGE)) {
            case 0 -> shape = SHAPE_STAGE_0;
            case 1 -> shape = SHAPE_STAGE_1;
            default -> shape = SHAPE_DEFAULT;
        }

        return shape;
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return state.getValue(AGE) < 3;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
        int age = state.getValue(AGE);
        if (age < 3 && rand.nextInt(5) == 0) {
            level.setBlock(pos, state.setValue(AGE, age + 1), 2);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(state));
        }
    }

    @Override
    public InteractionResult useItemOn(ItemStack held, BlockState state, Level level, BlockPos pos,
                                       Player player, InteractionHand hand, BlockHitResult hit) {
        int age = state.getValue(AGE);

        // Harvest when mature
        if (age >= 3) {
            int dropCount = 1 + level.random.nextInt(2); // 1–2 beans
            popResource(level, pos, new ItemStack(ModItems.COFFEE_BEANS.get(), dropCount));
            level.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + level.random.nextFloat() * 0.4F);

            // Reset age
            level.setBlock(pos, state.setValue(AGE, 2), 2); //Check
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    protected boolean mayPlaceOn(BlockState below, BlockGetter level, BlockPos pos) {
        return below.is(BlockTags.DIRT) || below.is(BlockTags.SAND) || below.is(net.minecraft.world.level.block.Blocks.FARMLAND);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos belowPos = pos.below();
        return mayPlaceOn(level.getBlockState(belowPos), (BlockGetter) level, belowPos);
    }

    @Override
public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
    return state.getValue(AGE) < 3;
}

@Override
public boolean isBonemealSuccess(Level level, RandomSource rand, BlockPos pos, BlockState state) {
    return true;
}

@Override
public void performBonemeal(ServerLevel level, RandomSource rand, BlockPos pos, BlockState state) {
    int age = state.getValue(AGE);
    int inc = 1 + rand.nextInt(1); // Should randomize normal growth on bonemeal
    int next = Math.min(3, age + inc);
    level.setBlock(pos, state.setValue(AGE, next), 2);
}

static {
    SHAPE_STAGE_0 = Block.column(10.0D, 0.0D, 11.0D);
    SHAPE_STAGE_1 = Block.column(12.0D, 0.0D, 14.0D);
    SHAPE_DEFAULT = Block.column(14.0D, 0.0D, 14.0D);
}

}