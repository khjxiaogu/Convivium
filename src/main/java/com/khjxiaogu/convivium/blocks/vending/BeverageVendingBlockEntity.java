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

package com.khjxiaogu.convivium.blocks.vending;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import com.khjxiaogu.convivium.CVBlockEntityTypes;
import com.khjxiaogu.convivium.CVMain;
import com.teammoeg.caupona.network.CPBaseBlockEntity;
import com.teammoeg.caupona.util.IInfinitable;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;

public class BeverageVendingBlockEntity extends CPBaseBlockEntity implements IInfinitable,MenuProvider {
	public ItemStackHandler storage=new ItemStackHandler(6);
	public FluidTank tank=new FluidTank(2500);
	public UUID owner;
	boolean isInfinite = false;
	public int amt;
	public BeverageVendingBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
		super(CVBlockEntityTypes.BEVERAGE_VENDING_MACHINE.get(), pWorldPosition, pBlockState);
	}
	public void setAmount(int cnt) {
		amt=Math.max(Math.min(64, cnt),0);
	}
	public IFluidHandler handler=new IFluidHandler() {
		@Override
		public int getTanks() {return 1;}
		@Override
		public @NotNull FluidStack getFluidInTank(int n) {return tank.getFluid();}
		@Override
		public int getTankCapacity(int tank) {return 2500;}
		@Override
		public boolean isFluidValid(int tank, @NotNull FluidStack stack) {return false;}
		@Override
		public int fill(FluidStack resource, FluidAction action) {return 0;}
		@Override
		public @NotNull FluidStack drain(FluidStack resource, FluidAction oaction) {
			if(getBlockState().getValue(BeverageVendingBlock.ACTIVE)&&resource.getAmount()>=250) {
				if(resource.getAmount()!=250)
					resource=new FluidStack(resource.getFluid(),250,resource.getTag());
				FluidAction action=oaction;
				if(isInfinite)
					action=FluidAction.SIMULATE;
				FluidStack fs=tank.drain(resource, action);
				if(amt>0&&oaction.execute()&&!fs.isEmpty()) {
					level.setBlockAndUpdate(getBlockPos(), getBlockState().setValue(BeverageVendingBlock.ACTIVE,false));
				}
				return fs;
			}
			return FluidStack.EMPTY;
		}

		@Override
		public @NotNull FluidStack drain(int maxDrain, FluidAction oaction) {
			if(getBlockState().getValue(BeverageVendingBlock.ACTIVE)&&maxDrain>=250) {
				FluidAction action=oaction;
				if(isInfinite)
					action=FluidAction.SIMULATE;
				FluidStack fs=tank.drain(250, action);
				if(amt>0&&oaction.execute()&&!fs.isEmpty()) {
					level.setBlockAndUpdate(getBlockPos(), getBlockState().setValue(BeverageVendingBlock.ACTIVE,false));
				}
				return fs;
			}
			return FluidStack.EMPTY;
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
	public void readCustomNBT(CompoundTag nbt, boolean isClient) {
		if(!isClient) {
			storage.deserializeNBT(nbt.getCompound("storage"));
			
			
			
		}
		isInfinite = nbt.getBoolean("inf");
		if(nbt.contains("owner"))
			owner=nbt.getUUID("owner");
		tank.readFromNBT(nbt.getCompound("tank"));
		amt=nbt.getInt("amount");
	}

	@Override
	public void writeCustomNBT(CompoundTag nbt, boolean isClient) {
		if(!isClient) {
			nbt.put("storage",storage.serializeNBT());
			
			
			
		}
		nbt.putBoolean("inf", isInfinite);
		nbt.putUUID("owner", owner);
		nbt.put("tank", tank.writeToNBT(new CompoundTag()));
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
	LazyOptional<IFluidHandler> fl = LazyOptional.of(() -> handler);
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == ForgeCapabilities.FLUID_HANDLER)
			return fl.cast();
		return super.getCapability(cap, side);
	}

}
