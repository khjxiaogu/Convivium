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

package com.khjxiaogu.convivium.blocks.foods;

import org.jetbrains.annotations.Nullable;

import com.khjxiaogu.convivium.CVBlockEntityTypes;
import com.khjxiaogu.convivium.CVBlocks;
import com.khjxiaogu.convivium.CVComponents;
import com.khjxiaogu.convivium.CVItems;
import com.khjxiaogu.convivium.util.PotionItemInfo;
import com.teammoeg.caupona.blocks.CPRegisteredEntityBlock;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BeverageBlock extends CPRegisteredEntityBlock<BeverageBlockEntity> {


	public BeverageBlock(Properties blockProps) {
		super(blockProps, CVBlockEntityTypes.BEVERAGE);
		CVBlocks.beverage.add(this);
	}


	static final VoxelShape shape = Block.box(4, 0, 4, 12, 15, 12);

	@Override
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
		if (!(newState.getBlock() instanceof BeverageBlock)) {
			if (worldIn.getBlockEntity(pos) instanceof BeverageBlockEntity dish) {

				super.popResource(worldIn, pos, dish.internal);
			}
			worldIn.removeBlockEntity(pos);
		}

	}
	
	@Override
	public InteractionResult useWithoutItem(BlockState state, Level worldIn, BlockPos pos, Player player, BlockHitResult hit) {
		InteractionResult p = super.useWithoutItem(state, worldIn, pos, player,  hit);
		if (p.consumesAction())
			return p;
		if (worldIn.getBlockEntity(pos) instanceof BeverageBlockEntity dish &&
				dish.internal != null) {
			if(dish.internal.getItem() instanceof BeverageItem
					&& dish.internal.getFoodProperties(null)!=null) {
				FoodProperties fp = dish.internal.getFoodProperties(player);
				if(fp.nutrition()<=0) {
					player.gameEvent(GameEvent.DRINK);
					player.getFoodData().eat(fp);
					player.awardStat(Stats.ITEM_USED.get(dish.internal.getItem()));
					if(!worldIn.isClientSide) {
						CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer) player, dish.internal);
						if(!dish.isInfinite) {
							ItemStack iout = dish.internal.getCraftingRemainingItem();
							dish.internal = iout;
							dish.syncData();
						}
					}
				}else if (player.canEat(fp.canAlwaysEat())) {
					player.gameEvent(GameEvent.DRINK);
					player.eat(worldIn, dish.internal);
					if(!worldIn.isClientSide) {
						if(!dish.isInfinite) {
							ItemStack iout = dish.internal.getCraftingRemainingItem();
							dish.internal = iout;
							dish.syncData();
						}
					}
				}
				
			}else if(dish.internal.is(Items.POTION)) {
				if (player instanceof ServerPlayer) {
			         CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)player,dish.internal);
			      }

			      if (!worldIn.isClientSide) {
			         for(MobEffectInstance mobeffectinstance : dish.internal.get(DataComponents.POTION_CONTENTS).getAllEffects()) {
			            if (mobeffectinstance.getEffect().value().isInstantenous()) {
			               mobeffectinstance.getEffect().value().applyInstantenousEffect(player, player, player, mobeffectinstance.getAmplifier(), 1.0D);
			            } else {
			            	player.addEffect(new MobEffectInstance(mobeffectinstance));
			            }
			         }
			         if(!dish.isInfinite) {
							ItemStack iout = new ItemStack(Items.GLASS_BOTTLE);
							dish.internal = iout;
							dish.syncData();
						}
			      }
				
			}
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	@Override
	public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
		super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
		if (pLevel.getBlockEntity(pPos) instanceof BeverageBlockEntity dish) {
			if(pStack.is(CVItems.POTION.get())) {
				@Nullable PotionItemInfo comp=pStack.get(CVComponents.POTION_ITEM);
				if(comp!=null)
				dish.internal = comp.getStack();
			}else
				dish.internal = pStack.copyWithCount(1);
		}
	}

	public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos,
			Player player) {
		if (level.getBlockEntity(pos) instanceof BeverageBlockEntity dish) {
			if (dish.internal == null)
				return ItemStack.EMPTY;
			return dish.internal.copy();
		}
		return this.getCloneItemStack(state, target, level, pos, player);
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState pState) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState pState, Level pLevel, BlockPos pPos) {
		if (pLevel.getBlockEntity(pPos) instanceof BeverageBlockEntity dish)
			if (dish.internal != null && !dish.internal.isEmpty() && dish.internal.getFoodProperties(null)!=null) 
				return 15;
		
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
