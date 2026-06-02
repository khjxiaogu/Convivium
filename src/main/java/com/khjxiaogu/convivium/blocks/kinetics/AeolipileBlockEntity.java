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

package com.khjxiaogu.convivium.blocks.kinetics;

import java.util.HashSet;
import java.util.Set;

import com.khjxiaogu.convivium.CVBlockEntityTypes;
import com.khjxiaogu.convivium.CVConfig;
import com.teammoeg.caupona.CPCapability;
import com.teammoeg.caupona.blocks.CPHorizontalBlock;
import com.teammoeg.caupona.blocks.pot.StewPotBlockEntity;
import com.teammoeg.caupona.blocks.stove.IStove;
import com.teammoeg.caupona.network.CPBaseBlockEntity;
import com.teammoeg.caupona.util.IInfinitable;
import com.teammoeg.caupona.util.LazyTickWorker;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class AeolipileBlockEntity extends CPBaseBlockEntity implements IInfinitable{
	LazyTickWorker process;
	public int speed;
	public int waterTick;
	private int r;
	private boolean inf;
	public AeolipileBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
		super(CVBlockEntityTypes.AOELIPILE.get(), pWorldPosition, pBlockState);
		r = CVConfig.SERVER.kineticRange.get();
		process = new LazyTickWorker(Mth.ceil(CVConfig.SERVER.kineticValidation.get() / 2f),()->{
			Set<BlockPos> pss = getAll();
			for (BlockPos pos : pss) {
				if (level.getBlockEntity(pos) instanceof KineticConnected cog) {
					cog.setSpeed(speed);
					if (level.getBlockEntity(pos.below()) instanceof KineticConnected cog2&&cog2.isReceiver()) {
						cog2.setSpeed(speed);
					}
				}
			}
			return true;
		});
	}

	@Override
	public void handleMessage(short type, int data) {
	}

	@Override
	public void readCustomNBT(ValueInput nbt, boolean isClient) {
		speed = nbt.getIntOr("heatSpeed",0);
		waterTick = nbt.getIntOr("water",0);
		inf=nbt.getBooleanOr("inf",false);
	}

	@Override
	public void writeCustomNBT(ValueOutput nbt, boolean isClient) {
		nbt.putInt("heatSpeed", speed);
		nbt.putInt("water", waterTick);
		nbt.putBoolean("inf", inf);
	}

	private boolean dist(BlockPos crn, BlockPos orig) {
		return Mth.abs(crn.getX() - orig.getX()) <= r && Mth.abs(crn.getZ() - orig.getZ()) <= r && Mth.abs(crn.getY() - orig.getY()) <= r;
	}
	public void findNext(Level l, BlockPos orig,Direction dir,Set<BlockPos> pos) {
		BlockPos crn=orig.relative(dir);
		if(l.isLoaded(crn)&&isCage(crn,dir.getOpposite()))
			findNext(l,crn,crn,true,pos);
	}
	/**
	 * @param dir  
	 */
	public boolean isCage(BlockPos bs,Direction dir) {
		if(level.getBlockEntity(bs) instanceof Cog cog) {
			return cog.isCageTowards(dir);
		}
		return false;
	}
	/**
	 * @param dir  
	 */
	public boolean isCog(BlockPos bs,Direction dir) {
		if(level.getBlockEntity(bs) instanceof Cog cog) {
			return cog.isCogTowards(dir);
		}
		return false;
	}
	public boolean isReciver(BlockPos bs) {
		if(level.getBlockEntity(bs) instanceof KineticConnected transfer) {
			return transfer.isReceiver();
		}
		return false;
	}
	public void findNext(Level l, BlockPos crn, BlockPos orig,boolean isCage,Set<BlockPos> pos) {
		if (dist(crn, orig)) {
			if (pos.add(crn)) {
				for (Direction dir : Utils.horizontals) {
					BlockPos act = crn.relative(dir);
					if (l.isLoaded(act) && 
							((isCage&&isCog(act,dir.getOpposite()))||
							(!isCage&&this.isCage(act,dir.getOpposite())))) {
						findNext(l, act, orig,!isCage,pos);
					}
				}
			}
		}else {
			if (isReciver(crn)) {
				pos.add(crn);
			}
		}
	}
	public BlockPos getFacingPos() {
		return this.getBlockPos().relative(this.getBlockState().getValue(CPHorizontalBlock.FACING).getClockWise());
	}
	@SuppressWarnings("resource")
	public Set<BlockPos> getAll() {
		Set<BlockPos> poss = new HashSet<>();
		findNext(this.getLevel(), this.getBlockPos(),this.getBlockState().getValue(CPHorizontalBlock.FACING).getClockWise(), poss);
		return poss;
	}

	@Override
	public void tick() {
		if (this.level.isClientSide())
			return;
		process.tick();
		if (level.getCapability(CPCapability.HEAT_STOVE, worldPosition.below(2), Direction.UP) instanceof IStove stove&&level.getBlockEntity(worldPosition.below()) instanceof StewPotBlockEntity stew_pot&&stew_pot.canAddFluid()) {
			ResourceHandler<FluidResource> tank=stew_pot.getTank();
			if(tank.getResource(0).is(Fluids.WATER)) {
				try(Transaction trans=Transaction.openRoot()){
					if(waterTick==0) {
						if(inf||tank.extract(tank.getResource(0),1,trans)>0) {
							stew_pot.syncData();
							waterTick=20;
						}
					}
					if(waterTick>0) {
						
						int nh = stove.requestHeat(2,trans);
						if (speed != nh) {
							if(speed==0) {
								this.level.setBlockAndUpdate(worldPosition,this.getBlockState().setValue(KineticBasedBlock.ACTIVE, true));
							}else if(nh==0) {
								process.enqueue();
								this.level.setBlockAndUpdate(worldPosition,this.getBlockState().setValue(KineticBasedBlock.ACTIVE,false));
								this.setChanged();
							}
							process.enqueue();
							speed = nh;
						}
						if(speed>0)
							waterTick--;
						this.setChanged();
						trans.commit();
						return;
					}
				}
			}
		}
		if (speed != 0) {
			process.enqueue();
			this.level.setBlockAndUpdate(worldPosition,this.getBlockState().setValue(KineticBasedBlock.ACTIVE, false));
			speed = 0;
			this.setChanged();
		}
	}

	@Override
	public boolean setInfinity() {
		return inf=!inf;
	}

	@Override
	public boolean isInfinite() {
		return inf;
	}

}
