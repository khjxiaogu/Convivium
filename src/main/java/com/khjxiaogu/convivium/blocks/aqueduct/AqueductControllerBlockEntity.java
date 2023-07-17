package com.khjxiaogu.convivium.blocks.aqueduct;

import com.khjxiaogu.convivium.CVBlockEntityTypes;
import com.teammoeg.caupona.network.CPBaseBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class AqueductControllerBlockEntity extends CPBaseBlockEntity {
	
	public AqueductControllerBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
		super(CVBlockEntityTypes.AQUEDUCT_MAIN.get(), pWorldPosition, pBlockState);
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
		// TODO Auto-generated method stub
		i++;
		if(i>=20) {
			i=0;
			Direction[] dirs=this.getBlockState().getValue(AqueductBlock.CONN).getNext(Direction.NORTH);
			if(dirs.length>0) {
				move(this.getBlockPos().above(),this.getBlockPos().relative(dirs[this.level.random.nextInt(dirs.length)]).above());
			}
		}
	}
	public void move(BlockPos pos1,BlockPos pos2) {
		BlockEntity be=this.level.getBlockEntity(pos1);
		BlockState bs=this.level.getBlockState(pos1);
		if(bs.is(Blocks.AIR))return;
		if(be!=null) {
			CompoundTag nbt=be.serializeNBT();
			this.level.removeBlockEntity(pos1);
			this.level.setBlock(pos1, Blocks.AIR.defaultBlockState(), 2);
			this.level.setBlock(pos2, bs, 2);
			this.level.getBlockEntity(pos2).load(nbt);;
		}else {
			this.level.setBlock(pos1, Blocks.AIR.defaultBlockState(), 2);
			this.level.setBlock(pos2, bs, 2);
		}
	}

}
