/*
 * Copyright (c) 2022 TeamMoeg
 *
 * This file is part of Caupona.
 *
 * Caupona is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Caupona is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * Specially, we allow this software to be used alongside with closed source software Minecraft(R) and Forge or other modloader.
 * Any mods or plugins can also use apis provided by forge or com.teammoeg.caupona.api without using GPL or open source.
 *
 * You should have received a copy of the GNU General Public License
 * along with Caupona. If not, see <https://www.gnu.org/licenses/>.
 */

package com.khjxiaogu.convivium.blocks.platter;

import com.khjxiaogu.convivium.CVBlockEntityTypes;
import com.teammoeg.caupona.CPBlockEntityTypes;
import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.blocks.CPRegisteredEntityBlock;
import com.teammoeg.caupona.blocks.pan.PanBlockEntity;
import com.teammoeg.caupona.item.DishItem;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
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
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return shape;
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

	public int getSlot(PlatterBlockEntity be,boolean dx,boolean dz) {
		if(dx) {
			if(dz) 
				return 3;//
			else
				return 1;
		}else {
			if(dz)
				return 2;//
			else
				return 0;
		}
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
				if(player.isShiftKeyDown()) {
					double dx=hit.getLocation().x-pos.getX();
					double dz=hit.getLocation().z-pos.getZ();
					boolean ddx=dx>0.5;
					boolean ddz=dz>0.5;
					if(blockEntity.config==GlobalConfig.PILED) {
						ItemStack hand=player.getItemInHand(handIn);
						if(!hand.isEmpty()) {
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
									ItemHandlerHelper.giveItemToPlayer(player, ret);
									blockEntity.storage.setStackInSlot(i,ItemStack.EMPTY);
									break;
								}
							}
					}else {
						int slot=getSlot(blockEntity,ddx,ddz);
						ItemStack orig=blockEntity.storage.getStackInSlot(slot);
						if(!orig.isEmpty()) {
							ItemHandlerHelper.giveItemToPlayer(player, orig);
							orig=ItemStack.EMPTY;
						}else {
							orig=player.getItemInHand(handIn).split(1);
						}
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
