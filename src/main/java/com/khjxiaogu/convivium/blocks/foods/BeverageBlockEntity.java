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

package com.khjxiaogu.convivium.blocks.foods;

import com.khjxiaogu.convivium.CVBlockEntityTypes;
import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.blocks.foods.IFoodContainer;
import com.teammoeg.caupona.item.DishItem;
import com.teammoeg.caupona.network.CPBaseBlockEntity;
import com.teammoeg.caupona.util.IInfinitable;

import net.minecraft.core.BlockPos;
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
	public void readCustomNBT(CompoundTag nbt, boolean isClient) {
		internal = ItemStack.of(nbt.getCompound("bowl"));
		isInfinite = nbt.getBoolean("inf");
	}

	@Override
	public void writeCustomNBT(CompoundTag nbt, boolean isClient) {
		nbt.put("bowl", internal.serializeNBT());
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
			if(internal.is(Items.BOWL)) {
				this.getLevel().setBlockAndUpdate(this.getBlockPos(), CPBlocks.DISH.get().defaultBlockState());
			}else if(internal.getItem() instanceof DishItem dish){
				this.getLevel().setBlockAndUpdate(this.getBlockPos(), dish.getBlock().defaultBlockState());
			}
			this.syncData();
		}
	}

	@Override
	public int getSlots() {
		return 1;
	}

	@Override
	public boolean accepts(int num, ItemStack is) {
		return is.getItem() instanceof DishItem||is.is(Items.BOWL);
	}

}
