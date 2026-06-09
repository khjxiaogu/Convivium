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

package com.khjxiaogu.convivium.blocks.vending;

import java.util.UUID;

import com.khjxiaogu.convivium.CVBlockEntityTypes;
import com.khjxiaogu.convivium.CVMain;
import com.teammoeg.caupona.network.CPBaseBlockEntity;
import com.teammoeg.caupona.util.IInfinitable;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.DelegatingResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class BeverageVendingBlockEntity extends CPBaseBlockEntity implements IInfinitable,MenuProvider {
	public ItemStacksResourceHandler storage = new ItemStacksResourceHandler(6) {
		@Override
		protected void onContentsChanged(int slot,ItemStack stack) {
			super.onContentsChanged(slot,stack);
			syncData();
		}
	};
	public final FluidStacksResourceHandler tank=new FluidStacksResourceHandler(1,2500) {

		@Override
		protected void onContentsChanged(int index, FluidStack previousContents) {
			super.onContentsChanged(index, previousContents);
			syncData();
		}
		
	};
	public UUID owner;
	boolean isInfinite = false;
	public int amt;
	public BeverageVendingBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
		super(CVBlockEntityTypes.BEVERAGE_VENDING_MACHINE.get(), pWorldPosition, pBlockState);
	}
	public void setAmount(int cnt) {
		amt=Math.max(Math.min(64, cnt),0);
	}
	public ResourceHandler<FluidResource> handler=new DelegatingResourceHandler<>(tank) {

		@Override
		public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
			return 0;
		}

		@Override
		public int insert(FluidResource resource, int amount, TransactionContext transaction) {
			return 0;
		}

		@Override
		public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
			if(getBlockState().getValue(BeverageVendingBlock.ACTIVE)&&amount>=250) {
				int extracted;
				try(Transaction trans=Transaction.open(transaction)){
					extracted=super.extract(index, resource, 250, trans);
					if(isInfinite) {
						trans.commit();
					}
				}
				if(extracted>=250) {
					BlockStateSnapshotJournal journal=new BlockStateSnapshotJournal(BeverageVendingBlockEntity.this,getBlockState().setValue(BeverageVendingBlock.ACTIVE,false));
					journal.updateSnapshots(transaction);
				}
				return extracted;
			}
			return 0;
		}

		@Override
		public int extract(FluidResource resource, int amount, TransactionContext transaction) {
			// TODO Auto-generated method stub
			return super.extract(resource, amount, transaction);
		}
		
	};
	@Override
	public void handleMessage(short type, int data) {
		switch(type) {
		case 0:setAmount(amt+8);break;
		case 1:setAmount(amt+1);break;
		case 2:setAmount(amt-1);break;
		case 3:setAmount(amt-8);break;
		}
		boolean active=this.getBlockState().getValue(BeverageVendingBlock.ACTIVE);
		if(!active&&amt==0) {
			this.level.setBlockAndUpdate(getBlockPos(), getBlockState().setValue(BeverageVendingBlock.ACTIVE,true));
		}else if(active) {
			this.level.setBlockAndUpdate(getBlockPos(), getBlockState().setValue(BeverageVendingBlock.ACTIVE,false));
		}else
			this.syncData();
	}

	@Override
	public void readCustomNBT(ValueInput nbt, boolean isClient) {
		if(!isClient) {
			nbt.readChild("storage", storage);
			nbt.readChild("tank", tank);
		}
		isInfinite = nbt.getBooleanOr("inf", false);
		owner=nbt.read("owner", UUIDUtil.CODEC).orElse(null);
		amt=nbt.getIntOr("amount", 0);
	}

	@Override
	public void writeCustomNBT(ValueOutput nbt, boolean isClient) {
		if(!isClient) {
			nbt.putChild("storage", storage);
			nbt.putChild("tank", tank);
			nbt.putBoolean("inf", isInfinite);
		}
		if(owner!=null)
			nbt.store("owner", UUIDUtil.CODEC, owner);
		nbt.putInt("amount", amt);
	}

	@Override
	public void tick() {
		
	}

	@Override
	public boolean setInfinity() {
		return isInfinite = !isInfinite;
	}
	@Override
	public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
		return new BeverageVendingContainer(pContainerId, pInventory, this);
	}

	@Override
	public Component getDisplayName() {
		return Utils.translate("container." + CVMain.MODID + ".beverage_vending_machine.title");
	}
	@Override
	public Object getCapability(BlockCapability<?, Direction> cap, Direction side) {
		if (cap == Capabilities.Fluid.BLOCK)
			return handler;
		return super.getCapability(cap, side);
	}
	@Override
	public boolean isInfinite() {
		return isInfinite;
	}
}
