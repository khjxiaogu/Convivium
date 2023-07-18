package com.khjxiaogu.convivium.blocks.aqueduct;

import com.khjxiaogu.convivium.CVBlockEntityTypes;
import com.khjxiaogu.convivium.CVTags;
import com.teammoeg.caupona.blocks.CPRegisteredEntityBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AqueductBlock extends CPRegisteredEntityBlock<AqueductBlockEntity> {
	public static final EnumProperty<AqueductConnection> CONN=EnumProperty.create("connection", AqueductConnection.class);
	public AqueductBlock(Properties blockProps) {
		super(blockProps, CVBlockEntityTypes.AQUEDUCT);
		// TODO Auto-generated constructor stub
	}
	private static VoxelShape shape=Shapes.or(Block.box(2, 0, 2, 14, 9, 14),Block.box(0, 9, 0, 16, 16, 16));
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
		// TODO Auto-generated method stub
		super.createBlockStateDefinition(pBuilder);
		pBuilder.add(CONN);
	}

	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		BlockState blockstate = this.defaultBlockState().setValue(CONN,AqueductConnection.get(pContext.getClickedFace().getOpposite(),pContext.getHorizontalDirection()));
		return blockstate;
	}

	/**
	 * Update the provided state given the provided neighbor direction and neighbor
	 * state, returning a new state.
	 * For example, fences make their connections to the passed in state if
	 * possible, and wet concrete powder immediately
	 * returns its solidified counterpart.
	 * Note that this method should ideally consider only the specific direction
	 * passed in.
	 */
	public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel,
			BlockPos pCurrentPos, BlockPos pFacingPos) {
		if(pFacingState.is(CVTags.Blocks.aqueduct)) {
			AqueductConnection c=pState.getValue(CONN).connects(pFacing);
			if(c!=null)
				return pState.setValue(CONN, c);
		}else {
			AqueductConnection c=pState.getValue(CONN).disconnects(pFacing);
			if(c!=null)
				return pState.setValue(CONN, c);
		}
		return pState;
	}

	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return shape;
	}


}
