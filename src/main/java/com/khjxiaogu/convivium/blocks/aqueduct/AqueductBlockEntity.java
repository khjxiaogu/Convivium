package com.khjxiaogu.convivium.blocks.aqueduct;

import com.khjxiaogu.convivium.CVBlockEntityTypes;
import com.teammoeg.caupona.network.CPBaseBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class AqueductBlockEntity extends CPBaseBlockEntity {
	Direction from;
	int nxt;
	int tonxt;
	int snxt;
	public AqueductBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
		super(CVBlockEntityTypes.AQUEDUCT.get(), pWorldPosition, pBlockState);
		// TODO Auto-generated constructor stub
	}

	public AqueductBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
		super(pType, pWorldPosition, pBlockState);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void handleMessage(short type, int data) {
		// TODO Auto-generated method stub

	}

	@Override
	public void readCustomNBT(CompoundTag nbt, boolean isClient) {
		// TODO Auto-generated method stub
		if(!isClient) {
			from=Direction.values()[nbt.getInt("from")];
			tonxt=nbt.getInt("processMax");
			nxt=nbt.getInt("process");
			snxt=nbt.getInt("processA");
		}
	}

	@Override
	public void writeCustomNBT(CompoundTag nbt, boolean isClient) {
		// TODO Auto-generated method stub
		if(!isClient) {
			nbt.putInt("from", from.ordinal());
			nbt.putInt("processMax", tonxt);
			nbt.putInt("process", nxt);
			nbt.putInt("processA", snxt);
		}
	}
	
	public void addPush(Direction from,int nxt) {
		this.from=from;
		if(tonxt==0) {
			this.nxt=nxt;
		}
		tonxt=nxt;
		if(tonxt>0)
			snxt=Mth.ceil(tonxt/8f)*8;
	}
	@Override
	public void tick() {
		if(this.level.isClientSide)
			return;
		// TODO Auto-generated method stub
		if(snxt>0) {
			snxt--;
			int pos=snxt%8;
			Vec3 center=this.getBlockPos().getCenter();
			int dx=from.getStepX();
			int dz=from.getStepZ();
			for(int i=0;i<2;i++) {
				double rx=from.getClockWise().getStepX()*(Math.random()-0.5)*7/8f;
				double rz=from.getClockWise().getStepZ()*(Math.random()-0.5)*7/8f;
				((ServerLevel)this.level).sendParticles(ParticleTypes.DOLPHIN,center.x()+0.5*dx+rx,center.y()+0.95,center.z()+0.5*dz+rz,1,-dx, 0,-dz,0.1);
				System.out.println("tic"+dx+","+dz);
			}
			//System.out.println("tic"+center);
		}
		if(tonxt>0) {
			
			nxt--;
			if(nxt<=0) {
				Direction[] dirs=this.getBlockState().getValue(AqueductBlock.CONN).getNext(from);
				if(dirs.length>0) {
					Direction moving=dirs[this.level.random.nextInt(dirs.length)];
					move(moving);
					//if(move(moving)) {
					if(tonxt>0)
						snxt=Mth.ceil(tonxt/8f)*8;
					tonxt=0;
					nxt=0;
					/*}else
						nxt=5;*/
					
				}
			}
		}
	}
	public boolean move(Direction dir) {
		BlockPos src=this.getBlockPos().above();
		BlockPos rbe=this.getBlockPos().relative(dir);
		BlockPos dest=rbe.above();
		if(this.getLevel().getBlockEntity(rbe)instanceof AqueductBlockEntity aqueduct) {
			aqueduct.addPush(dir.getOpposite(), tonxt);
			BlockEntity be=this.level.getBlockEntity(src);
			BlockState bs=this.level.getBlockState(src);
			BlockState bs2=this.level.getBlockState(dest);
			if(bs.is(Blocks.AIR))return true;
			if(!bs2.is(Blocks.AIR))return false;
			if(be!=null) {
				CompoundTag nbt=be.serializeNBT();
				this.level.removeBlockEntity(src);
				this.level.setBlock(src, Blocks.AIR.defaultBlockState(), 2);
				this.level.setBlock(dest, bs, 2);
				this.level.getBlockEntity(dest).load(nbt);;
			}else {
				this.level.setBlock(src, Blocks.AIR.defaultBlockState(), 2);
				this.level.setBlock(dest, bs, 2);
			}
			return true;
		}
		return false;
	}

}
