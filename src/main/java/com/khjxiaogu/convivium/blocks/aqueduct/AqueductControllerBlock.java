package com.khjxiaogu.convivium.blocks.aqueduct;

import com.khjxiaogu.convivium.CVBlockEntityTypes;
import com.khjxiaogu.convivium.blocks.kinetics.KineticBasedBlock;
import com.khjxiaogu.convivium.blocks.kinetics.KineticTransferBlockEntity;
import com.khjxiaogu.convivium.util.RotationUtils;
import com.teammoeg.caupona.blocks.CPHorizontalEntityBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.phys.Vec3;

public class AqueductControllerBlock extends CPHorizontalEntityBlock<AqueductControllerBlockEntity> {
	public AqueductControllerBlock(Properties blockProps) {
		super(CVBlockEntityTypes.AQUEDUCT_MAIN,blockProps);
		// TODO Auto-generated constructor stub
		this.registerDefaultState(this.defaultBlockState().setValue(KineticBasedBlock.ACTIVE, false).setValue(KineticBasedBlock.LOCKED, false));
	}
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		// TODO Auto-generated method stub
		super.createBlockStateDefinition(builder);
		builder.add(KineticBasedBlock.ACTIVE).add(KineticBasedBlock.LOCKED);
	}
	@Override
	public void stepOn(Level pLevel, BlockPos pPos, BlockState pState, Entity pEntity) {
		if(pState.getValue(KineticBasedBlock.ACTIVE)) {
			Direction dir=pState.getValue(AqueductControllerBlock.FACING);
			Direction moving;
			if(RotationUtils.isBlackGrid(pPos.relative(dir))) {
				moving=dir.getClockWise();
			}else {
				moving=dir.getCounterClockWise();
			}
			Vec3i v3=moving.getNormal();
			
			BlockPos facingPos=pPos.relative(pState.getValue(AqueductControllerBlock.FACING));
			if(pLevel.getBlockEntity(facingPos) instanceof KineticTransferBlockEntity ent) {
				pEntity.addDeltaMovement(Vec3.atLowerCornerOf(v3).scale(0.0225*ent.getSpeed()));
			}
			
			
		}
	}


}
