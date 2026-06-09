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

package com.khjxiaogu.convivium.blocks.wolf_fountain;

import java.util.Optional;
import java.util.stream.StreamSupport;

import org.joml.Vector2i;
import org.jspecify.annotations.Nullable;
import org.spongepowered.include.com.google.common.base.Objects;

import com.khjxiaogu.convivium.CVBlockEntityTypes;
import com.khjxiaogu.convivium.CVEntityTypes;
import com.khjxiaogu.convivium.CVItems;
import com.khjxiaogu.convivium.CVMain;
import com.khjxiaogu.convivium.blocks.kinetics.Cog;
import com.khjxiaogu.convivium.blocks.kinetics.KineticTransferBlockEntity;
import com.khjxiaogu.convivium.client.CVParticles;
import com.khjxiaogu.convivium.util.FoodPropertieHelper;
import com.teammoeg.caupona.api.CauponaHooks;
import com.teammoeg.caupona.api.events.ContanerContainFoodEvent;
import com.teammoeg.caupona.blocks.foods.IFoodContainer;
import com.teammoeg.caupona.util.ChancedEffect;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.FoodProperties.Builder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;


public class WolfFountainBlockEntity extends KineticTransferBlockEntity implements Cog{
	public FluidStacksResourceHandler fluid=new FluidStacksResourceHandler(1,1000) {
		@Override
		protected void onContentsChanged(int index, FluidStack previousContents) {
			// TODO Auto-generated method stub
			super.onContentsChanged(index, previousContents);
			syncData();
		}
	};
	public ItemStack item;
	FoodProperties appliedFood;
	Consumable appliedConsumable;
	int currentVersion;
	public static final Vector2i[] spd1pos=new Vector2i[] {
		new Vector2i(1,0),
		new Vector2i(1,-1),
		new Vector2i(2,-1),
		new Vector2i(1,-2),
		new Vector2i(2,-2),
		new Vector2i(2,-3),
		new Vector2i(2,-4),
		new Vector2i(2,-5),
		new Vector2i(2,-6),
		new Vector2i(2,-7),
		
	};
	public static final Vector2i[] spd2pos=new Vector2i[] {
		new Vector2i(1,0),
		new Vector2i(2,0),
		new Vector2i(2,-1),
		new Vector2i(2,-2),
		new Vector2i(3,-2),
		new Vector2i(3,-3),
		new Vector2i(3,-4),
		new Vector2i(3,-5),
		new Vector2i(4,-5),
		new Vector2i(4,-6),
		new Vector2i(4,-7),
	};
	int workProcess;
	int throwProcess;
	BlockPos lasthit;
	public WolfFountainBlockEntity( BlockPos pWorldPosition, BlockState pBlockState) {
		super(CVBlockEntityTypes.WOLF_FOUNTAIN.get(), pWorldPosition, pBlockState);
	}

	@Override
	public void handleMessage(short type, int data) {
		
	}


	public void resetContent() {
		workProcess=0;
		lasthit=null;
		appliedFood=null;
		appliedConsumable=null;
		fluid.set(0, FluidResource.EMPTY, 0);
		item=null;
		if(!this.level.isClientSide()) {
			do {
				int nxtrnd=this.level.getRandom().nextInt();
				if(nxtrnd!=currentVersion) {
					currentVersion=nxtrnd;
					break;
				}
			}while(true);
			this.syncData();
		}
		
	}
	@Override
	public void readCustomNBT(ValueInput tag, boolean arg1) {
		super.readCustomNBT(tag, arg1);
		tag.readChild("fluid", fluid);
		item=tag.read("item", ItemStack.CODEC).orElse(null);
		if(!arg1) {
			appliedFood=tag.read("food",FoodProperties.DIRECT_CODEC).orElse(null);
			appliedConsumable=tag.read("consumable",Consumable.CODEC).orElse(null);
			workProcess=tag.getIntOr("process",0);
			lasthit=tag.read("lasthit", BlockPos.CODEC).orElse(null);
			currentVersion=tag.getIntOr("version",0);
			throwProcess= tag.getIntOr("emitProcess",0);
		}
	}

	@Override
	public void writeCustomNBT(ValueOutput tag, boolean arg1) {
		super.writeCustomNBT(tag, arg1);
		tag.putChild("fluid", fluid);
		if(item!=null)
			tag.store("item",ItemStack.CODEC,item);
		if(!arg1) {
			if(appliedFood!=null)
				tag.store("food",FoodProperties.DIRECT_CODEC, appliedFood);
			if(appliedConsumable!=null)
				tag.store("consumable",Consumable.CODEC, appliedConsumable);
			tag.putInt("process", workProcess);
			if(lasthit!=null)
				tag.store("lasthit", BlockPos.CODEC, lasthit);
			tag.putInt("version", currentVersion);
			tag.putInt("emitProcess", throwProcess);
		}
	}
	public void applyEffectTo(int currentVersion,BlockPos pos) {
		if(this.level.isClientSide())return;
		if(currentVersion==this.currentVersion) {
			if(item!=null&&appliedConsumable!=null) {//potion portion applied
				return;
			}
			if(Objects.equal(pos, lasthit)) {
				BlockEntity be=this.level.getBlockEntity(pos);
				try(Transaction trans=Transaction.openRoot()){
					if(be instanceof IFoodContainer cont&&(item!=null||fluid.getAmountAsInt(0)>=250)) {//transfer target
						workProcess++;
						if(workProcess>=5) {
							workProcess=0;
							FluidResource fs=fluid.getResource(0);
							for(int i=0;i<cont.getSlots();i++) {
								ItemStack its=cont.exchangeInternal(null, trans);
	
								if(!its.isEmpty()&&Utils.isExtractAllowed(its)) {
									if(item!=null) {
										if(Utils.isExchangeAllowed(its, item)&&cont.accepts(i, item)) {
											cont.setInternal(i, item);
											resetContent();
										}
										break;
									}else if(!fs.isEmpty()){
										ContanerContainFoodEvent ev=Utils.contain(its, fs,true);
										if(ev.isAllowed()) {
											if(cont.accepts(i, ev.out)) {
												fs=fluid.drain(ev.drainAmount, FluidAction.EXECUTE);
												if(fs.getAmount()==ev.drainAmount) {
													ev=Utils.contain(its, fs,false);
													cont.setInternal(i,ev.out);
													break;
												}
											}
											
										}
										if(fluid.isEmpty())
											this.resetContent();
									}
								}
								
							}
							
						}
					}else if(!fluid.isEmpty()){
						IFluidHandler ifh=FluidHandler.BLOCK.getCapability(level, pos,null, be, dir);
						if(ifh!=null) {
							FluidStack out=fluid.drain(50, FluidAction.SIMULATE);
							if(ifh.fill(out, FluidAction.SIMULATE)==out.getAmount()) {
								FluidStack drained=fluid.drain(50, FluidAction.EXECUTE);
								ifh.fill(drained, FluidAction.EXECUTE);
							}
						}
						if(fluid.isEmpty())
							this.resetContent();
					}
					
				}
				
			}else {
				lasthit=pos;
			}
		}
	}
	static final Consumable EMPTY=Consumable.builder().build();
	public void applyEffectTo(int currentVersion,LivingEntity entity) {
		if(this.level.isClientSide())return;
		if(currentVersion==this.currentVersion) {
			if(appliedConsumable==null) {
				if(item!=null) {
					PotionContents potc=item.get(DataComponents.POTION_CONTENTS);
					if(potc!=null) {
						Consumable.Builder fp = Consumable.builder();
						StreamSupport.stream(potc.getAllEffects().spliterator(), false).map(t->ChancedEffect.createByParts(t,5)).forEach(t->t.toPossibleEffects(fp));
						appliedConsumable=fp.build();
					}
				}else if(fluid.getAmountAsInt(0)>0) {
					Consumable cons=fluid.getResource(0).get(DataComponents.CONSUMABLE);
					FoodProperties food=fluid.getResource(0).get(DataComponents.FOOD);
					appliedFood=FoodPropertieHelper.copyWithPart(food, 5);
					appliedConsumable=FoodPropertieHelper.copyWithPart(cons, 5);
					if(appliedConsumable==null)
						appliedConsumable=EMPTY;
				}
			}
			if(appliedConsumable!=null) {
				ItemStack fake=new ItemStack(CVItems.POTION.get());
				if(item!=null) { 
					workProcess++;
					appliedConsumable.onConsume(level, entity, fake);
					if(appliedFood!=null)
						appliedFood.onConsume(level, entity, fake, appliedConsumable);
					if(workProcess==5) {
						resetContent();
					}
				} else if(fluid.getAmountAsInt(0)>0){
					try(Transaction trans=Transaction.openRoot()){
						FluidResource fr=fluid.getResource(0);
						int drained=fluid.extract(fr, 50, trans);
						if(drained>=50) {
							appliedConsumable.onConsume(level, entity, fake);
							if(appliedFood!=null)
								appliedFood.onConsume(level, entity, fake, appliedConsumable);
							if(fr.getFluidType().getTemperature()>270) {
						        if (!entity.fireImmune()) {
						            entity.setRemainingFireTicks(entity.getRemainingFireTicks() + 1);
						            if (entity.getRemainingFireTicks() == 0) {
						                entity.igniteForSeconds(8.0F);
						            }
						        }
								if(fr.getFluidType().getTemperature()>1000)
									entity.lavaHurt();
							}
						}
					}
				}
			}
		}
	}
	@Override
	public void tick() {
		super.tick();
		if(this.level.isClientSide()) {
			if(getSpeed()>0) {
				if(fluid.getAmountAsInt(0)>0) {
				//if(this.level.getGameTime()%20==0) {
					Vec3i vec=this.getBlockState().getValue(WolfFountainBlock.FACING).getUnitVec3i();
					Vec3 center=this.getBlockPos().getCenter().add(vec.getX()*0.75,0.1815,vec.getZ()*0.75);
					
					this.level.addParticle(CVParticles.SPLASH.get().with(fluid.getResource(0).toStack(250)),center.x,center.y,center.z,vec.getX()*0.1*getSpeed(), 0,vec.getZ()*0.1*getSpeed());
				}else if(item!=null) {
					Vec3i vec=this.getBlockState().getValue(WolfFountainBlock.FACING).getUnitVec3i();
					
					Vec3 center=this.getBlockPos().getCenter().add(vec.getX()*0.75,0.1815,vec.getZ()*0.75);
					this.level.addParticle(CVParticles.SPLASH.get().with(item),center.x,center.y,center.z,vec.getX()*0.1*getSpeed(), 0,vec.getZ()*0.1*getSpeed());
				}
			}
			//}
			return;
		}
		int speed=getSpeed();
		if(speed>0) {
			Direction face=this.getBlockState().getValue(WolfFountainBlock.FACING);
			if(fluid.isEmpty()&&item==null) {
				
				Direction backFace=face.getOpposite();
				BlockPos back=this.getBlockPos().relative(backFace);
				Block backBlock=this.getLevel().getBlockState(back).getBlock();

				Optional<IFluidHandler> blockSource = FluidUtil.getFluidHandler(this.getLevel(), back,face);
				if (blockSource.isPresent()) {
					FluidUtil.tryFluidTransfer(fluid, blockSource.orElse(null), 250, true);
					if(!fluid.isEmpty())
						this.syncData();
				} else if (backBlock instanceof BucketPickup bpu) {
					FluidUtil.tryFluidTransfer(fluid,
							new BucketPickupHandlerWrapper(null,bpu,this.getLevel(),back), FluidType.BUCKET_VOLUME,
							true);
					if(!fluid.isEmpty())
						this.syncData();
				}else if(this.getLevel().getBlockEntity(back) instanceof IFoodContainer cont) {
					for(int i=0;i<cont.getSlots();i++) {
						ItemStack its=cont.getInternal(i);
						IFluidHandlerItem ifhi=FluidHandler.ITEM.getCapability(its, null);
						if(ifhi!=null) {
							FluidStack fs=ifhi.drain(1000, FluidAction.SIMULATE);
							if(!fs.isEmpty()) {
								if(fluid.fill(fs, FluidAction.SIMULATE)==fs.getAmount()) {
									fs=ifhi.drain(1000, FluidAction.EXECUTE);
									fluid.fill(fs, FluidAction.EXECUTE);
									cont.setInternal(i,ifhi.getContainer());
									this.syncData();
									break;
								}
							}

						}else if(its.is(Items.POTION)) {
							item=its;
							cont.setInternal(i,new ItemStack(Items.GLASS_BOTTLE));
							this.syncData();
							break;
						}

					}
					
				}
			}
			if(!fluid.isEmpty()||item!=null) {
				if(++throwProcess>=10) {
					throwProcess=0;
					WolfFountainProjectile wfp=CVEntityTypes.WOLF_FOUNTAIN_DROP.get().create(this.level);
					wfp.source=this.getBlockPos();
					wfp.verid=this.currentVersion;
					Vec3i vec=this.getBlockState().getValue(WolfFountainBlock.FACING).getNormal();
					Vec3 center=this.getBlockPos().getCenter().add(vec.getX()*0.75,0.1815,vec.getZ()*0.75);
					wfp.setPos(center);
					wfp.setDeltaMovement(vec.getX()*0.1*getSpeed(), 0,vec.getZ()*0.1*getSpeed());
					this.level.addFreshEntity(wfp);
				}
			}
		}
	}

	@Override
	public boolean isReceiver() {
		return true;
	}

	@Override
	public boolean isCogTowards(Direction facing) {
		return false;
	}

	@Override
	public boolean isCageTowards(Direction facing) {
		return facing.getAxis()==this.getBlockState().getValue(WolfFountainBlock.FACING).getClockWise().getAxis();
	}

}
