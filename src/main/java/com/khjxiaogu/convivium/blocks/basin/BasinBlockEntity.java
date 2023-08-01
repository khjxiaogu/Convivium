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

package com.khjxiaogu.convivium.blocks.basin;

import java.util.List;

import com.khjxiaogu.convivium.CVBlockEntityTypes;
import com.khjxiaogu.convivium.CVMain;
import com.khjxiaogu.convivium.blocks.kinetics.KineticTransferBlockEntity;
import com.khjxiaogu.convivium.data.recipes.BasinRecipe;
import com.khjxiaogu.convivium.data.recipes.GrindingRecipe;
import com.khjxiaogu.convivium.util.RotationUtils;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;

public class BasinBlockEntity extends KineticTransferBlockEntity implements MenuProvider {
	public ItemStackHandler inv = new ItemStackHandler(4) {
		@Override
		public boolean isItemValid(int slot, ItemStack stack) {
			return slot < 3&&GrindingRecipe.testInput(stack);
		}

		@Override
		protected void onContentsChanged(int slot) {
			// TODO Auto-generated method stub
			super.onContentsChanged(slot);
			syncData();
		}
	};
	public final FluidTank tankin = new FluidTank(1000);
	public int process;
	public int processMax;
	public List<ItemStack> items;
	public BasinBlockEntity( BlockPos pWorldPosition, BlockState pBlockState) {
		super(CVBlockEntityTypes.BASIN.get(), pWorldPosition, pBlockState);
	}

	@Override
	public void readCustomNBT(CompoundTag nbt, boolean isClient) {
		// TODO Auto-generated method stub
		super.readCustomNBT(nbt, isClient);
		process=nbt.getInt("process");
		processMax=nbt.getInt("processMax");
		tankin.readFromNBT(nbt.getCompound("in"));
		inv.deserializeNBT(nbt.getCompound("inv"));
	}

	@Override
	public void writeCustomNBT(CompoundTag nbt, boolean isClient) {
		// TODO Auto-generated method stub
		super.writeCustomNBT(nbt, isClient);
		nbt.putInt("process", process);
		nbt.putInt("processMax", processMax);
		nbt.put("in",tankin.writeToNBT(new CompoundTag()));
		nbt.put("inv", inv.serializeNBT());
	}

	@Override
	public void handleMessage(short type, int data) {

	}
	@Override
	public boolean isReceiver() {
		// TODO Auto-generated method stub
		return true;
	}
	public void spawnParticleFor(ItemStack is) {
		if(is.isEmpty())return;
		ItemParticleOption data = new ItemParticleOption(ParticleTypes.ITEM, is);
		Vec3 rot=new Vec3(0,0,1).yRot((float) RotationUtils.getRotationAngle(0,this.getBlockPos())).scale(0.3f);
		Vec3 center = Vec3.atCenterOf(this.getBlockPos()).add(rot);
		Vec3 target=Vec3.ZERO.offsetRandom(this.level.random,0.2f).multiply(1,0,1).add(0, 0.2, 0);
		level.addParticle(data, center.x, center.y, center.z, target.x, target.y, target.z);
	}
	@Override
	public void tick() {
		// TODO Auto-generated method stub
		super.tick();
		if(level.isClientSide) {
			if(process!=0)
				for(int i=0;i<3;i++) {
					ItemStack stackInSlot = inv.getStackInSlot(i);
					if (!stackInSlot.isEmpty()){
						if(Math.random()<0.05)
							spawnParticleFor(stackInSlot);
					}
				}
			return;
		}
		if(processMax!=0) {
			
			if(process<=0) {
				items.replaceAll(t->Utils.insertToOutput(inv,5,Utils.insertToOutput(inv,4,Utils.insertToOutput(inv,3,t))));
				items.removeIf(ItemStack::isEmpty);
				if(items.isEmpty()) {
					processMax=0;
				}
			}else {
				process-=getSpeed();
			}
			this.syncData();
		}else {
			BasinRecipe recipe=BasinRecipe.testAll(tankin.getFluid());
			if(recipe!=null) {
				items=recipe.handle(tankin.getFluidInTank(0));
				process=processMax=recipe.processTime;
				this.syncData();
			}
			
		}
	}
	LazyOptional<IItemHandler> down = LazyOptional.of(() -> new RangedWrapper(inv, 3, 6));
	LazyOptional<IItemHandler> side = LazyOptional.of(() -> new RangedWrapper(inv, 0, 3));
	LazyOptional<IFluidHandler> fl = LazyOptional.of(() -> tankin);

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == ForgeCapabilities.ITEM_HANDLER) {
			if (side == Direction.DOWN)
				return down.cast();
			return this.side.cast();
		}
		if (cap == ForgeCapabilities.FLUID_HANDLER)
			return fl.cast();
		return super.getCapability(cap, side);
	}
	@Override
	public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
		return new BasinContainer(pContainerId, pInventory, this);
	}

	@Override
	public Component getDisplayName() {
		return Utils.translate("container." + CVMain.MODID + ".pestle_and_mortar.title");
	}
}
