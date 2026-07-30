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

package com.khjxiaogu.convivium.blocks.foods;

import org.jspecify.annotations.Nullable;

import com.khjxiaogu.convivium.CVBlockEntityTypes;
import com.teammoeg.caupona.blocks.foods.IFoodContainer;
import com.teammoeg.caupona.network.CPBaseBlockEntity;
import com.teammoeg.caupona.util.IInfinitable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.UseRemainder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class BeverageBlockEntity extends CPBaseBlockEntity implements IInfinitable,IFoodContainer {
	private ItemStacksResourceHandler internal=new ItemStacksResourceHandler(1) {

		@Override
		protected void onContentsChanged(int index, ItemStack previousContents) {
			syncData();
			super.onContentsChanged(index, previousContents);
		}
		
		
	};
	boolean isInfinite = false;

	public BeverageBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
		super(CVBlockEntityTypes.BEVERAGE.get(), pWorldPosition, pBlockState);
	}

	@Override
	public void handleMessage(short type, int data) {
	}

	@Override
	public void readCustomNBT(ValueInput nbt, boolean isClient) {
		nbt.readChild("drink", getInternal());
		isInfinite = nbt.getBooleanOr("inf",false);
	}

	@Override
	public void writeCustomNBT(ValueOutput nbt, boolean isClient) {
		nbt.putChild("drink", getInternal());
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
	public int getSlots() {
		return 1;
	}


	@Override
	public boolean isInfinite() {
		return isInfinite;
	}

	@Override
	public boolean accepts(int slot, ItemResource is) {
		return is.getItem() instanceof BeverageItem||is.getItem() instanceof EmptyBeverageBlockItem||is.is(Items.GLASS_BOTTLE)||is.is(Items.POTION);
	}

	@Override
	public ItemResource exchangeInternal(int num, ItemResource is,TransactionContext parent) {
		ItemResource ir=getInternal().getResource(0);
		try(Transaction trans=Transaction.open(parent)){
			int inserted=1;
			int extracted=getInternal().extract(0, ir, 1, trans);
			if(!is.isEmpty()) {
				inserted=getInternal().insert(0,is,1,trans);
			}
			if(inserted==1) {
				trans.commit();
				if(extracted>0) {
					return ir;
				}
				return ItemResource.EMPTY;
			}
		}
		return is;
	}

	public ItemStacksResourceHandler getInternal() {
		return internal;
	}

	@Override
	public ItemResource getValidContainer(int slot) {
		ItemResource ir=getInternal().getResource(0);
		if(ir.getItem() instanceof BeverageItem) {
			@Nullable UseRemainder reminder=ir.get(DataComponents.USE_REMAINDER);
			if(reminder!=null)
				return ItemResource.of(reminder.convertInto());
		}else if(ir.getItem() instanceof EmptyBeverageBlockItem) {
			return ir;
		}
		return ItemResource.of(Items.GLASS_BOTTLE);
	}

}
