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

import com.khjxiaogu.convivium.CVBlockEntityTypes;
import com.teammoeg.caupona.blocks.foods.IFoodContainer;
import com.teammoeg.caupona.network.CPBaseBlockEntity;
import com.teammoeg.caupona.util.IInfinitable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;

public class BeverageBlockEntity extends CPBaseBlockEntity implements IInfinitable,IFoodContainer {
	public ItemStack internal = ItemStack.EMPTY;
	boolean isInfinite = false;

	public BeverageBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
		super(CVBlockEntityTypes.BEVERAGE.get(), pWorldPosition, pBlockState);
	}

	@Override
	public void handleMessage(short type, int data) {
	}

	@Override
	public void readCustomNBT(CompoundTag nbt, boolean isClient,HolderLookup.Provider ra) {
		internal = ItemStack.parseOptional(ra,nbt.getCompound("bowl"));
		isInfinite = nbt.getBoolean("inf");
	}

	@Override
	public void writeCustomNBT(CompoundTag nbt, boolean isClient,HolderLookup.Provider ra) {
		nbt.put("bowl", internal.save(ra));
		nbt.putBoolean("inf", isInfinite);
	}

	@Override
	public void tick() {
	}

	@Override
	public boolean setInfinity() {
		return isInfinite = !isInfinite;
	}
	@Override
	public ItemStack getInternal(int num) {
		return internal;
	}

	@Override
	public void setInternal(int num, ItemStack is) {
		if(!isInfinite) {
			internal=is;
			this.syncData();
		}
	}

	@Override
	public int getSlots() {
		return 1;
	}

	@Override
	public boolean accepts(int num, ItemStack is) {
		return is.getItem() instanceof BeverageItem||is.is(Items.GLASS_BOTTLE)||is.is(Items.POTION);
	}

}
