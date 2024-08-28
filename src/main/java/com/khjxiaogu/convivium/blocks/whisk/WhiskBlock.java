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

package com.khjxiaogu.convivium.blocks.whisk;

import com.khjxiaogu.convivium.CVBlockEntityTypes;
import com.khjxiaogu.convivium.blocks.kinetics.KineticBasedBlock;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public class WhiskBlock extends KineticBasedBlock<WhiskBlockEntity> {

	public WhiskBlock(Properties p_54120_) {
		super(CVBlockEntityTypes.WHISK, p_54120_);
		// TODO Auto-generated constructor stub
	}

	static final VoxelShape shape = Shapes.or(Block.box(1, 0, 1, 15, 8, 15), Block.box(6, 8, 6, 10, 16, 10));

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return shape;
	}

	@Override
	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!(newState.getBlock() instanceof WhiskBlock)) {
			if (worldIn.getBlockEntity(pos) instanceof WhiskBlockEntity dish) {
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
		if (be instanceof WhiskBlockEntity pam) {
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
		BlockEntity be=level.getBlockEntity(pos);
		if (be instanceof WhiskBlockEntity pam) {
			if (held.isEmpty() && player.isShiftKeyDown()) {
				pam.accessabletank.drain(1250, FluidAction.EXECUTE);
				return ItemInteractionResult.SUCCESS;
			}
			if(held.getItem()==Items.POTION) {
				PotionContents potc=held.get(DataComponents.POTION_CONTENTS);
				if(potc.potion().filter(o->o==Potions.WATER).isPresent()) {
					FluidStack water=new FluidStack(Fluids.WATER,250);
					if(pam.accessabletank.fill(water,FluidAction.SIMULATE)==250) {
						ItemStack remain=new ItemStack(Items.GLASS_BOTTLE);
						held.shrink(1);
						pam.accessabletank.fill(water, FluidAction.EXECUTE);
						ItemHandlerHelper.giveItemToPlayer(player, remain);
						return ItemInteractionResult.SUCCESS;
					}
				}
			}

			FluidStack out=Utils.extractFluid(held);
			if (!out.isEmpty()) {
				if(pam.accessabletank.fill(out, FluidAction.SIMULATE)==out.getAmount()) {
					pam.accessabletank.fill(out, FluidAction.EXECUTE);
					ItemStack ret = held.getCraftingRemainingItem();
					held.shrink(1);
					ItemHandlerHelper.giveItemToPlayer(player, ret);
					return ItemInteractionResult.sidedSuccess(level.isClientSide);
				}
			}
			if (FluidUtil.interactWithFluidHandler(player, hand, pam.accessabletank))
				return ItemInteractionResult.SUCCESS;
		}
		
		return p;
	}
}
