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

package com.khjxiaogu.convivium.blocks.pestle_and_mortar;

import com.khjxiaogu.convivium.CVBlockEntityTypes;
import com.khjxiaogu.convivium.CVMain;
import com.khjxiaogu.convivium.blocks.kinetics.KineticTransferBlockEntity;
import com.khjxiaogu.convivium.data.recipes.GrindingRecipe;
import com.khjxiaogu.convivium.util.RotationUtils;
import com.teammoeg.caupona.util.RecipeHandleStatus;
import com.teammoeg.caupona.util.RecipeHandler;
import com.teammoeg.caupona.util.SizedOrCatalystIngredient;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.RangedResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class PamBlockEntity extends KineticTransferBlockEntity implements MenuProvider {
	public ItemStacksResourceHandler inv = new ItemStacksResourceHandler(6) {
		@Override
		public boolean isValid(int slot, ItemResource stack) {
			return slot >=3||GrindingRecipe.testInput(stack.toStack());
		}

		@Override
		protected void onContentsChanged(int slot,ItemStack stack) {
			super.onContentsChanged(slot,stack);
			recipeHandler.onContainerChanged();
			syncData();
		}
	};
	public final FluidStacksResourceHandler tanks=new FluidStacksResourceHandler(2,1000) {

		@Override
		protected void onContentsChanged(int index, FluidStack previousContents) {
			super.onContentsChanged(index, previousContents);
			if(index==0)
				recipeHandler.onContainerChanged();
			syncData();
		}
		
	};

	public RecipeHandler<GrindingRecipe> recipeHandler=new RecipeHandler<>(rcp->{
		RecipeHolder<GrindingRecipe> recipe=GrindingRecipe.recipes.get(rcp);
		if(recipe!=null) {
			try(Transaction trans=Transaction.openRoot()){
				for (SizedOrCatalystIngredient igd :recipe.value().items) {
					if (igd.count() == 0)
						continue;
					int count=igd.count();
					for (int i = 0; i < 3; i++) {
						ItemResource ir=inv.getResource(i);
						ItemStack is = ir.toStack(inv.getAmountAsInt(i));
						if (igd.test(is)) {
							count-=inv.extract(i, ir, count, trans);
						}
						if(count<=0)
							break;
					}
					if(count>0)
						return RecipeHandleStatus.FAILED;
				}
				if(recipe.value().in!=null&&recipe.value().in.amount()>0) {
					FluidResource fr=tanks.getResource(0);
					if(tanks.extract(0, fr, recipe.value().in.amount(), trans)!=recipe.value().in.amount())
						return RecipeHandleStatus.FAILED;
				}
				for(ItemStackTemplate is:recipe.value().output) {
					ItemResource ir=ItemResource.of(is);
					int countRemain=is.count();
					for (int i = 3; i < 6; i++) {
						countRemain-=inv.insert(i, ir, countRemain, trans);
						if(countRemain<=0)
							break;
					}
					if(countRemain>0)
						return RecipeHandleStatus.BLOCKED;
				}
				if(recipe.value().out.isPresent()) {
					FluidResource fr=FluidResource.of(recipe.value().out.get());
					int count=recipe.value().out.get().amount();
					if(recipe.value().keepInfo) {
						fr=fr.withMergedPatch(tanks.getResource(0).getComponentsPatch());
					}
					if(tanks.insert(1, fr, count, trans)!=count) {
						return RecipeHandleStatus.BLOCKED;
					}
				}
					trans.commit();
					return RecipeHandleStatus.SUCCEED;
				
			}
		}
		return RecipeHandleStatus.FAILED;
	});

	public PamBlockEntity( BlockPos pWorldPosition, BlockState pBlockState) {
		super(CVBlockEntityTypes.PAM.get(), pWorldPosition, pBlockState);
	}

	@Override
	public void readCustomNBT(ValueInput nbt, boolean isClient) {
		super.readCustomNBT(nbt, isClient);
		nbt.readChild("tanks", tanks);
		nbt.readChild("inv", inv);
	}

	@Override
	public void writeCustomNBT(ValueOutput nbt, boolean isClient) {
		// TODO Auto-generated method stub
		super.writeCustomNBT(nbt, isClient);
		nbt.putChild("tanks", tanks);
		nbt.putChild("inv", inv);
		
	}

	@Override
	public void handleMessage(short type, int data) {

	}
	@Override
	public boolean isReceiver() {
		// TODO Auto-generated method stub
		return true;
	}
	public void spawnParticleFor(ItemResource is) {
		if(is.isEmpty())return;
		ItemParticleOption data = new ItemParticleOption(ParticleTypes.ITEM, is.getItem());
		Vec3 rot=new Vec3(0,0,1).yRot((float) RotationUtils.getRotationAngle(0,this.getBlockPos())).scale(0.3f);
		Vec3 center = Vec3.atCenterOf(this.getBlockPos()).add(rot);
		Vec3 target=Vec3.ZERO.offsetRandom(this.level.getRandom(),0.2f).multiply(1,0,1).add(0, 0.2, 0);
		level.addParticle(data, center.x, center.y, center.z, target.x, target.y, target.z);
	}
	@Override
	public void tick() {
		// TODO Auto-generated method stub
		super.tick();
		if(level.isClientSide()) {
			if(recipeHandler.getProcess()!=0)
				for(int i=0;i<3;i++) {
					ItemResource stackInSlot = inv.getResource(i);
					if (!stackInSlot.isEmpty()){
						if(Math.random()<0.05)
							spawnParticleFor(stackInSlot);
					}
				}
			return;
		}
		
		if(recipeHandler.shouldTestRecipe()){
			RecipeHolder<GrindingRecipe> recipe=GrindingRecipe.test(tanks, inv);
			if(recipe!=null) {
				recipeHandler.setRecipe(recipe, recipe.value().processTime);
				this.syncData();
			}
		}else
		if(recipeHandler.getProcessMax()>0) {
			if(recipeHandler.tickProcess(getSpeed()))
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
		if (type == Capabilities.Item.BLOCK) {
			if (d == Direction.DOWN)
				return RangedResourceHandler.of(inv, 3, 6);
			return RangedResourceHandler.of(inv, 0, 3);
		}
		if (type == Capabilities.Fluid.BLOCK)
			return tanks;
		return super.getCapability(type, d);
	}
}
