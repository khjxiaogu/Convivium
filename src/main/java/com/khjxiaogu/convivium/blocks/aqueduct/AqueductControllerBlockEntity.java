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

package com.khjxiaogu.convivium.blocks.aqueduct;

import com.khjxiaogu.convivium.CVBlockEntityTypes;
import com.khjxiaogu.convivium.blocks.kinetics.KineticBasedBlock;
import com.khjxiaogu.convivium.blocks.kinetics.KineticTransferBlockEntity;
import com.khjxiaogu.convivium.client.CVParticles;
import com.khjxiaogu.convivium.util.RotationUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class AqueductControllerBlockEntity extends AqueductBlockEntity {
	
	public AqueductControllerBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
		super(CVBlockEntityTypes.AQUEDUCT_MAIN.get(), pWorldPosition, pBlockState);
		tonxt=20;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void handleMessage(short type, int data) {
		// TODO Auto-generated method stub

	}

	@Override
	public void readCustomNBT(CompoundTag nbt, boolean isClient,HolderLookup.Provider ra) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeCustomNBT(CompoundTag nbt, boolean isClient,HolderLookup.Provider ra) {
		// TODO Auto-generated method stub

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
					this.level.addParticle(CVParticles.SPLASH.get(),center.x()+0.5*dx+rx,center.y()+0.45,center.z()+0.5*dz+rz,-dx*0.05, 0,-dz*0.05);
					//System.out.println("tic"+dx+","+dz);
				}
				
			}
			return;
		}
		
		Direction facing=state.getValue(AqueductControllerBlock.FACING);
		boolean active=state.getValue(KineticBasedBlock.ACTIVE);
		BlockPos facingPos=getBlockPos().relative(facing);
		BlockState bs=this.getLevel().getBlockState(facingPos);
		int spd=0;
		if(bs.hasProperty(KineticBasedBlock.ACTIVE)&&bs.getValue(KineticBasedBlock.ACTIVE)) {
			boolean isChanged=false;
			if(!active) {
				active=true;
				state=state.setValue(KineticBasedBlock.ACTIVE, active);
				isChanged=true;
			}
			boolean hasSignal=level.hasNeighborSignal(this.worldPosition);
			boolean locked=state.getValue(KineticBasedBlock.LOCKED);
			if(locked!=hasSignal) {
				state=state.setValue(KineticBasedBlock.LOCKED, hasSignal);
				isChanged=true;
			}
			if(isChanged)
				this.level.setBlockAndUpdate(this.getBlockPos(),state);
			if(hasSignal)
				active=false;
			if(level.getBlockEntity(facingPos) instanceof KineticTransferBlockEntity ent) {
				spd=ent.getSpeed();
			}
		}else if(active) {
			active=false;
			this.level.setBlockAndUpdate(this.getBlockPos(), state.setValue(KineticBasedBlock.ACTIVE, active));
		}
		if(active) {
			/*if(this.level.getBlockState(src).is(Blocks.AIR)) {
			nxt=20;
			return;
			}*/
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

}
