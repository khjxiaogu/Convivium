/*
 * Copyright (c) 2023 IEEM Trivium Society/khjxiaogu
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

package com.khjxiaogu.convivium.blocks.basin;

import com.khjxiaogu.convivium.CVBlockEntityTypes;
import com.khjxiaogu.convivium.blocks.kinetics.KineticBasedBlock;
import com.teammoeg.caupona.blocks.CPHorizontalEntityBlock;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.network.NetworkHooks;

public class BasinBlock extends CPHorizontalEntityBlock<BasinBlockEntity> {

	public BasinBlock(Properties p_54120_) {
		super(CVBlockEntityTypes.BASIN, p_54120_);
		// TODO Auto-generated constructor stub
	}
	static final VoxelShape shape = Block.box(1, 0, 1, 15, 7, 15);
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return shape;
	}
	@SuppressWarnings("deprecation")
	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn,
			BlockHitResult hit) {
		InteractionResult p = super.use(state, worldIn, pos, player, handIn, hit);
		if (p.consumesAction())
			return p;
		BlockEntity be=worldIn.getBlockEntity(pos);
		if (be instanceof BasinBlockEntity pam) {
			/*ItemStack held = player.getItemInHand(handIn);
			FluidStack out=Utils.extractFluid(held);
			if (!out.isEmpty()) {
				if(pam.tankin.fill(out, FluidAction.SIMULATE)==out.getAmount()) {
					pam.tankin.fill(out, FluidAction.EXECUTE);
					ItemStack ret = held.getCraftingRemainingItem();
					held.shrink(1);
					ItemHandlerHelper.giveItemToPlayer(player, ret);
					return InteractionResult.sidedSuccess(worldIn.isClientSide);
				}
			}*/
			if (FluidUtil.interactWithFluidHandler(player, handIn, pam.tankin))
				return InteractionResult.SUCCESS;
			if (handIn == InteractionHand.MAIN_HAND) {
				if(!worldIn.isClientSide)
					NetworkHooks.openScreen((ServerPlayer) player, pam, pam.getBlockPos());
				return InteractionResult.SUCCESS;
			}
		}
		
		return p;
	}
	@Override
	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!(newState.getBlock() instanceof BasinBlock)) {
			if (worldIn.getBlockEntity(pos) instanceof BasinBlockEntity dish) {
				for(int i=0;i<dish.inv.getSlots();i++) {
					super.popResource(worldIn, pos, dish.inv.getStackInSlot(i));
				}
			}
			worldIn.removeBlockEntity(pos);
		}
	}
}
