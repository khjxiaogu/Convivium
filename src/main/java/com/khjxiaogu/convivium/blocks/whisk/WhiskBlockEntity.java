package com.khjxiaogu.convivium.blocks.whisk;

import com.khjxiaogu.convivium.CVBlockEntityTypes;
import com.khjxiaogu.convivium.blocks.kinetics.KineticTransferBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class WhiskBlockEntity extends KineticTransferBlockEntity {

	public WhiskBlockEntity( BlockPos pWorldPosition, BlockState pBlockState) {
		super(CVBlockEntityTypes.WHISK.get(), pWorldPosition, pBlockState);

	}

	@Override
	public void handleMessage(short type, int data) {

	}

	@Override
	public boolean isReceiver() {
		// TODO Auto-generated method stub
		return true;
	}

}
