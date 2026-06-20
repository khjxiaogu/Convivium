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

package com.khjxiaogu.convivium.blocks.whisk;

import java.util.ArrayList;
import java.util.List;
import org.jspecify.annotations.Nullable;

import com.khjxiaogu.convivium.CVBlockEntityTypes;
import com.khjxiaogu.convivium.CVComponents;
import com.khjxiaogu.convivium.CVMain;
import com.khjxiaogu.convivium.CVTags;
import com.khjxiaogu.convivium.blocks.kinetics.KineticTransferBlockEntity;
import com.khjxiaogu.convivium.data.recipes.BeverageTypeRecipe;
import com.khjxiaogu.convivium.data.recipes.ConvertionRecipe;
import com.khjxiaogu.convivium.data.recipes.TasteRecipe;
import com.khjxiaogu.convivium.util.BeverageInfo;
import com.khjxiaogu.convivium.util.BeveragePendingContext;
import com.khjxiaogu.convivium.util.CurrentSwayInfo;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.teammoeg.caupona.CPCapability;
import com.teammoeg.caupona.CPConfig;
import com.teammoeg.caupona.api.events.ContanerContainFoodEvent;
import com.teammoeg.caupona.blocks.stove.IStove;
import com.teammoeg.caupona.util.IInfinitable;
import com.teammoeg.caupona.util.LazyTickWorker;
import com.teammoeg.caupona.util.RecipeHandleStatus;
import com.teammoeg.caupona.util.RecipeHandler;
import com.teammoeg.caupona.util.TwoSlotItemAccess;
import com.teammoeg.caupona.util.Utils;

import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.UseRemainder;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.DelegatingResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class WhiskBlockEntity extends KineticTransferBlockEntity implements IInfinitable, MenuProvider {
	public enum HeatingStatus{
		ON(Utils.translate("gui." + CVMain.MODID + ".whisk.heat_on")),
		OFF(Utils.translate("gui." + CVMain.MODID + ".whisk.heat_off")),
		REDSTONE(Utils.translate("gui." + CVMain.MODID + ".whisk.heat_redstone"));
		public final Component text;

		private HeatingStatus(Component text) {
			this.text = text;
		}
	}
	public List<CurrentSwayInfo> swayhint = new ArrayList<>();
	public static Codec<List<CurrentSwayInfo>> CSI_CODEC = Codec.list(CurrentSwayInfo.CODEC);
	public int process;
	public int processMax;
	public boolean isHeating;
	public HeatingStatus heating=HeatingStatus.OFF;
	public boolean inf;
	public FluidResource target;
	public int targetAmount;
	public static record ResultData(int process, int processMax, FluidResource target, int targetAmount,List<CurrentSwayInfo> swayhint) {}
	public RecipeHandler<ConvertionRecipe> convertion=new RecipeHandler<>(this::handleRecipe);
	public FluidStacksResourceHandler tank = new FluidStacksResourceHandler(1,1250) {

		@Override
		protected void onContentsChanged(int index, FluidStack previousContents) {
			super.onContentsChanged(index, previousContents);
			convertion.onContainerChanged();
			syncData();
		}
	};
	public ItemStacksResourceHandler inv = new ItemStacksResourceHandler(2) {
		@Override
		protected void onContentsChanged(int slot,ItemStack stack) {
			super.onContentsChanged(slot,stack);
			if (slot == 0) {
				convertion.onContainerChanged();
			}
			syncData();
		}
		@Override
		public long getCapacityAsLong(int index, ItemResource resource) {
			if (index == 0)
				return 1;
			return super.getCapacityAsLong(index, resource);
		}
	};
	
	SnapshotJournal<ResultData> swayInfo=new SnapshotJournal<>() {

		@Override
		protected ResultData createSnapshot() {
			return new ResultData(process,processMax,target,targetAmount,swayhint);
		}

		@Override
		protected void revertToSnapshot(ResultData snapshot) {
			swayhint=snapshot.swayhint();
			process=snapshot.process();
			processMax=snapshot.processMax();
			target=snapshot.target();
			targetAmount=snapshot.targetAmount();
		}

		@Override
		protected void onRootCommit(ResultData originalState) {
			super.onRootCommit(originalState);
			if(processMax>0||target!=null)
				convertion.resetProgress();
			syncData();
		}

		
	};
	public RecipeHandleStatus handleRecipe(Identifier id) {
		
		RecipeHolder<ConvertionRecipe> recipe=ConvertionRecipe.recipes.get(id);
		if(recipe!=null) {
			FluidResource resource=tank.getResource(0);
			int amount=tank.getAmountAsInt(0);
			ItemResource stack=inv.getResource(0);
			try(Transaction trans=Transaction.openRoot()){
				swayInfo.updateSnapshots(trans);
				if(inv.extract(amount, stack, 1, trans)==1) {
					BeverageInfo info=getOrCreateCopy(resource);
					int total=0;
					for(int ent:info.relishes.values()) {
						total+=ent;
					}
					List<FluidStack> stacks=new ArrayList<>();
					for(Entry<Holder<Fluid>> fs:info.relishes.object2IntEntrySet())
						stacks.add(new FluidStack(fs.getKey(),amount*fs.getIntValue()/total));
					if(recipe.value().item.isPresent()) {
						if(!recipe.value().item.get().test(stack.toStack()))
							return RecipeHandleStatus.FAILED;
					}
					for(FluidStack fs:stacks) {
						if(recipe.value().in.test(fs)) {
							info.exchangeRelish(amount/250,fs.typeHolder(), recipe.value().out.typeHolder(), recipe.value().in.amount()/250);
							setFluid(info,resource,amount,0);
							trans.commit();
							return RecipeHandleStatus.SUCCEED;
						}
					}
				}
			}
		}
		return RecipeHandleStatus.FAILED;
		
	}
	public static BeverageInfo getOrCreateCopy(FluidResource resource) {
		BeverageInfo info = resource.get(CVComponents.BEVERAGE_INFO);
		if (info == null) {
			info=new BeverageInfo();
			info.relishes.put(resource.typeHolder(), 1);
			info.checkFluidType();
		}else {
			return info.copy();
		}
		return info;
	}
	public static BeverageInfo getOrCreate(FluidResource resource) {
		BeverageInfo info = resource.get(CVComponents.BEVERAGE_INFO);
		if (info == null) {
			info=new BeverageInfo();
			info.relishes.put(resource.typeHolder(), 1);
			info.checkFluidType();
		}
		return info;
	}
	public void setFluid(BeverageInfo info,FluidResource orig,int amount,int time) {
		Pair<List<CurrentSwayInfo>, Either<BeverageTypeRecipe, Fluid>> swi = info.handleSway();
		setSwayhint(swi.getFirst());
		Either<BeverageTypeRecipe, Fluid> right=swi.getSecond();
		info.completeData();
		target=FluidResource.of(right.<Fluid>map(t->t.output, t->t),orig.getComponentsPatch()).with(CVComponents.BEVERAGE_INFO, info);
		processMax=time;
		right.ifLeft(t->{
			if(t.output!=orig.getFluid())
				processMax+=t.time;
		});
		targetAmount=amount;
	}
	public DelegatingResourceHandler<FluidResource> modtank=new DelegatingResourceHandler<>(tank) {
		@Override
		public int extract(FluidResource resource, int amount, TransactionContext transaction) {

			return extract(0,resource, amount, transaction);
		}
		@Override
		public int insert(FluidResource resource, int amount, TransactionContext transaction) {

			return insert(0,resource, amount, transaction);
		}
		@Override
		public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
			if(target!=null||processMax>0)
				return 0;
			int beforeAmount=super.getAmountAsInt(index);
			int ins=super.insert(index, resource, amount, transaction);
			swayInfo.updateSnapshots(transaction);
			if (ins != 0) {// add fluid
				BeverageInfo info = getOrCreate(resource);
				BeveragePendingContext context = new BeveragePendingContext(info);
				setSwayhint(context.getSwayHint());
				
				return ins;
			} else if (tank.isValid(index,resource)&&beforeAmount<1250) {
				if (getSpeed() == 0)// not stiring, can not mix
					return 0;
				amount=Math.min(amount, 1250-beforeAmount);
				FluidResource orig=super.getResource(index);
				BeverageInfo info = getOrCreateCopy(orig);
				BeverageInfo ninfo = getOrCreate(resource);
				if(ninfo.relishes.equals(info.relishes)) {
					info.merge(ninfo, beforeAmount/250f, amount/250f);
					setFluid(info,orig,beforeAmount+amount,0);
					return amount;
				}
				if(amount >= 250&&beforeAmount%250==0) {// mix new relish fluid
					int beforeParts=beforeAmount/250;
					int insertParts=amount/250;
					int toAdd=info.addableRelish(beforeParts);
					System.out.println(toAdd);
					int toAdd2=ninfo.addableRelish(insertParts,beforeParts);
					System.out.println(toAdd2);
					if(toAdd2<=toAdd) {
						info.addRelishes(beforeParts, ninfo.relishes, toAdd2);
						info.merge(ninfo, beforeParts, toAdd2);
						setFluid(info,orig,beforeAmount+toAdd2*250,40);
						System.out.println("succeed");
						return toAdd2*250;
					}
					return 0;
				}
				
			}
			return 0;
		}

		@Override
		public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
			if(target!=null||processMax>0)
				return 0;
			return super.extract(index, resource, amount, transaction);
		}
		
	};
	public LazyTickWorker contain;

	public WhiskBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
		super(CVBlockEntityTypes.WHISK.get(), pWorldPosition, pBlockState);
		contain = new LazyTickWorker(CPConfig.SERVER.containerTick.get(), () -> {
			if (processMax == 0&&!convertion.shouldTick()) {
				if (tryContianFluid())
					return true;
			}
			return false;
		});

	}
	@Override
	public void readCustomNBT(ValueInput nbt, boolean isClient) {
		super.readCustomNBT(nbt, isClient);
		swayhint = nbt.read("hint", CSI_CODEC).orElse(List.of());
		process = nbt.getIntOr("process",0);
		processMax = nbt.getIntOr("processMax",0);
		isHeating = nbt.getBooleanOr("heating",false);
		heating=HeatingStatus.values()[nbt.getIntOr("heatstatus", 0)];
		inf = nbt.getBooleanOr("inf",false);
		target = nbt.read("target", FluidResource.CODEC).orElse(null);
		targetAmount = nbt.getIntOr("targetAmount", targetAmount);
		nbt.readChild("tank", tank);
		nbt.readChild("inv", inv);
	}

	@Override
	public void writeCustomNBT(ValueOutput nbt, boolean isClient) {
		super.writeCustomNBT(nbt, isClient);
		nbt.storeNullable("hint", CSI_CODEC, swayhint);
		nbt.putInt("process", process);
		nbt.putInt("processMax", processMax);
		nbt.putInt("heatstatus", heating.ordinal());
		nbt.putBoolean("heating", isHeating);
		nbt.putBoolean("inf", inf);
		nbt.storeNullable("target", FluidResource.CODEC, target);
		nbt.putInt("targetAmount", targetAmount);
		nbt.putChild("tank", tank);
		nbt.putChild("inv", inv);
	}
	private boolean tryContianFluid() {
		ItemResource container=inv.getResource(0);
		if(!container.isEmpty()) {
			try(Transaction trans=Transaction.openRoot()){
				if (!inf) {
					ItemStack containerStack=container.toStack();
					@Nullable ResourceHandler<FluidResource> cap=containerStack.getCapability(Capabilities.Fluid.ITEM,new TwoSlotItemAccess(inv, 0,1));
					if(cap!=null) {
						int amt=cap.getAmountAsInt(0);
						if (ResourceHandlerUtil.move(cap, modtank, _->true, amt, trans)>0) {
							trans.commit();
							return true;
						}else if (ResourceHandlerUtil.move(modtank, cap, _->true, amt, trans)>0) {
							trans.commit();
							return true;
						}
					}
				}
			}
			try(Transaction trans=Transaction.openRoot()){
				if(modtank.getAmountAsInt(0)>=250) {
					FluidResource rs=modtank.getResource(0);
					int itemCount=inv.extract(0, container, 1, trans);
					int fluidAmount=modtank.extract(rs, 250, trans);
					if(itemCount>0&&fluidAmount>=250) {
						ContanerContainFoodEvent result=Utils.contain(container,rs,fluidAmount);
						if(result.isAllowed()) {
							if(inv.insert(1,result.getOutput(), 1, trans)==1) {
								trans.commit();
								return true;
							}
						}
					}
				}
			}
			
		}
		return false;
	}
	public boolean isValidInput(ItemStack is) {
		return is.is(CVTags.Items.BEVERAGE_MATERIAL) || TasteRecipe.recipes.stream().map(t -> t.value()).anyMatch(t -> t.item.test(is))
			|| ConvertionRecipe.recipes.values().stream().map(t -> t.value()).filter(t->t.item.isPresent())
				.flatMap(t -> t.item.stream()).anyMatch(t -> t.test(is));
	}

	public void tryMixItems() {
		ItemResource itemCur=inv.getResource(0);
		if(itemCur.isEmpty())
			return;
		int amount=tank.getAmountAsInt(0);
		float part=amount/250f;
		FluidResource fr=tank.getResource(0);
		BeverageInfo info = getOrCreateCopy(tank.getResource(0));
		try(Transaction trans=Transaction.openRoot()){
			swayInfo.updateSnapshots(trans);
			
			inv.extract(0, itemCur, 1, trans);
			if(itemCur.is(Items.POTION)) {
				UseRemainder out=itemCur.get(DataComponents.USE_REMAINDER);
				if(out!=null&&inv.insert(1, ItemResource.of(out.convertInto()), 1, trans)!=1)
					return;
				for (MobEffectInstance eff : itemCur.get(DataComponents.POTION_CONTENTS).getAllEffects())
					info.addEffect(eff, part);
			}else{
				ItemStack curStack=itemCur.toStack();
				boolean hasRecipe=false;
				for(RecipeHolder<TasteRecipe> ti:TasteRecipe.recipes) {
					if(ti.value().item.test(curStack)) {
						hasRecipe=true;
						break;
					}
				}
				if(!(hasRecipe&&info.addItem(curStack, part))) {
					return;
				}
			}
			setFluid(info,fr,amount,40);
			trans.commit();
			syncData();
		}
		
	}

	@Override
	public void tick() {
		super.tick();
		if (level.isClientSide())
			return;
		contain.tick();
		boolean lastIsHeating=isHeating;
		isHeating=false;
		if(heating==HeatingStatus.ON||(heating==HeatingStatus.REDSTONE&&level.hasNeighborSignal(worldPosition))) {
			isHeating=true;
		}
		if(lastIsHeating!=isHeating) {
			convertion.onContainerChanged();
		}
		if(getSpeed() > 0) {
			if(processMax>0) {
				if(process<processMax) {
					process++;
					this.setChanged();
					return;
				}
				processMax=process=0;
			}
			if(target!=null) {
				tank.set(0, target, targetAmount);
				target=null;
				targetAmount=0;
				this.syncData();
				return;
			}
			if(convertion.shouldTestRecipe()) {
				ItemStack stack=ItemUtil.getStack(inv, 0);
				BeverageInfo info=getOrCreate(tank.getResource(0));
				RecipeHolder<ConvertionRecipe> recipe=ConvertionRecipe.test(stack,info,tank.getAmountAsInt(0),isHeating);
				if(recipe==null)
					convertion.setRecipe(null, 0);
				else
					convertion.setRecipe(recipe, recipe.value().processTime);
				this.setChanged();
			}
			Identifier id=convertion.getLastRecipe();
			if(id!=null) {
				RecipeHolder<ConvertionRecipe> recipe=ConvertionRecipe.recipes.get(id);
				if(!convertion.isRecipeFinished()) {
					try(Transaction trans=Transaction.openRoot()){
						int actual=getSpeed();
						if(recipe!=null&&recipe.value().heated&&level.getCapability(CPCapability.HEAT_STOVE,worldPosition.below(), Direction.UP)instanceof IStove stove) {
							if(isHeating)
								actual=stove.requestHeat(actual, trans);
							else
								actual=0;
						}
						if(actual>0&&convertion.tickProcess(actual)) {
							trans.commit();
						}
					}
				}else {
					convertion.tickProcess(1);
				}
				this.setChanged();
			}else {
				tryMixItems();
			}
			
		}
	}

	@Override
	public Object getCapability(BlockCapability<?, Direction> type, Direction d) {
		if (type == Capabilities.Item.BLOCK) {
			return inv;
		}
		if (type == Capabilities.Fluid.BLOCK)
			return modtank;
		return super.getCapability(type, d);
	}
	public List<CurrentSwayInfo> getSwayhint() {
		return swayhint;
	}

	public void setSwayhint(List<CurrentSwayInfo> swayhint) {
		this.swayhint=new ArrayList<>(5);
		int num1 = 0;
		int num2 = 0;
		for (CurrentSwayInfo hint : swayhint) {
			if (hint.getActive() > 0) {
				if (num1++ < 3) {
					this.swayhint.add(hint);
				}
			} else {
				if (num2++ < 3) {
					this.swayhint.add(hint);
				}
			}
		}
	}
	@Override
	public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
		return new WhiskContainer(pContainerId, pPlayerInventory, this);
	}

	@Override
	public Component getDisplayName() {
		return Utils.translate("container." + CVMain.MODID + ".whisk.title");
	}

	@Override
	public boolean setInfinity() {
		return inf = !inf;
	}
	@Override
	public boolean isInfinite() {
		return inf;
	}

	@Override
	public boolean isReceiver() {
		return true;
	}
	@Override
	public void handleMessage(short type, int data) {
		this.heating=HeatingStatus.values()[(data+1)%3];
	}
}
