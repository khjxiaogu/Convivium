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

package com.khjxiaogu.convivium.blocks.basin;

import java.util.ArrayList;
import java.util.List;

import com.khjxiaogu.convivium.CVBlockEntityTypes;
import com.khjxiaogu.convivium.CVBlocks;
import com.khjxiaogu.convivium.CVMain;
import com.khjxiaogu.convivium.data.recipes.BasinRecipe;
import com.khjxiaogu.convivium.util.RotationUtils;
import com.teammoeg.caupona.blocks.stove.IStove;
import com.teammoeg.caupona.network.CPBaseBlockEntity;
import com.teammoeg.caupona.util.RecipeHandler;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
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

public class BasinBlockEntity extends CPBaseBlockEntity implements MenuProvider {
	public ItemStackHandler inv = new ItemStackHandler(5) {
		@Override
		public boolean isItemValid(int slot, ItemStack stack) {
			return slot < 1&&BasinRecipe.testInput(stack);
		}

		@Override
		protected void onContentsChanged(int slot) {
			super.onContentsChanged(slot);
			if(slot<2)
				recipeHandler.onContainerChanged();
			setChanged();
		}
	};
	public final FluidTank tankin = new FluidTank(1000) {

		@Override
		protected void onContentsChanged() {
			super.onContentsChanged();
			recipeHandler.onContainerChanged();
			syncData();
		}
		
	};
	public RecipeHandler<BasinRecipe> recipeHandler=new RecipeHandler<>(()->{
		RecipeHolder<BasinRecipe> recipe=BasinRecipe.testAll(tankin.getFluid(),inv.getStackInSlot(0),this.getBlockState().is(CVBlocks.lead_basin.get()));
		if(recipe!=null) {
			items=recipe.value().handle(tankin.getFluidInTank(0),inv.getStackInSlot(0));
		}
	});
	public List<ItemStack> items=new ArrayList<>();
	public boolean isLastHeating;
	public BasinBlockEntity( BlockPos pWorldPosition, BlockState pBlockState) {
		super(CVBlockEntityTypes.BASIN.get(), pWorldPosition, pBlockState);
	}

	@Override
	public void readCustomNBT(CompoundTag nbt, boolean isClient,HolderLookup.Provider ra) {
		recipeHandler.readCustomNBT(nbt, isClient);
		tankin.readFromNBT(ra,nbt.getCompound("in"));
		if(!isClient) {
			inv.deserializeNBT(ra,nbt.getCompound("inv"));
			ListTag list=nbt.getList("outBuff",10);
			items=new ArrayList<>();
			for(int i=0;i<list.size();i++) {
				items.add(ItemStack.parse(ra,list.getCompound(i)).orElse(ItemStack.EMPTY));
			}
		}
		isLastHeating=nbt.getBoolean("heating");
	}

	@Override
	public void writeCustomNBT(CompoundTag nbt, boolean isClient,HolderLookup.Provider ra) {
		nbt.putBoolean("heating", isLastHeating);
		recipeHandler.writeCustomNBT(nbt, isClient);
		nbt.put("in",tankin.writeToNBT(ra, new CompoundTag()));
		
		if(!isClient) {
			nbt.put("inv", inv.serializeNBT(ra));
			ListTag tl=new ListTag();
			items.forEach(t->tl.add(t.save(ra)));
			nbt.put("outBuff", tl);
		}
	}

	@Override
	public void handleMessage(short type, int data) {

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
		if(level.isClientSide) {
			return;
		}
		if(!items.isEmpty()) {
			items.replaceAll(t->Utils.insertToOutput(inv,4,Utils.insertToOutput(inv,3,Utils.insertToOutput(inv,2,Utils.insertToOutput(inv,1,t)))));
			items.removeIf(ItemStack::isEmpty);
			this.setChanged();
		}
		
		if(recipeHandler.getProcessMax()>0) {
			boolean preIsLastHeating=isLastHeating;
			boolean ticked=false;
			if (level.getBlockEntity(worldPosition.below()) instanceof IStove stove && stove.canEmitHeat()) {
				ticked=recipeHandler.tickProcess(stove.requestHeat());
				isLastHeating = true;
			}else isLastHeating = false;
			if(preIsLastHeating!=isLastHeating||ticked) {
				this.syncData();
			}
		}
			
		if (level.getBlockEntity(worldPosition.below()) instanceof IStove stove && stove.canEmitHeat()) {
			if(recipeHandler.shouldTestRecipe()) {
				isLastHeating = false;
				
				RecipeHolder<BasinRecipe> recipe=BasinRecipe.testAll(tankin.getFluid(),inv.getStackInSlot(0),this.getBlockState().is(CVBlocks.lead_basin.get()));
				if(recipe!=null) {
					recipeHandler.setRecipe(recipe);
					this.syncData();
				}
			}
			
		}
	}


	@Override
	public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
		return new BasinContainer(pContainerId, pInventory, this);
	}

	@Override
	public Component getDisplayName() {
		return Utils.translate("container." + CVMain.MODID + ".basin.title");
	}

	@Override
	public Object getCapability(BlockCapability<?, Direction> type, Direction d) {

		if(type==Capabilities.FluidHandler.BLOCK)
			return tankin;
		if(type==Capabilities.ItemHandler.BLOCK) {
			if (d == Direction.DOWN)
				return new RangedWrapper(inv, 3, 6);
			return new RangedWrapper(inv, 0, 3);
		}
		return super.getCapability(type, d);
	}
}
