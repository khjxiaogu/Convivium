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

package com.khjxiaogu.convivium.blocks.pestle_and_mortar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.jetbrains.annotations.NotNull;

import com.khjxiaogu.convivium.CVBlockEntityTypes;
import com.khjxiaogu.convivium.CVMain;
import com.khjxiaogu.convivium.blocks.kinetics.KineticTransferBlockEntity;
import com.khjxiaogu.convivium.data.recipes.GrindingRecipe;
import com.khjxiaogu.convivium.util.RotationUtils;
import com.teammoeg.caupona.util.SyncedFluidHandler;
import com.teammoeg.caupona.util.Utils;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.RangedWrapper;

public class PamBlockEntity extends KineticTransferBlockEntity implements MenuProvider {
	public ItemStackHandler inv = new ItemStackHandler(6) {
		@Override
		public boolean isItemValid(int slot, ItemStack stack) {
			return slot < 3&&GrindingRecipe.testInput(stack);
		}

		@Override
		protected void onContentsChanged(int slot) {
			recipeTested=false;
			super.onContentsChanged(slot);
			syncData();
		}
	};
	public final IFluidHandler tanks=new SyncedFluidHandler(this,new IFluidHandler() {

		@Override
		public int getTanks() {
			return 2;
		}

		@Override
		public @NotNull FluidStack getFluidInTank(int tank) {
			// TODO Auto-generated method stub
			return tank==0?tankout.getFluidInTank(0):tankin.getFluidInTank(1);
		}

		@Override
		public int getTankCapacity(int tank) {
			// TODO Auto-generated method stub
			return 1000;
		}

		@Override
		public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
			// TODO Auto-generated method stub
			if(tank==0)return false;
			return tankin.isFluidValid(0,stack);
		}

		@Override
		public int fill(FluidStack resource, FluidAction action) {
			// TODO Auto-generated method stub
			return tankin.fill(resource, action);
		}

		@Override
		public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
			// TODO Auto-generated method stub
			FluidStack out=tankout.drain(resource, action);
			if(!out.isEmpty())
				return out;
			return tankin.drain(resource, action);
		}

		@Override
		public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
			// TODO Auto-generated method stub
			FluidStack out=tankout.drain(maxDrain, action);
			if(!out.isEmpty())
				return out;
			return tankin.drain(maxDrain, action);
		}
		
	});
	public final FluidTank tankin = new FluidTank(1000) {

		@Override
		protected void onContentsChanged() {
			recipeTested=false;
			super.onContentsChanged();
		}
		
	};
	public final FluidTank tankout = new FluidTank(1000);
	public int process;
	public int processMax;
	public boolean recipeTested=false;
	public List<ItemStack> items=new ArrayList<>();
	public FluidStack fout=FluidStack.EMPTY;
	ResourceLocation lastRecipe;
	public PamBlockEntity( BlockPos pWorldPosition, BlockState pBlockState) {
		super(CVBlockEntityTypes.PAM.get(), pWorldPosition, pBlockState);
	}

	@Override
	public void readCustomNBT(CompoundTag nbt, boolean isClient,HolderLookup.Provider ra) {
		// TODO Auto-generated method stub
		super.readCustomNBT(nbt, isClient,ra);
		process=nbt.getInt("process");
		processMax=nbt.getInt("processMax");
		tankin.readFromNBT(ra,nbt.getCompound("in"));
		tankout.readFromNBT(ra,nbt.getCompound("out"));
		inv.deserializeNBT(ra,nbt.getCompound("inv"));
		
		if(!isClient) {
			fout=FluidStack.parseOptional(ra,nbt.getCompound("fout"));
			ListTag list=nbt.getList("outBuff",10);
			items=new ArrayList<>();
			
			for(int i=0;i<list.size();i++) {
				items.add(ItemStack.parseOptional(ra,list.getCompound(i)));
			}
			if(nbt.contains("lastRecipe"))
				lastRecipe=ResourceLocation.parse(nbt.getString("lastRecipe"));
			else
				lastRecipe=null;
		}
		
	}

	@Override
	public void writeCustomNBT(CompoundTag nbt, boolean isClient,HolderLookup.Provider ra) {
		// TODO Auto-generated method stub
		super.writeCustomNBT(nbt, isClient,ra);
		nbt.putInt("process", process);
		nbt.putInt("processMax", processMax);
		nbt.put("in",tankin.writeToNBT(ra,new CompoundTag()));
		nbt.put("out",tankout.writeToNBT(ra,new CompoundTag()));
		nbt.put("inv", inv.serializeNBT(ra));
		if(isClient)return;
		
		if(fout!=null)
			nbt.put("fout", fout.saveOptional(ra));
		ListTag tl=new ListTag();
		items.forEach(t->tl.add(t.save(ra)));
		nbt.put("outBuff", tl);
		if(lastRecipe!=null)
			nbt.putString("lastRecipe", lastRecipe.toString());
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
		if(!items.isEmpty()||!fout.isEmpty()) {
			items.replaceAll(t->Utils.insertToOutput(inv,5,Utils.insertToOutput(inv,4,Utils.insertToOutput(inv,3,t))));
			items.removeIf(ItemStack::isEmpty);
			fout.shrink(tankout.fill(fout, FluidAction.EXECUTE));
			return;
		}
		
		
		if(!recipeTested){
			recipeTested=true;
			RecipeHolder<GrindingRecipe> recipe=GrindingRecipe.test(tankin.getFluid(), inv);
			if(recipe!=null) {
				if(!recipe.id().equals(lastRecipe)) {
					process=processMax=recipe.value().processTime;
					lastRecipe=recipe.id();
				}
			}else {
				process=processMax=0;
				lastRecipe=null;
			}
			this.syncData();
		}else
		if(processMax!=0) {
			if(process<=0) {
				RecipeHolder<GrindingRecipe> recipe=GrindingRecipe.test(tankin.getFluid(), inv);
				if(recipe!=null) {
					fout=recipe.value().out.copy();
					if(recipe.value().keepInfo) {
						fout.applyComponents(tankin.getFluidInTank(0).getComponentsPatch());
					}
					items=recipe.value().handle(tankin.getFluidInTank(0), inv);
				}else if(items.isEmpty()&&fout.isEmpty()){
					process=processMax=0;
				}
				recipeTested=false;
				lastRecipe=null;
			}else process-=getSpeed();
			
			this.syncData();
		}
	}

	@Override
	public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
		return new PamContainer(pContainerId, pInventory, this);
	}

	@Override
	public Component getDisplayName() {
		return Utils.translate("container." + CVMain.MODID + ".pestle_and_mortar.title");
	}

	@Override
	public Object getCapability(BlockCapability<?, Direction> type, Direction d) {
		if (type == Capabilities.ItemHandler.BLOCK) {
			if (d == Direction.DOWN)
				return new RangedWrapper(inv, 3, 6);
			return new RangedWrapper(inv, 0, 3);
		}
		if (type == Capabilities.FluidHandler.BLOCK)
			return tanks;
		return super.getCapability(type, d);
	}
}
