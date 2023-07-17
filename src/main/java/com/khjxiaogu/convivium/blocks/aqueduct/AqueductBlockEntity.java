package com.khjxiaogu.convivium.blocks.aqueduct;

import com.khjxiaogu.convivium.CVBlockEntityTypes;
import com.teammoeg.caupona.network.CPBaseBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class AqueductBlockEntity extends CPBaseBlockEntity {

	public AqueductBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
		super(CVBlockEntityTypes.AQUEDUCT.get(), pWorldPosition, pBlockState);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void handleMessage(short type, int data) {
		// TODO Auto-generated method stub

	}

	@Override
	public void readCustomNBT(CompoundTag nbt, boolean isClient) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeCustomNBT(CompoundTag nbt, boolean isClient) {
		// TODO Auto-generated method stub

	}

	@Override
	public void tick() {
		// TODO Auto-generated method stub

	}

}
