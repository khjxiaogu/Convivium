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

package com.khjxiaogu.convivium.blocks.aqueduct;

import com.khjxiaogu.convivium.CVBlockEntityTypes;
import com.khjxiaogu.convivium.blocks.kinetics.Cog;
import com.khjxiaogu.convivium.blocks.kinetics.KineticBasedBlock;
import com.khjxiaogu.convivium.blocks.kinetics.KineticConnected;
import com.khjxiaogu.convivium.client.CVParticles;
import com.khjxiaogu.convivium.util.RotationUtils;
import com.teammoeg.caupona.util.LazyTickWorker;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class AqueductControllerBlockEntity extends AqueductBlockEntity implements KineticConnected,Cog{
	protected LazyTickWorker process;
	protected int speed;//
	public AqueductControllerBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
		super(CVBlockEntityTypes.AQUEDUCT_MAIN.get(), pWorldPosition, pBlockState);
		tonxt=20;
		process = KineticConnected.createKineticValidator(this);
	}
	@Override
	public int getSpeed() {
		return speed;
	};

	@Override
	public void setSpeed(int val) {
		if (speed > val)
			return;
		process.rewind();
		if(speed!=val) {
			if(getSpeed()==0) {
				this.level.setBlockAndUpdate(worldPosition,this.getBlockState().setValue(KineticBasedBlock.ACTIVE, true));
			}
			speed = val;
			this.syncData();
		}else this.setChanged();
	}

	@Override
	public void handleMessage(short type, int data) {

	}

	@Override
	public void readCustomNBT(CompoundTag nbt, boolean isClient,HolderLookup.Provider ra) {
		speed = nbt.getInt("speed");
		process.read(nbt,"kttic");
	}

	@Override
	public void writeCustomNBT(CompoundTag nbt, boolean isClient,HolderLookup.Provider ra) {
		nbt.putInt("speed", speed);
		process.write(nbt,"kttic");
	}

	
	int i=0;
	@Override
	public void tick() {
		BlockState state=this.getBlockState();
		if(this.level.isClientSide) {
			if(state.getValue(KineticBasedBlock.ACTIVE)&&!state.getValue(KineticBasedBlock.LOCKED)) {
				Direction dir=this.getBlockState().getValue(AqueductControllerBlock.FACING);
				Direction moving;
				if(RotationUtils.isBlackGrid(getBlockPos())) {
					moving=dir.getClockWise();
				}else
					moving=dir.getCounterClockWise();
				Vec3 center=this.getBlockPos().getCenter();
				if(Math.random()<0.5d) {
					
					int dx=-moving.getStepX();
					int dz=-moving.getStepZ();
					double rx=moving.getClockWise().getStepX()*(Math.random()-0.5)*6/8f;
					double rz=moving.getClockWise().getStepZ()*(Math.random()-0.5)*6/8f;
					this.level.addParticle(CVParticles.FLOW.get(),center.x()+0.5*dx+rx,center.y()+0.45,center.z()+0.5*dz+rz,-dx*0.05, 0,-dz*0.05);
					//System.out.println("tic"+dx+","+dz);
				}
				
			}
			return;
		}
		if(process.tick()) {
			this.syncData();
		}
		boolean isChanged=false;
		boolean hasSignal=level.hasNeighborSignal(this.worldPosition);
		boolean locked=state.getValue(KineticBasedBlock.LOCKED);
		if(locked!=hasSignal) {
			state=state.setValue(KineticBasedBlock.LOCKED, hasSignal);
			isChanged=true;
		}
		if(isChanged) {
			this.level.setBlockAndUpdate(this.getBlockPos(),state);
			this.setChanged();
		}
		boolean active=state.getValue(KineticBasedBlock.ACTIVE);
		int spd=getSpeed();
		if(active) {
			Direction dir=this.getBlockState().getValue(AqueductControllerBlock.FACING);
			Direction moving;
			if(RotationUtils.isBlackGrid(getBlockPos())) {
				moving=dir.getClockWise();
			}else
				moving=dir.getCounterClockWise();
			if(nxt--==0) {
				tonxt=nxt=40/Math.max(1, spd);
				move(moving);
			}
		}
		

	}
	@Override
	public boolean isReceiver() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean isCogTowards(Direction facing) {
		return facing==this.getBlockState().getValue(AqueductControllerBlock.FACING);
	}
	@Override
	public boolean isCageTowards(Direction facing) {
		return false;
	}

}
