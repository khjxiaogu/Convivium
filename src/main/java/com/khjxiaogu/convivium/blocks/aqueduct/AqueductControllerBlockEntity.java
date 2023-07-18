package com.khjxiaogu.convivium.blocks.aqueduct;

import com.khjxiaogu.convivium.CVBlockEntityTypes;
import com.khjxiaogu.convivium.util.RotationUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

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
	public void readCustomNBT(CompoundTag nbt, boolean isClient) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeCustomNBT(CompoundTag nbt, boolean isClient) {
		// TODO Auto-generated method stub

	}
	int i=0;
	@Override
	public void tick() {
		if(this.level.isClientSide)
			return;
		BlockPos src=this.getBlockPos().above();
		if(this.level.getBlockState(src).is(Blocks.AIR)) {
			nxt=20;
			return;
		}
		if(nxt--==0) {
			nxt=20;
			tonxt=20;
			Direction dir=this.getBlockState().getValue(AqueductControllerBlock.FACING);
			Direction moving;
			if(RotationUtils.isBlackGrid(getBlockPos().relative(dir))) {
				moving=dir.getClockWise();
			}else
				moving=dir.getCounterClockWise();
			move(moving);
		}
		//if()
		// TODO Auto-generated method stub
	}

}
