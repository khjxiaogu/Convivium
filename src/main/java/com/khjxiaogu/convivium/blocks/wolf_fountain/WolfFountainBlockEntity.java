package com.khjxiaogu.convivium.blocks.wolf_fountain;

import com.khjxiaogu.convivium.CVBlockEntityTypes;
import com.khjxiaogu.convivium.blocks.aqueduct.AqueductControllerBlock;
import com.khjxiaogu.convivium.blocks.kinetics.KineticBasedBlock;
import com.khjxiaogu.convivium.blocks.kinetics.KineticTransferBlockEntity;
import com.teammoeg.caupona.network.CPBaseBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class WolfFountainBlockEntity extends CPBaseBlockEntity {

	public WolfFountainBlockEntity( BlockPos pWorldPosition, BlockState pBlockState) {
		super(CVBlockEntityTypes.WOLF_FOUNTAIN.get(), pWorldPosition, pBlockState);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void handleMessage(short type, int data) {
		// TODO Auto-generated method stub

	}

	@Override
	public void readCustomNBT(CompoundTag nbt, boolean isClient, Provider registries) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeCustomNBT(CompoundTag nbt, boolean isClient, Provider registries) {
		// TODO Auto-generated method stub

	}

	@Override
	public void tick() {
		BlockState state=this.getBlockState();
		Direction facing=state.getValue(AqueductControllerBlock.FACING);
		boolean active=state.getValue(KineticBasedBlock.ACTIVE);
		BlockPos facingPos=getBlockPos().relative(facing);
		BlockState bs=this.getLevel().getBlockState(facingPos);
		BlockPos facingPos2=getBlockPos().relative(facing.getOpposite());
		BlockState bs2=this.getLevel().getBlockState(facingPos2);
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
		}else if(bs2.hasProperty(KineticBasedBlock.ACTIVE)&&bs2.getValue(KineticBasedBlock.ACTIVE)) {
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
	}

}
