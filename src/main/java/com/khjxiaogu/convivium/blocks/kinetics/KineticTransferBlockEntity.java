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

package com.khjxiaogu.convivium.blocks.kinetics;

import com.teammoeg.caupona.network.CPBaseBlockEntity;
import com.teammoeg.caupona.util.LazyTickWorker;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class KineticTransferBlockEntity extends CPBaseBlockEntity implements KineticConnected {
	protected LazyTickWorker process;
	protected int speed;//
	boolean isSpeedApplied=false;
	public KineticTransferBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
		super(pType, pWorldPosition, pBlockState);
		process = KineticConnected.createKineticValidator(this);
	}

	@Override
	public int getSpeed() {
		return speed;
	};

	@Override
	public void setSpeed(int val) {
		if (isSpeedApplied&&speed > val)
			return;
		isSpeedApplied=true;
		process.rewind();

		if(speed!=val) {
			if(getSpeed()==0) {
				this.level.setBlockAndUpdate(worldPosition,this.getBlockState().setValue(KineticBasedBlock.ACTIVE, true));
			}
			if(val==0) {
				this.level.setBlockAndUpdate(worldPosition,this.getBlockState().setValue(KineticBasedBlock.ACTIVE, false));
			}
			speed = val;
			this.syncData();
		}else this.setChanged();
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

	@Override
	public void tick() {
		if(level.isClientSide)return;
		isSpeedApplied=false;
		if(process.tick()) {
			this.syncData();
		}
	}

}
