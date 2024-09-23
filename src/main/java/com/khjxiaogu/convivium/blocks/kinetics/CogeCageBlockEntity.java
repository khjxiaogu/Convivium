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

import com.khjxiaogu.convivium.CVBlockEntityTypes;
import com.khjxiaogu.convivium.CVBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class CogeCageBlockEntity extends KineticTransferBlockEntity implements Cog{

	public CogeCageBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
		super(CVBlockEntityTypes.COG_CAGE.get(), pWorldPosition, pBlockState);

	}

	@Override
	public void handleMessage(short type, int data) {
	}

	@Override
	public void tick() {
		if (this.level.isClientSide)
			return;
		super.tick();

	}
	@Override
	public boolean isReceiver() {
		return false;
	}

	@Override
	public void readCustomNBT(CompoundTag nbt, boolean isClient, Provider registries) {
		
	}

	@Override
	public void writeCustomNBT(CompoundTag nbt, boolean isClient, Provider registries) {
		
	}
	@Override
	public boolean isCogTowards(Direction facing) {
		return this.getBlockState().is(CVBlocks.cog.get());
	}

	@Override
	public boolean isCageTowards(Direction facing) {
		return this.getBlockState().is(CVBlocks.cage.get());
	}
}
