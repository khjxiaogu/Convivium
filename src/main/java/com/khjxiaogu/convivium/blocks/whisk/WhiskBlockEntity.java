package com.khjxiaogu.convivium.blocks.whisk;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

import com.khjxiaogu.convivium.CVBlockEntityTypes;
import com.khjxiaogu.convivium.CVTags;
import com.khjxiaogu.convivium.blocks.kinetics.KineticTransferBlockEntity;
import com.khjxiaogu.convivium.data.recipes.ContainingRecipe;
import com.khjxiaogu.convivium.data.recipes.ConvertionRecipe;
import com.khjxiaogu.convivium.data.recipes.RelishFluidRecipe;
import com.khjxiaogu.convivium.data.recipes.TasteRecipe;
import com.khjxiaogu.convivium.fluid.BeverageFluid;
import com.khjxiaogu.convivium.util.BeverageInfo;
import com.khjxiaogu.convivium.util.BeveragePendingContext;
import com.khjxiaogu.convivium.util.CurrentSwayInfo;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.teammoeg.caupona.data.SerializeUtil;
import com.teammoeg.caupona.util.FloatemStack;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;

public class WhiskBlockEntity extends KineticTransferBlockEntity {
	ItemStackHandler inv=new ItemStackHandler(6) {
	    @Override
	    public boolean isItemValid(int slot, @NotNull ItemStack stack)
	    {
	    	if(slot<4)
	    		return isValidInput(stack);
	    	if(slot==4)
	    		return stack.getItem()==Items.GLASS_BOTTLE||ContainingRecipe.getFluidType(stack)!=Fluids.EMPTY;
	    	return false;
	    }
	};
	FluidTank tank=new FluidTank(1250,e->RelishFluidRecipe.recipes.containsKey(e.getFluid()));
	List<CurrentSwayInfo> swayhint;
	static Codec<List<CurrentSwayInfo>> CSI_CODEC=Codec.list(CurrentSwayInfo.CODEC);
	BeverageInfo info;
	int process;
	int processMax;
	boolean isStiring;
	int heating;
	boolean isHeating;
	boolean rs;
	public WhiskBlockEntity( BlockPos pWorldPosition, BlockState pBlockState) {
		super(CVBlockEntityTypes.WHISK.get(), pWorldPosition, pBlockState);

	}
	@Override
	public void readCustomNBT(CompoundTag nbt, boolean isClient) {
		super.readCustomNBT(nbt, isClient);
		if(nbt.contains("info"))
			info=new BeverageInfo(nbt.getCompound("info"));
		else
			info=null;
		swayhint=CSI_CODEC.decode(NbtOps.INSTANCE,nbt.get("hint")).result().map(Pair::getFirst).orElse(null);
		process=nbt.getInt("process");
		processMax=nbt.getInt("processMax");
		isStiring=nbt.getBoolean("isStir");
		heating=nbt.getInt("heat");
		isHeating=nbt.getBoolean("heating");
		rs=nbt.getBoolean("rs");
		
	}
	@Override
	public void writeCustomNBT(CompoundTag nbt, boolean isClient) {
		super.writeCustomNBT(nbt, isClient);
		if(info!=null)
			nbt.put("info", info.save());
		if(swayhint!=null)
			nbt.put("hint", CSI_CODEC.encodeStart(NbtOps.INSTANCE,swayhint).result().orElse(null));
		nbt.putInt("process", process);
		nbt.putInt("processMax", processMax);
		nbt.putBoolean("isStir", isStiring);
		nbt.putInt("heat", heating);
		nbt.putBoolean("heating", isHeating);
		nbt.putBoolean("rs", rs);
	}
	@Override
	public void handleMessage(short type, int data) {
		if(type==0) {
			rs=data==0;
		}else if(type==1) {
			isHeating=data==0;
		}
	}

	@Override
	public boolean isReceiver() {
		// TODO Auto-generated method stub
		return true;
	}
	public boolean isValidInput(ItemStack is) {
		return is.is(CVTags.Items.drinkMaterial)||TasteRecipe.recipes.stream().anyMatch(t->t.item.test(is));
	}
	@Override
	public void tick() {
		super.tick();
		if(level.isClientSide)return;
		if(rs) {
			isHeating=level.hasNeighborSignal(this.worldPosition);
		}
		if(processMax!=0) {
			process-=speed;
			if(isStiring) {
				boolean flag=false;
				for (int i = 0; i < 4; i++) {
					ItemStack is = inv.getStackInSlot(i);
					if (!is.isEmpty()) {
						if (is.getItem() != Items.POTION) {
							flag|=true;
						}
					}
				}
				if(!flag) {
					isStiring=false;
					process=processMax=0;
				}else
				if(process<=200) {
					isStiring=false;
					if(info==null)
						info=new BeverageInfo();
					int amt=tank.getFluidAmount()/250;
					NonNullList<ItemStack> interninv = NonNullList.withSize(4, ItemStack.EMPTY);
					for (int i = 0; i < 4; i++) {
						ItemStack is = inv.getStackInSlot(i);
						if (!is.isEmpty()) {
							if (is.getItem() == Items.POTION) {
								for (MobEffectInstance eff : PotionUtils.getMobEffects(is))
									info.addEffect(eff,amt);
								inv.setStackInSlot(i, new ItemStack(Items.GLASS_BOTTLE));
							} else {
								for (int j = 0; j < 4; j++) {
									ItemStack ois = interninv.get(j);
									if (ois.isEmpty()) {
										interninv.set(j, is.copy());
										break;
									} else if (ItemStack.isSameItemSameTags(ois, is)) {
										ois.setCount(ois.getCount() + is.getCount());
										break;
									}
								}
								inv.setStackInSlot(i, is.getCraftingRemainingItem());
							}
						}
					}
					for (int i = 0; i < 4; i++) {
						ItemStack is = interninv.get(i);
						if (is.isEmpty())
							break;
						info.addItem(is, amt);
					}
					setSwayhint(info.handleSway());
					
				}
			}
			if(process<=0) {
				process=processMax=0;
				BeverageFluid.setInfo(tank.getFluid(), info);
			}
		}else { 
			if(tank.getFluidAmount()%250==0){
				int amt=tank.getFluidAmount()/250;
				float cnt=0;
				boolean hasItem=false;
				if(info!=null) {
					cnt=info.getDensity()*amt;
					for (int i = 0; i < 4; i++) {
						ItemStack is = inv.getStackInSlot(i);
						if (!is.isEmpty()) {
							if (is.getItem() != Items.POTION) {
								cnt++;
							}
							hasItem=true;
						}
					}
				}else {
					for (int i = 0; i < 4; i++) {
						ItemStack is = inv.getStackInSlot(i);
						if (!is.isEmpty()) {
							if (is.getItem() != Items.POTION) {
								cnt++;
							}
							hasItem=true;
						}
					}
				}
				if(hasItem&&cnt/amt<=2) {
					isStiring=true;
					process=processMax=400;
				}else if(info!=null){
					ConvertionRecipe rc=null;
					outer:for(ConvertionRecipe r:ConvertionRecipe.recipes){
						if(r.temperature<=info.heat)
							continue;
						if(r.in!=Fluids.EMPTY) {
							boolean flag=false;
							int cntx=0;
							for(Fluid f:info.relishes) {
								if(f==r.in) {
									flag=true;
									cntx++;
								}
							}
							if(!flag) continue outer;
							if(cntx<r.inpart)continue outer;
						}
						int newpart=info.getRelishCount()+r.outpart-r.inpart;
						if(newpart>5||newpart<=0) {
							continue;
						}
						float tccn=info.getDensity();
						if(!r.items.isEmpty()) {
							
							for(Pair<Ingredient, Float> i:r.items) {
								boolean flag=true;
								for(FloatemStack fs:info.stacks) {
									float ccn=i.getSecond()/cnt;
									tccn-=ccn;
									if(!i.getFirst().test(fs.getStack())||ccn>fs.getCount()) {
										flag=false;
										break;
									}
								}
								if(!flag)
									continue outer;
							}
						}
						if(!r.output.isEmpty()) {
							for(Pair<ItemStack, Float> i:r.output) {
								tccn+=i.getSecond()/cnt;
							}
						}
						if(tccn*cnt/newpart>2)continue;
						rc=r;
						break;
					}
					if(rc!=null) {
						List<Fluid> fs=new ArrayList<>();
						int nin=rc.inpart;
						if(rc.in!=Fluids.EMPTY) {
							for(int i=0;i<5;i++) {
								Fluid f=info.relishes[i];
								info.relishes[i]=null;
								if(f==null)continue;
								if(nin!=0&&f==rc.in) {
									nin--;
								}else
									fs.add(f);
							}
							if(rc.out!=Fluids.EMPTY)
								for(int i=0;i<rc.outpart;i++)
									fs.add(rc.out);
							for(int i=0;i<fs.size();i++) {
								info.relishes[i]=fs.get(i);
							}
						}else {
							int cntx=info.getRelishCount();
							for(int i=cntx;i<cntx+rc.outpart;i++) {
								info.relishes[i]=rc.out;
							}
						}
						
						if(!rc.items.isEmpty()) {
							for(Pair<Ingredient, Float> i:rc.items) {
								for(FloatemStack fss:info.stacks) {
									if(i.getFirst().test(fss.getStack())) {
										fss.setCount(fss.getCount()-i.getSecond()/cnt);
										break;
									}
								}
							}
						}
						if(!rc.output.isEmpty()) {
							for(Pair<ItemStack, Float> i:rc.output) {
								info.addItem(i.getFirst(), cnt/i.getSecond());
							}
						}
						int cn=info.getRelishCount()*250;
						setSwayhint(info.adjustParts(tank.getFluidAmount(),cn));
						tank.getFluid().setAmount(cn);
						process=processMax=rc.processTime;
						
					}
				}
			}
			
		}
		if(tank.getFluidAmount()%250==0&&!tank.isEmpty()) {
			heating++;
			if(isHeating) {
				if(heating>=2*tank.getFluidAmount()/250) {
					if(info==null) {
						info=new BeverageInfo();
						FluidStack fs=tank.getFluid();
						for(int i=0;i<fs.getAmount()/250;i++) {
							info.relishes[i]=fs.getFluid();
						}
					}
					if(info.heat<70)
						info.heat++;
					heating=0;
				}
			}else if(info!=null){
				if(heating>=4*tank.getFluidAmount()/250) {
					if(info.heat>0)
						info.heat--;
					else
						info.heat++;
					heating=0;
				}
			}
		}
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
			if(processMax!=0)return 0;
			boolean isEmpty=tank.getFluid().isEmpty();
			int filled = tank.fill(resource, action);
			if (filled != 0) {
				if(action.execute()) {
					if(isEmpty) {
						if(resource.getFluid() instanceof BeverageFluid) {
							info=BeverageFluid.getInfo(resource).orElse(null);
							if(info!=null) {
								BeveragePendingContext context=new BeveragePendingContext(info);
								setSwayhint(context.getSwayHint());
							}
						}
					}
					syncData();
				}
			}else if(tank.isFluidValid(resource)&&resource.getAmount()>250){
				FluidStack stack=tank.getFluid();
				if(stack.getAmount()%250==0) {
					int amt=stack.getAmount()/250;
					if(info!=null&&info.getRelishCount()!=amt)return 0;
					Fluid actual=resource.getFluid();
					int famt=Math.min(5-amt,resource.getAmount()/250);
					if(actual instanceof BeverageFluid) {
						BeverageInfo other=BeverageFluid.getInfo(resource).orElse(null);
						if(other!=null) {
							if(other.equals(info)) {
								if(action.execute()) {
									info.merge(other,amt, famt);
									setSwayhint(info.adjustParts(amt,amt+famt));
									
									tank.getFluid().setAmount(tank.getFluidAmount()+famt*250);
									BeverageFluid.setInfo(tank.getFluid(), info);
								}
								return famt*250;
							}
						}
					}
					
					if(action.execute()) {
						if(info==null) {
							info=new BeverageInfo();
							for(int i=0;i<amt;i++) {
								info.relishes[i]=stack.getFluid();
							}
							for(int i=amt;i<amt+famt;i++) {
								info.relishes[i]=actual;
							}
						}else {
							for(int i=amt;i<amt+famt;i++) {
								info.relishes[i]=actual;
							}
							setSwayhint(info.adjustParts(amt,amt+famt));
							
							BeverageFluid.setInfo(tank.getFluid(), info);
						}
						process=processMax=400;
						syncData();
					}
					return famt*250;
				}
				
			}
			return filled;
		}

		@Override
		public FluidStack drain(FluidStack resource, FluidAction action) {
			if(processMax!=0)return FluidStack.EMPTY;
			FluidStack drained = tank.drain(resource, action);
			if (!drained.isEmpty() && action.execute()) {
				if(tank.getFluid().isEmpty()) {
					info=null;
				}
				syncData();
			}
			return drained;
		}

		@Override
		public FluidStack drain(int maxDrain, FluidAction action) {
			if(processMax!=0)return FluidStack.EMPTY;
			FluidStack drained = tank.drain(maxDrain, action);
			if (!drained.isEmpty() && action.execute()) {
				if(tank.getFluid().isEmpty()) {
					info=null;
				}
				syncData();
			}
			return drained;
		}

	}
	public List<CurrentSwayInfo> getSwayhint() {
		return swayhint;
	}
	public void setSwayhint(List<CurrentSwayInfo> swayhint) {
		this.swayhint = swayhint.subList(0,Math.min(3, swayhint.size()));
	}


}
