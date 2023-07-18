package com.khjxiaogu.convivium.blocks.aqueduct;

import com.khjxiaogu.convivium.CVBlockEntityTypes;
import com.khjxiaogu.convivium.blocks.kinetics.KineticBasedBlock;
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
		BlockState state=this.getBlockState();
		Direction facing=state.getValue(AqueductControllerBlock.FACING);
		boolean active=state.getValue(KineticBasedBlock.ACTIVE);
		BlockPos facingPos=getBlockPos().relative(facing);
		BlockState bs=this.getLevel().getBlockState(facingPos);
		if(bs.hasProperty(KineticBasedBlock.ACTIVE)&&bs.getValue(KineticBasedBlock.ACTIVE)&&!level.hasNeighborSignal(this.worldPosition)) {
			if(!active) {
				active=true;
				this.level.setBlockAndUpdate(this.getBlockPos(), state.setValue(KineticBasedBlock.ACTIVE, active));
			}
		}else if(active) {
			active=false;
			this.level.setBlockAndUpdate(this.getBlockPos(), state.setValue(KineticBasedBlock.ACTIVE, active));
		}
		BlockPos src=this.getBlockPos().above();
		if(active) {
			/*if(this.level.getBlockState(src).is(Blocks.AIR)) {
				nxt=20;
				return;
			}*/
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
		}

	}

}
