/*
 * Copyright (c) 2022 TeamMoeg
 *
 * This file is part of Caupona.
 *
 * Caupona is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Caupona is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * Specially, we allow this software to be used alongside with closed source software Minecraft(R) and Forge or other modloader.
 * Any mods or plugins can also use apis provided by forge or com.teammoeg.caupona.api without using GPL or open source.
 *
 * You should have received a copy of the GNU General Public License
 * along with Caupona. If not, see <https://www.gnu.org/licenses/>.
 */

package com.khjxiaogu.convivium.blocks.kinetics;

import com.khjxiaogu.convivium.CVConfig;
import com.teammoeg.caupona.network.CPBaseBlockEntity;
import com.teammoeg.caupona.util.LazyTickWorker;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class KineticTransferBlockEntity extends CPBaseBlockEntity {
	protected LazyTickWorker process;
	protected int speed;//
	public KineticTransferBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
		super(pType, pWorldPosition, pBlockState);
		process = new LazyTickWorker(CVConfig.SERVER.kineticValidation.get(),()->{
			if(this.speed!=0) {
				this.speed = 0;
				if(getSpeed()==0)
					this.level.setBlockAndUpdate(worldPosition,this.getBlockState().setValue(KineticBasedBlock.ACTIVE, false));
				return true;
			}
			return false;
		});
	}

	public int getSpeed() {
		return speed;
	};

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
	public void readCustomNBT(CompoundTag nbt, boolean isClient) {
		speed = nbt.getInt("speed");
		process.read(nbt,"kttic");
	}

	@Override
	public void writeCustomNBT(CompoundTag nbt, boolean isClient) {
		nbt.putInt("speed", speed);
		process.write(nbt,"kttic");
	}

	@Override
	public void tick() {
		if(level.isClientSide)return;
		if(process.tick()) {
			this.syncData();
		}
	}
	public abstract boolean isReceiver() ;
}
