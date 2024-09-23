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

package com.khjxiaogu.convivium.blocks.pestle_and_mortar;

import com.khjxiaogu.convivium.CVBlockEntityTypes;
import com.khjxiaogu.convivium.blocks.kinetics.KineticBasedBlock;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public class PamBlock extends KineticBasedBlock<PamBlockEntity> {

	public PamBlock(Properties p_54120_) {
		super(CVBlockEntityTypes.PAM, p_54120_);
		// TODO Auto-generated constructor stub
	}

	static final VoxelShape shape = Shapes.or(Block.box(0, 0, 0, 16, 9, 16), Block.box(3, 9, 3, 13, 14, 13), Block.box(6, 14, 6, 10, 16, 10));

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return shape;
	}

	@Override
	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!(newState.getBlock() instanceof PamBlock)) {
			if (worldIn.getBlockEntity(pos) instanceof PamBlockEntity dish) {
				for (int i = 0; i < dish.inv.getSlots(); i++) {
					super.popResource(worldIn, pos, dish.inv.getStackInSlot(i));
				}
			}
			worldIn.removeBlockEntity(pos);
		}
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		InteractionResult p = super.useWithoutItem(state, level, pos, player, hitResult);
		if (p.consumesAction())
			return p;
		BlockEntity be = level.getBlockEntity(pos);
		if (be instanceof PamBlockEntity pam) {
			if (!level.isClientSide)
				player.openMenu(pam, pam.getBlockPos());
			return InteractionResult.sidedSuccess(level.isClientSide);
		}
		return p;
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		ItemInteractionResult p = super.useItemOn(held, state, level, pos, player, hand, hitResult);
		if (p.consumesAction())
			return p;
		BlockEntity be = level.getBlockEntity(pos);
		if (be instanceof PamBlockEntity pam) {
			FluidStack out = Utils.extractFluid(held);
			if (!out.isEmpty()) {
				if (pam.tankin.fill(out, FluidAction.SIMULATE) == out.getAmount()) {
					pam.tankin.fill(out, FluidAction.EXECUTE);
					ItemStack ret = held.getCraftingRemainingItem();
					held.shrink(1);
					ItemHandlerHelper.giveItemToPlayer(player, ret);
					return ItemInteractionResult.sidedSuccess(level.isClientSide);
				}
			}
			if (FluidUtil.interactWithFluidHandler(player, hand, pam.tanks))
				return ItemInteractionResult.SUCCESS;
		}

		return p;
	}
}
