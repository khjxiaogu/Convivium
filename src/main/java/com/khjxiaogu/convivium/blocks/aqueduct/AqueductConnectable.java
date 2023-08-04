package com.khjxiaogu.convivium.blocks.aqueduct;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public interface AqueductConnectable {
	boolean canConnect(BlockPos pos,BlockState state,Direction from);
}
