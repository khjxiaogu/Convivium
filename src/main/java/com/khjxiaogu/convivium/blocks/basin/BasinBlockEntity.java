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

import com.khjxiaogu.convivium.CVBlockEntityTypes;
import com.khjxiaogu.convivium.CVBlocks;
import com.khjxiaogu.convivium.CVMain;
import com.khjxiaogu.convivium.data.recipes.BasinRecipe;
import com.khjxiaogu.convivium.util.RotationUtils;
import com.teammoeg.caupona.CPCapability;
import com.teammoeg.caupona.blocks.stove.IStove;
import com.teammoeg.caupona.network.CPBaseBlockEntity;
import com.teammoeg.caupona.util.RecipeHandleStatus;
import com.teammoeg.caupona.util.RecipeHandler;
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

public class BasinBlockEntity extends CPBaseBlockEntity implements MenuProvider {
	public ItemStacksResourceHandler inv = new ItemStacksResourceHandler(5) {

		@Override
		protected void onContentsChanged(int index, ItemStack previousContents) {
			super.onContentsChanged(index, previousContents);
			if(index<1)
				recipeHandler.onContainerChanged();
			setChanged();
		}
		@Override
		public boolean isValid(int index, ItemResource resource) {
			return index > 0||BasinRecipe.testInput(resource.toStack());
		}
	};
	public final FluidStacksResourceHandler tankin = new FluidStacksResourceHandler(1,1000) {
		@Override
		protected void onContentsChanged(int index, FluidStack previousContents) {
			super.onContentsChanged(index, previousContents);
			recipeHandler.onContainerChanged();
			syncData();
		}
		
	};
	public RecipeHandler<BasinRecipe> recipeHandler=new RecipeHandler<>(id->{
		FluidResource fr=tankin.getResource(0);
		int fluidCount=tankin.getAmountAsInt(0);
		FluidStack fluid=fr.toStack(fluidCount);
		ItemResource ir=inv.getResource(0);
		int itemCount=inv.getAmountAsInt(0);
		ItemStack is=ir.toStack(itemCount);
		RecipeHolder<BasinRecipe> recipe=BasinRecipe.testAll(id,fluid,is,this.getBlockState().is(CVBlocks.LEAD_BASIN.get()));
		if(recipe!=null) {
			int itemIn=recipe.value().item.count();
			int fluidIn=recipe.value().in.amount();
			try(Transaction trans=Transaction.openRoot()){
				if(tankin.extract(0, fr, fluidIn, trans)==fluidIn) {
					if(inv.extract(0, ir, itemIn, trans)==itemIn) {
						for(ItemStackTemplate ist:recipe.value().output) {
							ItemResource iro=ItemResource.of(ist);
							int amt=ist.count();
							for(int i=1;i<5;i++) {
								amt-=inv.insert(0, iro, amt, trans);
								if(amt<=0)
									break;
							}
							if(amt>0)
								return RecipeHandleStatus.BLOCKED;
						}
						return RecipeHandleStatus.SUCCEED;
					}
				}
			
			}
		}
		return RecipeHandleStatus.FAILED;
	});
	public boolean isLastHeating;
	public BasinBlockEntity( BlockPos pWorldPosition, BlockState pBlockState) {
		super(CVBlockEntityTypes.BASIN.get(), pWorldPosition, pBlockState);
	}

	@Override
	public void readCustomNBT(ValueInput nbt, boolean isClient) {
		recipeHandler.readCustomNBT(nbt, isClient);
		nbt.readChild("tank", tankin);
		if(!isClient) {
			nbt.readChild("inv", inv);
		}
		isLastHeating=nbt.getBooleanOr("heating",false);
	}

	@Override
	public void writeCustomNBT(ValueOutput nbt, boolean isClient) {
		nbt.putBoolean("heating", isLastHeating);
		recipeHandler.writeCustomNBT(nbt, isClient);
		nbt.putChild("tank", tankin);
		
		if(!isClient) {
			nbt.putChild("inv", inv);
		}
	}

	@Override
	public void handleMessage(short type, int data) {

	}
	public void spawnParticleFor(ItemStack is) {
		if(is.isEmpty())return;
		ItemParticleOption data = new ItemParticleOption(ParticleTypes.ITEM, ItemStackTemplate.fromNonEmptyStack(is));
		Vec3 rot=new Vec3(0,0,1).yRot((float) RotationUtils.getRotationAngle(0,this.getBlockPos())).scale(0.3f);
		Vec3 center = Vec3.atCenterOf(this.getBlockPos()).add(rot);
		Vec3 target=Vec3.ZERO.offsetRandom(this.level.getRandom(),0.2f).multiply(1,0,1).add(0, 0.2, 0);
		level.addParticle(data, center.x, center.y, center.z, target.x, target.y, target.z);
	}
	@Override
	public void tick() {
		// TODO Auto-generated method stub
		if(level.isClientSide()) {
			return;
		}
		
		if(recipeHandler.getProcessMax()>0) {
			boolean preIsLastHeating=isLastHeating;
			boolean ticked=false;
			if (level.getCapability(CPCapability.HEAT_STOVE, worldPosition.below(), Direction.UP) instanceof IStove stove && stove.canEmitHeat()) {
				try(Transaction trans=Transaction.openRoot()){
					ticked=recipeHandler.tickProcess(stove.requestHeat(2,trans));
					if(ticked) {
						trans.commit();
						this.setChanged();
					}
				}
				isLastHeating = true;
			}else isLastHeating = false;
			if(preIsLastHeating!=isLastHeating||ticked) {
				this.syncData();
			}
		}
			
		if (level.getCapability(CPCapability.HEAT_STOVE, worldPosition.below(), Direction.UP) instanceof IStove stove && stove.canEmitHeat()) {
			if(recipeHandler.shouldTestRecipe()) {
				isLastHeating = false;
				FluidStack fluid=tankin.getResource(0).toStack(tankin.getAmountAsInt(0));
				ItemStack is=inv.getResource(0).toStack(inv.getAmountAsInt(0));
				RecipeHolder<BasinRecipe> recipe=BasinRecipe.testAll(fluid,is,this.getBlockState().is(CVBlocks.LEAD_BASIN.get()));
				if(recipe!=null) {
					recipeHandler.setRecipe(recipe,recipe.value().processTime);
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

		if(type==Capabilities.Fluid.BLOCK)
			return tankin;
		if(type==Capabilities.Item.BLOCK) {
			if (d == Direction.DOWN)
				return RangedResourceHandler.of(inv, 1, 5);
			return RangedResourceHandler.of(inv, 0, 1);
		}
		return super.getCapability(type, d);
	}
}
