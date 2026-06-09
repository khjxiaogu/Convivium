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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import com.khjxiaogu.convivium.CVBlockEntityTypes;
import com.khjxiaogu.convivium.CVComponents;
import com.khjxiaogu.convivium.CVMain;
import com.khjxiaogu.convivium.CVTags;
import com.khjxiaogu.convivium.blocks.kinetics.KineticTransferBlockEntity;
import com.khjxiaogu.convivium.data.recipes.ConvertionRecipe;
import com.khjxiaogu.convivium.data.recipes.RelishFluidRecipe;
import com.khjxiaogu.convivium.data.recipes.TasteRecipe;
import com.khjxiaogu.convivium.util.BeverageInfo;
import com.khjxiaogu.convivium.util.BeveragePendingContext;
import com.khjxiaogu.convivium.util.CurrentSwayInfo;
import com.khjxiaogu.convivium.util.FloatSizedOrCatalystIngredient;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.teammoeg.caupona.CPConfig;
import com.teammoeg.caupona.api.CauponaApi;
import com.teammoeg.caupona.api.events.ContanerContainFoodEvent;
import com.teammoeg.caupona.blocks.stove.IStove;
import com.teammoeg.caupona.util.FloatemStack;
import com.teammoeg.caupona.util.IInfinitable;
import com.teammoeg.caupona.util.LazyTickWorker;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class WhiskBlockEntity extends KineticTransferBlockEntity implements IInfinitable, MenuProvider {
	public ItemStacksResourceHandler inv = new ItemStacksResourceHandler(6) {
		@Override
		public boolean isValid(int index, ItemResource resource) {
			if (index < 4)
				return isValidInput(resource.toStack());
			return super.isValid(index, resource);
		}
		@Override
		protected void onContentsChanged(int slot,ItemStack stack) {
			super.onContentsChanged(slot,stack);
			if (slot < 4) {
				resetAdding();
			}
			syncData();
		}
		@Override
		public long getCapacityAsLong(int index, ItemResource resource) {
			if (index < 4)
				return 1;
			return super.getCapacityAsLong(index, resource);
		}
	};
	public static final int IDLE = 0;
	public static final int ADDING_INGREDIENT = 1;
	public static final int MIXING = 2;
	public static final int HEATING = 3;
	public FluidStacksResourceHandler tank = new FluidStacksResourceHandler(1,1250) {
		@Override
		public boolean isValid(int index, FluidResource resource) {
			return RelishFluidRecipe.recipes.containsKey(resource.getFluid()) || resource.has(CVComponents.BEVERAGE_INFO);
		}
	};
	public List<CurrentSwayInfo> swayhint = new ArrayList<>();
	public static Codec<List<CurrentSwayInfo>> CSI_CODEC = Codec.list(CurrentSwayInfo.CODEC);
	public static final int MAX_DENSE = 3;
	public int process;
	public int processMax;
	public int status;
	public int heating;
	public int temperature;
	public int lastProcess;
	public boolean isHeating;
	public boolean rs;
	public boolean inf;
	public boolean isLastHeating;
	public boolean recipeTested;
	public FluidStack target;
	public LazyTickWorker contain;

	public WhiskBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
		super(CVBlockEntityTypes.WHISK.get(), pWorldPosition, pBlockState);
		contain = new LazyTickWorker(CPConfig.SERVER.containerTick.get(), () -> {
			if (processMax == 0) {
				if (tryContianFluid())
					return true;
			}
			return false;
		});

	}

	protected void resetAdding() {
		if(status==ADDING_INGREDIENT) {
			target=null;
			process=processMax=0;
			status=IDLE;
			
			
		}
		lastProcess=0;
		recipeTested=false;
	}

	@Override
	public void readCustomNBT(ValueInput nbt, boolean isClient) {
		super.readCustomNBT(nbt, isClient);
		swayhint = new ArrayList<>(
			CSI_CODEC.decode(NbtOps.INSTANCE, nbt.get("hint")).result().map(Pair::getFirst).orElse(List.of()));
		process = nbt.getInt("process");
		processMax = nbt.getInt("processMax");
		status = nbt.getInt("status");
		heating = nbt.getInt("heat");
		isHeating = nbt.getBoolean("heating");
		rs = nbt.getBoolean("rs");
		inf = nbt.getBoolean("inf");
		isLastHeating = nbt.getBoolean("last_heat");
		temperature = nbt.getInt("temperature");
		if (nbt.contains("target"))
			target = FluidStack.parseOptional(ra, nbt.getCompound("target"));
		else
			target=null;
		tank.readFromNBT(ra, nbt.getCompound("tank"));
		if(!isClient) {
			inv.deserializeNBT(ra, nbt.getCompound("inv"));
			temperature = nbt.getInt("temperature");
		}
		
		lastProcess=nbt.getInt("lastProcess");

	}

	@Override
	public void writeCustomNBT(CompoundTag nbt, boolean isClient, HolderLookup.Provider ra) {
		super.writeCustomNBT(nbt, isClient, ra);
		if (swayhint != null)
			CSI_CODEC.encodeStart(NbtOps.INSTANCE, swayhint).result().ifPresent(t -> nbt.put("hint", t));

		nbt.putInt("process", process);
		nbt.putInt("processMax", processMax);
		nbt.putInt("status", status);
		nbt.putInt("heat", heating);
		nbt.putBoolean("heating", isHeating);
		nbt.putBoolean("rs", rs);
		nbt.putBoolean("inf", inf);
		nbt.putBoolean("last_heat", isLastHeating);
		
		if (target != null)
			nbt.put("target", target.saveOptional(ra));
		
		nbt.put("tank", tank.writeToNBT(ra, new CompoundTag()));
		if(!isClient) {
			nbt.put("inv", inv.serializeNBT(ra));
			nbt.putInt("temperature", temperature);
		}
	}

	@Override
	public void handleMessage(short type, int data) {
		if (type == 0) {
			rs = data != 0;
		} else if (type == 1) {
			isHeating = data != 0;
		}
		this.syncData();
	}

	private boolean tryContianFluid() {
		try(Transaction trans=Transaction.openRoot()){
			if(tank.getAmountAsInt(0)>=250) {
				ItemResource container=inv.getResource(4);
				FluidResource rs=tank.getResource(0);
				int itemCount=inv.extract(9, container, 1, trans);
				int fluidAmount;
				try(Transaction sub=Transaction.open(trans)){
					fluidAmount=tank.extract(rs, 250, sub);
					if(!inf)
						sub.commit();
				}
				if(itemCount>0&&fluidAmount>=250) {
					ContanerContainFoodEvent result=Utils.contain(container,rs,fluidAmount);
					if(result.isAllowed()) {
						if(inv.insert(5,result.getOutput(), 1, trans)==1) {
							trans.commit();
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean isReceiver() {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean isValidInput(ItemStack is) {
		return is.is(CVTags.Items.BEVERAGE_MATERIAL) || TasteRecipe.recipes.stream().map(t -> t.value()).anyMatch(t -> t.item.test(is))
			|| ConvertionRecipe.recipes.stream().map(t -> t.value()).filter(t -> t.items != null)
				.flatMap(t -> t.items.stream()).anyMatch(t -> t.test(is));
	}

	public void tryMixItems() {
		BeverageInfo info = tank.getFluid().get(CVComponents.BEVERAGE_INFO);
		
		if (info == null) {// not beverage fluid: create a temporary info
			info = new BeverageInfo();
			FluidStack fs = tank.getFluid();
			int amt = fs.getAmount() / 250;
			for (int i = 0; i < amt; i++) {
				info.relishes[i] = fs.getFluid();
			}
		}else {
			info=info.copy();
		}
		int amt = tank.getFluidAmount() / 250;
		int count=0;
		for (int i = 0; i < 4; i++) {
			ItemStack is = inv.getStackInSlot(i);
			if (!is.isEmpty() && isValidInput(is)) {
				if (is.getItem() != Items.POTION) {
					if (!info.addItem(is, amt))
						return;
					else
						count++;
				} else {
					for (MobEffectInstance eff : is.get(DataComponents.POTION_CONTENTS).getAllEffects())
						info.addEffect(eff, amt);
				}
			}
		}
		if(count==0)return;
		Pair<List<CurrentSwayInfo>, Fluid> val = info.handleSway();
		info.completeData();
		setSwayhint(val.getFirst());
		System.out.println(val.getSecond());
		target = new FluidStack(val.getSecond(), amt * 250);
		target.applyComponents(tank.getFluid().getComponentsPatch());
		target.set(CVComponents.BEVERAGE_INFO, info);
		processMax = 400;
		if(status!=ADDING_INGREDIENT) {
			process = 400;
		}else
			process=Math.max(100, process);
			
		status = ADDING_INGREDIENT;
		syncData();
	}

	public void tryConvertType() {
		Set<ConvertionRecipe> recipes = new HashSet<>();
		for (RecipeHolder<ConvertionRecipe> r : ConvertionRecipe.recipes) {
			if (r.value().temperature <= temperature && r.value().in.getAmount() % 250 == 0 && r.value().out.getAmount() % 250 == 0) {
				recipes.add(r.value());
			}
		}
		BeverageInfo info = tank.getFluid().get(CVComponents.BEVERAGE_INFO);
		FluidStack fluid = tank.getFluid();
		int camt = fluid.getAmount() / 250;
		if (info == null) {// not beverage fluid: create a temporary info
			info = new BeverageInfo();
			for (int i = 0; i < camt; i++) {
				info.relishes[i] = fluid.getFluid();
			}
		}
		if(info.getRelishCount()!=camt)return;
		int tprocessTime=0;
		boolean requireCopy=true;
		nextrcp:for(int inc=0;inc<10;inc++) {
			outer:
			for (ConvertionRecipe r : recipes) {
				int amt=info.getRelishCount();
				List<Fluid> fs = new ArrayList<>();
				if (!r.in.isEmpty()) {
					int nin = r.in.getAmount()/250;
					for (Fluid f : info.relishes) {
						if (nin != 0 && f == r.in.getFluid()) {
							nin--;
							
						}else if(f!=null){
							fs.add(f);
						}
					}
					if (nin>0)
						continue outer;
				}
				int newpart = info.getRelishCount() + r.out.getAmount() / 250 - r.in.getAmount() / 250;
				if (newpart > 5 || newpart <= 0) {
					continue outer;
				}
	
				if (r.items != null && !r.items.isEmpty()) {
	
					for (FloatSizedOrCatalystIngredient i : r.items) {
						boolean flag = true;
						for (FloatemStack stack : info.stacks) {
							if (i.testWithPart(stack, amt)) {
								// tccn -= i.count();
								flag = false;
								break;
							}
						}
						if (flag)
							continue outer;
					}
				}
				// no longer have counting test
				/*
				 * if (r.items != null && !r.items.isEmpty()) {
				 * 
				 * for (FloatSizedOrCatalystIngredient i : r.items) { boolean flag = true; for
				 * (FloatemStack fs : info.stacks) { if (i.testWithPart(fs,amt)) { tccn -=
				 * i.count(); flag = false; break; } } if (flag) break outer; } } if (r.output
				 * != null && !r.output.isEmpty()) { for (FloatemStack i : r.output) { tccn +=
				 * i.getCount(); } } if (tccn * cnt / newpart > MAX_DENSE) break;
				 */
				
				if (!r.out.isEmpty())
					for (int i = 0; i < r.out.getAmount()/250; i++)
						fs.add(r.out.getFluid());
				//recipe ok,copy and prepare to write
				
				if(requireCopy) {
					info=info.copy();
					requireCopy=false;
				}
				for (int i = 0; i < fs.size(); i++) {
					
					info.relishes[i] = fs.get(i);
				}
				int newAmount=fs.size();
				if (r.items != null && !r.items.isEmpty()) {
					
					for (FloatSizedOrCatalystIngredient i : r.items) {
						for (FloatemStack fss : info.stacks) {
							if (i.testWithPart(fss,amt)) {
								fss.shrink(i.count()/amt);
								break;
							}
						}
					}
				}
				if (r.output != null && !r.output.isEmpty()) {
					for (FloatemStack i : r.output) {
						info.addItem(i.copyWithCount(i.getCount()/amt));
					}
				}
				info.adjustParts(amt, newAmount);
				tprocessTime=Math.max(r.processTime,tprocessTime+r.processTime/6);
				continue nextrcp;
			}
			break;
		}
		if(tprocessTime>0) {
			process=processMax=tprocessTime;
			Pair<List<CurrentSwayInfo>, Fluid> res=info.handleSway();
			info.completeData();
			target=new FluidStack(res.getSecond(),info.getRelishCount()*250);
			target.applyComponents(tank.getFluid().getComponentsPatch());
			target.set(CVComponents.BEVERAGE_INFO, info);
			status=MIXING;
		}

	}

	public void applyHeat() {
		if (tank.getFluidAmount() % 250 == 0 && !tank.isEmpty()) {
			if (isHeating) {
				if (temperature < 70) {
					if (level.getBlockEntity(worldPosition.below()) instanceof IStove stove && stove.canEmitHeat()) {
						heating += stove.requestHeat() * getSpeed();
						isLastHeating = true;
					}
				}
				if (heating >= 4 * tank.getFluidAmount() / 250) {
					temperature++;
					if(ConvertionRecipe.activeLevel.contains(temperature))
						tryConvertType();
					heating = 0;
				}
			} else if (temperature != 0) {
				if (heating >= 16 * tank.getFluidAmount() / 250) {// temperature slowly fall to 0
					if (temperature > 0)
						temperature--;
					else
						temperature++;
					heating = 0;
				}

			}
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (level.isClientSide)
			return;
		contain.tick();
		if (rs) {
			isHeating = level.hasNeighborSignal(this.worldPosition);
		}
		boolean ilh = isLastHeating;
		isLastHeating = false;
		if (getSpeed() == 0)
			return;
		if (processMax != 0 && getSpeed() > 0) {
			process -= speed;

			if (process <= 0) {
				process = processMax = 0;
				
				
				if(status==ADDING_INGREDIENT) {
					status=IDLE;
					for(int i=0;i<4;i++) {
						inv.setStackInSlot(i, inv.getStackInSlot(i).getCraftingRemainingItem());
					}
				}
				status=IDLE;
				tank.setFluid(target);
				target=null;
				recipeTested=false;
			}
			this.syncData();
		} 
		if(!recipeTested&&status==IDLE) {
				tryConvertType();
			if(status==IDLE)
				tryMixItems();
			recipeTested=true;
		}
		applyHeat();
		if (isLastHeating != ilh)
			this.syncData();
	}

	ChangeDetectedFluidHandler accessabletank = new ChangeDetectedFluidHandler();

	@Override
	public Object getCapability(BlockCapability<?, Direction> type, Direction d) {
		if (type == Capabilities.ItemHandler.BLOCK) {
			if (d == Direction.DOWN)
				return new RangedWrapper(inv, 5, 6);
			return new RangedWrapper(inv, 0, 5);
		}
		if (type == Capabilities.FluidHandler.BLOCK)
			return accessabletank;
		return super.getCapability(type, d);
	}

	public class ChangeDetectedFluidHandler implements IFluidHandler {
		public ChangeDetectedFluidHandler() {
			super();
		}

		@Override
		public int getTanks() {
			return tank.getTanks();
		}

		@Override
		public FluidStack getFluidInTank(int t) {
			return tank.getFluidInTank(t);
		}

		@Override
		public int getTankCapacity(int t) {
			return tank.getTankCapacity(t);
		}

		@Override
		public boolean isFluidValid(int t, FluidStack stack) {
			return tank.isFluidValid(t, stack);
		}

		@Override
		public int fill(FluidStack resource, FluidAction action) {
			if (status > ADDING_INGREDIENT)
				return 0;
			boolean isEmpty = tank.getFluid().isEmpty();
			int filled = tank.fill(resource, action);
			if (filled != 0) {// add same fluid
				if (action.execute()) {
					if (isEmpty) {// add new fluid: compute and cache sway hint'
						BeverageInfo info = resource.get(CVComponents.BEVERAGE_INFO);
						if (info != null) {
							BeveragePendingContext context = new BeveragePendingContext(info);
							setSwayhint(context.getSwayHint());

						}
						temperature=0;
					}
					if (tank.getFluidAmount() % 250 == 0) {// add fluid to existing
						// System.out.print(5);
						BeverageInfo info = resource.get(CVComponents.BEVERAGE_INFO);
						if (info == null) {// not beverage fluid: create a temporary info and compute sway hint
							info = new BeverageInfo();
							FluidStack fs = tank.getFluid();
							int amt = fs.getAmount() / 250;
							for (int i = 0; i < amt; i++) {
								info.relishes[i] = fs.getFluid();
							}
						}

						BeveragePendingContext context = new BeveragePendingContext(info);
						setSwayhint(context.getSwayHint());
						temperature = (temperature * tank.getFluidAmount() - filled) / tank.getFluidAmount();
					}

					resetAdding();
					syncData();

				}
			} else if (tank.isFluidValid(resource) && resource.getAmount() >= 250) {// mix new relish fluid
				if (getSpeed() == 0)// not stiring, can not mix
					return 0;
				// System.out.print(0);
				BeverageInfo info = tank.getFluid().get(CVComponents.BEVERAGE_INFO);
				if (info == null) {// not beverage fluid: create info
					info = new BeverageInfo();
					FluidStack fs = tank.getFluid();
					int amt = fs.getAmount() / 250;
					for (int i = 0; i < amt; i++) {
						info.relishes[i] = fs.getFluid();
					}
				}
				FluidStack stack = tank.getFluid();
				if (stack.getAmount() % 250 == 0) {
					int amt = stack.getAmount() / 250;
					if (info.getRelishCount() != amt)// mix irregularly: reject
						return 0;
					Fluid actual = resource.getFluid();
					int famt = Math.min(5 - amt, resource.getAmount() / 250);
					BeverageInfo other = resource.get(CVComponents.BEVERAGE_INFO);
					if (other != null) {
						if (other.equals(info)) {
							if (action.execute()) {
								info = info.copy();
								info.merge(other, amt, famt);
								Pair<List<CurrentSwayInfo>, Fluid> swi = info.adjustParts(amt, amt + famt);
								setSwayhint(swi.getFirst());
								FluidStack fsn = new FluidStack(swi.getSecond(), tank.getFluidAmount() + famt * 250);
								fsn.applyComponents(tank.getFluid().getComponentsPatch());
								fsn.set(CVComponents.BEVERAGE_INFO, info);
								temperature = (temperature * amt) / (famt + amt);
								tank.setFluid(fsn);
							}
							return famt * 250;
						}
					}
					// System.out.print(3);
					if (action.execute()) {// mix beverage with relish fluid
						// System.out.print(4);
						temperature = (temperature * amt) / (famt + amt);
						if (!resource.has(CVComponents.BEVERAGE_INFO)) {// mix two relish fluid
							info = info.copy();
							// System.out.print(5);
							for (int i = amt; i < amt + famt; i++) {
								info.relishes[i] = actual;
							}
							Pair<List<CurrentSwayInfo>, Fluid> swi = info.handleSway();
							setSwayhint(swi.getFirst());
							info.completeData();
							target = new FluidStack(swi.getSecond(), tank.getFluidAmount() + famt * 250);
							target.set(CVComponents.BEVERAGE_INFO, info);
						} else {// mix relish fluid and beverage fluid
							// System.out.print(6);
							info = info.copy();
							for (int i = amt; i < amt + famt; i++) {
								info.relishes[i] = actual;
							}
							Pair<List<CurrentSwayInfo>, Fluid> swi = info.adjustParts(amt, amt + famt);
							setSwayhint(swi.getFirst());
							info.completeData();
							target = new FluidStack(swi.getSecond(), tank.getFluidAmount() + famt * 250);
							target.set(CVComponents.BEVERAGE_INFO, info);
						}
						resetAdding();
						status = MIXING;
						process = processMax = 400;
						syncData();

					}
					return famt * 250;
				}

			}
			return filled;
		}

		@Override
		public FluidStack drain(FluidStack resource, FluidAction action) {
			if (status > ADDING_INGREDIENT)
				return FluidStack.EMPTY;
			FluidStack drained = tank.drain(resource, action);
			if (!drained.isEmpty() && action.execute()) {
				if (tank.getFluid().isEmpty()) {
					tank.setFluid(FluidStack.EMPTY);
					swayhint.clear();
					temperature = 0;
				}
				resetAdding();
				syncData();

			}
			return drained;
		}

		@Override
		public FluidStack drain(int maxDrain, FluidAction action) {
			if (status > ADDING_INGREDIENT)
				return FluidStack.EMPTY;
			FluidStack drained = tank.drain(maxDrain, action);
			if (!drained.isEmpty() && action.execute()) {
				if (tank.getFluid().isEmpty()) {
					tank.setFluid(FluidStack.EMPTY);
					swayhint.clear();
					temperature = 0;
				}
				resetAdding();
				syncData();
			}
			return drained;
		}

	}

	public List<CurrentSwayInfo> getSwayhint() {
		return swayhint;
	}

	public void setSwayhint(List<CurrentSwayInfo> swayhint) {
		this.swayhint.clear();
		int num1 = 0;
		int num2 = 0;
		for (CurrentSwayInfo hint : swayhint) {
			if (hint.active > 0) {
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
}
