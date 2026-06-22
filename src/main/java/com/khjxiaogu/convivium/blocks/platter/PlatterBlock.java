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

package com.khjxiaogu.convivium.blocks.platter;

import java.util.List;

import com.khjxiaogu.convivium.CVBlockEntityTypes;
import com.teammoeg.caupona.blocks.CPRegisteredEntityBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class PlatterBlock extends CPRegisteredEntityBlock<PlatterBlockEntity> {

	public PlatterBlock(Properties blockProps) {
		super(blockProps, CVBlockEntityTypes.PLATTER);
	}


	static final VoxelShape shape = Block.box(0, 0, 0, 16, 3, 16);
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return shape;
	}
	@Override
	public float getShadeBrightness(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 1.0F;
	}

	@Override
	public boolean useShapeForLightOcclusion(BlockState state) {
		return true;
	}
	@Override
	protected List<ItemStack> getDrops(BlockState p_state, Builder p_params) {
		List<ItemStack> list= super.getDrops(p_state, p_params);
		if (p_params.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof PlatterBlockEntity dish) {
			for(int i=0;i<dish.storage.size();i++) {
				list.add(dish.storage.getResource(i).toStack(dish.storage.getAmountAsInt(i)));
			}
		}
		return list;
	}

	public int getSlot(boolean dx,boolean dz) {
		if(dx) {
			if(dz) 
				return 3;//
			return 1;
		}
		if(dz)
			return 2;//
		return 0;
	}




	@Override
	public boolean hasAnalogOutputSignal(BlockState pState) {
		return true;
	}
	
	@Override
	public int getAnalogOutputSignal(BlockState pState, Level pLevel, BlockPos pPos,Direction dir) {
		if (pLevel.getBlockEntity(pPos) instanceof PlatterBlockEntity dish) {
			int sign=0;
			for(int i=0;i<dish.storage.size();i++) {
				if(!dish.storage.getResource(i).isEmpty()) {
					sign|=1<<i;
				}
			}
			return Math.min(sign,15);
		}
		
		return 0;
	}

	@Override
	public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return 20;
	}

	@Override
	public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return 5;
	}
	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		InteractionResult p = super.useWithoutItem(state, level, pos, player, hitResult);
		if (p.consumesAction())
			return p;
		if (level.getBlockEntity(pos) instanceof PlatterBlockEntity blockEntity) {
			if (!level.isClientSide()&&!blockEntity.isInfinite) {
				player.openMenu(blockEntity, blockEntity.getBlockPos());
			}
			return InteractionResult.SUCCESS_SERVER;
		}
		return p;
	}
	@Override
	protected InteractionResult useItemOn(ItemStack held, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		InteractionResult p =super.useItemOn(held, state, level, pos, player, hand, hitResult);
		if (p.consumesAction())
			return p;
		if (level.getBlockEntity(pos) instanceof PlatterBlockEntity blockEntity) {
			if (!level.isClientSide()) {
				if(blockEntity.isInfinite||player.isShiftKeyDown()) {
					double dx=hitResult.getLocation().x-pos.getX();
					double dz=hitResult.getLocation().z-pos.getZ();
					boolean ddx=dx>0.5;
					boolean ddz=dz>0.5;
					if(blockEntity.config==GlobalConfig.PILED) {
						if(!held.isEmpty()&&!blockEntity.isInfinite) {
							for(int i=0;i<4;i++) {
								if(blockEntity.storage.getResource(i).isEmpty()) {
									blockEntity.storage.set(i,ItemResource.of(held.split(1)),1);
									player.setItemInHand(hand, held);
									break;
								}
							}
						}else
							for(int i=3;i>=0;i--) {
								ItemResource ret=blockEntity.storage.getResource(i);
								if(!ret.isEmpty()) {
									try(Transaction trans=Transaction.openRoot()){
										int amt=blockEntity.storage.extract(i, ret, 1, trans);
										if(amt>0)
											player.getInventory().placeItemBackInInventory(ret.toStack(amt));
										if(!blockEntity.isInfinite)
											trans.commit();
									}
									break;
								}
							}
					}else {
						int slot=getSlot(ddx,ddz);
						ItemResource orig=blockEntity.storage.getResource(slot);
						try(Transaction trans=Transaction.openRoot()){
							if(!orig.isEmpty()) {

								int amt=blockEntity.storage.extract(slot, orig, 1, trans);
								player.getInventory().placeItemBackInInventory(orig.toStack(amt));
								if(!blockEntity.isInfinite)
									trans.commit();
							}else if(!blockEntity.isInfinite){
								ItemAccess ia=ItemAccess.forPlayerInteraction(player, hand);
								ItemResource hld=ia.getResource();
								int amt=ia.extract(hld, 1, trans);
								if(amt>0) {
									blockEntity.storage.insert(slot, hld, amt, trans);
									trans.commit();
								}
							}
							
						}
					}
				}else
					player.openMenu( blockEntity, blockEntity.getBlockPos());
			}
			return InteractionResult.SUCCESS_SERVER;
		}
		return p;
	}

}
