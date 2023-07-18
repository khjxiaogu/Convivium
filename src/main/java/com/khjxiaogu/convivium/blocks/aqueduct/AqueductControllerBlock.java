package com.khjxiaogu.convivium.blocks.aqueduct;

import com.khjxiaogu.convivium.CVBlockEntityTypes;
import com.khjxiaogu.convivium.blocks.kinetics.KineticBasedBlock;
import com.teammoeg.caupona.blocks.CPHorizontalEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;

public class AqueductControllerBlock extends CPHorizontalEntityBlock<AqueductControllerBlockEntity> {
	public AqueductControllerBlock(Properties blockProps) {
		super(CVBlockEntityTypes.AQUEDUCT_MAIN,blockProps);
		// TODO Auto-generated constructor stub
		this.registerDefaultState(this.defaultBlockState().setValue(KineticBasedBlock.ACTIVE, false));
	}
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		// TODO Auto-generated method stub
		super.createBlockStateDefinition(builder);
		builder.add(KineticBasedBlock.ACTIVE);
	}



}
