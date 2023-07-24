package com.khjxiaogu.convivium.blocks.camellia;

import com.khjxiaogu.convivium.CVBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CamelliaBlock extends BushBlock {

	public CamelliaBlock(Properties pProperties) {
		super(pProperties);
		// TODO Auto-generated constructor stub
	}
	static final VoxelShape shape = Block.box(4, 0, 4, 12, 16, 12);

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return shape;
	}

	@Override
	public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
		// TODO Auto-generated method stub
		BlockPos above = pPos.above();
		if (pRandom.nextInt(25) == 0 && pLevel.getRawBrightness(above, 0) >= 9) {
			if (pLevel.getBlockState(above).isAir()) {
				pLevel.setBlockAndUpdate(above, CVBlocks.CAMELLIA_FLOWER.get().defaultBlockState());
			}
		}
	}

	@Override
	public boolean isRandomlyTicking(BlockState pState) {
		// TODO Auto-generated method stub
		return true;
	}

}
