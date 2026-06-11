/*
 * Copyright (c) 2024 IEEM Trivium Society/khjxiaogu
 *
 * This file is part of Convivium.
 *
 * Convivium is free software: you can redistribute it and/or modify
 * it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 * the Free Software Foundation, version 3.
 *
 * Convivium is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 * You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 * along with Convivium. If not, see <https://www.gnu.org/licenses/>.
 */

package com.khjxiaogu.convivium.blocks.vending;

import java.util.List;

import com.khjxiaogu.convivium.CVBlockEntityTypes;
import com.khjxiaogu.convivium.CVTags;
import com.teammoeg.caupona.blocks.CPHorizontalEntityBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class BeverageVendingBlock extends CPHorizontalEntityBlock<BeverageVendingBlockEntity> {
	public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

	public BeverageVendingBlock(Properties p_54120_) {
		super(CVBlockEntityTypes.BEVERAGE_VENDING_MACHINE, p_54120_);
		this.registerDefaultState(this.defaultBlockState().setValue(ACTIVE, true));
	}

	static final VoxelShape shape = Block.box(1, 0, 1, 15, 15, 15);

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		// TODO Auto-generated method stub
		super.createBlockStateDefinition(builder);
		builder.add(ACTIVE);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return shape;
	}

	@Override
	public float getDestroyProgress(BlockState pState, Player player, BlockGetter worldIn, BlockPos pos) {
		if (worldIn.getBlockEntity(pos) instanceof BeverageVendingBlockEntity blockEntity) {
			if (player.getAbilities().instabuild || player.getUUID().equals(blockEntity.owner))
				return super.getDestroyProgress(pState, player, worldIn, pos);
			return 0;
		}
		return super.getDestroyProgress(pState, player, worldIn, pos);
	}

	@Override
	public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
		super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
		if (pLevel.getBlockEntity(pPos) instanceof BeverageVendingBlockEntity dish) {
			dish.owner = pPlacer.getUUID();
		}
	}

	@Override
	protected List<ItemStack> getDrops(BlockState p_state, LootParams.Builder p_params) {
		List<ItemStack> list = super.getDrops(p_state, p_params);
		if (p_params.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof BeverageVendingBlockEntity dish) {
			for (int i = 0; i < dish.storage.size(); i++) {
				list.add(dish.storage.getResource(i).toStack(dish.storage.getAmountAsInt(i)));
			}
		}
		return list;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());

	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState pState) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState pState, Level pLevel, BlockPos pPos, Direction dir) {
		if (pLevel.getBlockEntity(pPos) instanceof BeverageVendingBlockEntity dish) {
			int sign = dish.tank.getAmountAsInt(0) / 250;
			return Math.min(sign, 15);
		}

		return 0;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		InteractionResult p = super.useWithoutItem(state, level, pos, player, hitResult);
		if (p.consumesAction())
			return p;
		if (level.getBlockEntity(pos) instanceof BeverageVendingBlockEntity blockEntity) {
			if (player.getUUID().equals(blockEntity.owner)) {
				if (!level.isClientSide())
					player.openMenu(blockEntity, blockEntity.getBlockPos());
				return InteractionResult.SUCCESS;
			}
			return InteractionResult.FAIL;
		}
		return p;
	}

	@Override
	protected InteractionResult useItemOn(ItemStack held, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		InteractionResult p = super.useItemOn(held, state, level, pos, player, hand, hitResult);
		if (p.consumesAction())
			return p;
		if (level.getBlockEntity(pos) instanceof BeverageVendingBlockEntity blockEntity) {
			if (player.getUUID().equals(blockEntity.owner)) {
				if (FluidUtil.interactWithFluidHandler(player, hand, pos, blockEntity.tank))
					return InteractionResult.SUCCESS;
			}
			if (state.getValue(ACTIVE)) {
				if (FluidUtil.interactWithFluidHandler(player, hand, pos, blockEntity.handler))
					return InteractionResult.SUCCESS;
			} else {
				if (held.is(CVTags.Items.ASSES) && held.getCount() >= blockEntity.amt && blockEntity.tank.getAmountAsInt(0) >= 250) {
					if (!level.isClientSide()) {
						try (Transaction trans = Transaction.openRoot()) {
							ItemResource asses = ItemResource.of(held);
							if (blockEntity.storage.insert(asses, blockEntity.amt, trans) == blockEntity.amt) {
								held.shrink(blockEntity.amt);
								if (!blockEntity.isInfinite) {
									trans.commit();
								}
								level.setBlockAndUpdate(pos, state.setValue(ACTIVE, true));
							}
						}
					}
					return InteractionResult.SUCCESS;
				}
			}
		}
		return p;
	}
}
