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

package com.khjxiaogu.convivium.blocks.whisk;

import java.util.List;

import com.khjxiaogu.convivium.CVBlockEntityTypes;
import com.khjxiaogu.convivium.blocks.basin.BasinBlockEntity;
import com.khjxiaogu.convivium.blocks.kinetics.KineticBasedBlock;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.level.storage.loot.LootParams.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

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
	protected List<ItemStack> getDrops(BlockState p_state, Builder p_params) {
		List<ItemStack> list=super.getDrops(p_state, p_params);
		if (p_params.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof WhiskBlockEntity dish) {
			for(int i=0;i<dish.inv.size();i++) {
				list.add(dish.inv.getResource(i).toStack(dish.inv.getAmountAsInt(i)));
			}
		}
		return list;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		InteractionResult p = super.useWithoutItem(state, level, pos, player, hitResult);
		if (p.consumesAction())
			return p;
		BlockEntity be = level.getBlockEntity(pos);
		if (be instanceof WhiskBlockEntity pam) {
			if (!level.isClientSide())
				player.openMenu(pam, pam.getBlockPos());
			return InteractionResult.SUCCESS;
		}

		return p;
	}

	@Override
	protected InteractionResult useItemOn(ItemStack held, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		InteractionResult p = super.useItemOn(held, state, level, pos, player, hand, hitResult);
		if (p.consumesAction())
			return p;
		BlockEntity be=level.getBlockEntity(pos);
		if (be instanceof WhiskBlockEntity pam) {
			if (held.isEmpty() && player.isShiftKeyDown()) {
				pam.tank.set(0, FluidResource.EMPTY, 0);
				return InteractionResult.SUCCESS;
			}
			if(held.getItem()==Items.POTION) {
				PotionContents potc=held.get(DataComponents.POTION_CONTENTS);
				if(potc.potion().filter(o->o==Potions.WATER).isPresent()) {
					FluidStack water=new FluidStack(Fluids.WATER,250);
					/*if(pam.accessabletank.fill(water,FluidAction.SIMULATE)==250) {
						ItemStack remain=new ItemStack(Items.GLASS_BOTTLE);
						held.shrink(1);
						pam.accessabletank.fill(water, FluidAction.EXECUTE);
						ItemHandlerHelper.giveItemToPlayer(player, remain);
						return ItemInteractionResult.SUCCESS;
					}*/
				}
			}
/*
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
				return ItemInteractionResult.SUCCESS;*/
		}
		
		return p;
	}
}
