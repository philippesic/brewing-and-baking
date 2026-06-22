package com.pp.brewingandbaking.block;

import com.pp.brewingandbaking.ModBlockEntityTypes;
import com.pp.brewingandbaking.block.entity.CookingPotBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class CookingPotBlock extends Block implements EntityBlock {
    public CookingPotBlock(Properties props) {
        super(props);
    }

    @Override
    protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
        return Block.column(14, 0, 7);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new CookingPotBlockEntity(blockPos, blockState);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide() && player instanceof ServerPlayer sp) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof CookingPotBlockEntity cookingPot) {
                sp.openMenu(cookingPot, buf -> buf.writeBlockPos(pos));
            }
        }

        return level.isClientSide()
                ? InteractionResult.SUCCESS
                : InteractionResult.SUCCESS_SERVER;
    }

    @SuppressWarnings("unchecked")
    public static <E extends BlockEntity, A extends BlockEntity> @Nullable BlockEntityTicker<A> createTickerHelper(
            BlockEntityType<A> type, BlockEntityType<E> checkedType, BlockEntityTicker<? super E> ticker
    ) {
        return checkedType == type ? (BlockEntityTicker<A>) ticker : null;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntityTypes.COOKING_POT_BLOCK_ENTITY.get(), CookingPotBlockEntity::serverTick);
    }

}
