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

package com.khjxiaogu.convivium.blocks.platter;

import com.khjxiaogu.convivium.CVBlockEntityTypes;
import com.teammoeg.caupona.blocks.CPRegisteredEntityBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.network.NetworkHooks;

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
	@OnlyIn(Dist.CLIENT)
	public float getShadeBrightness(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 1.0F;
	}

	@Override
	public boolean useShapeForLightOcclusion(BlockState state) {
		return true;
	}

	@Override
	public boolean propagatesSkylightDown(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
		return true;
	}



	@Override
	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!(newState.getBlock() instanceof PlatterBlock)) {
			if (worldIn.getBlockEntity(pos) instanceof PlatterBlockEntity dish) {
				for(int i=0;i<dish.storage.getSlots();i++) {
					super.popResource(worldIn, pos, dish.storage.getStackInSlot(i));
				}
			}
			worldIn.removeBlockEntity(pos);
		}

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
	@SuppressWarnings("deprecation")
	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn,
			BlockHitResult hit) {
		InteractionResult p = super.use(state, worldIn, pos, player, handIn, hit);
		if (p.consumesAction())
			return p;
		if (worldIn.getBlockEntity(pos) instanceof PlatterBlockEntity blockEntity) {
			if (!worldIn.isClientSide) {
				if(blockEntity.isInfinite||player.isShiftKeyDown()) {
					double dx=hit.getLocation().x-pos.getX();
					double dz=hit.getLocation().z-pos.getZ();
					boolean ddx=dx>0.5;
					boolean ddz=dz>0.5;
					if(blockEntity.config==GlobalConfig.PILED) {
						ItemStack hand=player.getItemInHand(handIn);
						if(!hand.isEmpty()&&!blockEntity.isInfinite) {
							for(int i=0;i<4;i++) {
								if(blockEntity.storage.getStackInSlot(i).isEmpty()) {
									blockEntity.storage.setStackInSlot(i,hand.split(1));
									player.setItemInHand(handIn, hand);
									break;
								}
							}
						}else
							for(int i=3;i>=0;i--) {
								ItemStack ret=blockEntity.storage.getStackInSlot(i);
								if(!ret.isEmpty()) {
									ItemHandlerHelper.giveItemToPlayer(player, ret.copy());
									if(!blockEntity.isInfinite)
										blockEntity.storage.setStackInSlot(i,ItemStack.EMPTY);
									break;
								}
							}
					}else {
						int slot=getSlot(ddx,ddz);
						ItemStack orig=blockEntity.storage.getStackInSlot(slot);
						if(!orig.isEmpty()) {
							ItemHandlerHelper.giveItemToPlayer(player, orig.copy());
							orig=ItemStack.EMPTY;
						}else if(!blockEntity.isInfinite){
							orig=player.getItemInHand(handIn).split(1);
						}
						if(!blockEntity.isInfinite)
							blockEntity.storage.setStackInSlot(slot,orig);
					}
				}else
					NetworkHooks.openScreen((ServerPlayer) player, blockEntity, blockEntity.getBlockPos());
			}
			return InteractionResult.SUCCESS;
		}
		return p;
	}



	@Override
	public boolean hasAnalogOutputSignal(BlockState pState) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState pState, Level pLevel, BlockPos pPos) {
		if (pLevel.getBlockEntity(pPos) instanceof PlatterBlockEntity dish) {
			int sign=0;
			for(int i=0;i<dish.storage.getSlots();i++) {
				if(!dish.storage.getStackInSlot(i).isEmpty()) {
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
}
