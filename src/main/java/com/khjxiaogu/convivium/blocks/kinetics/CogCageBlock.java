/*
 * Copyright (c) 2023 IEEM Trivium Society/khjxiaogu
 *
 * This file is part of Convivium.
 *
 * Convivium is free software: you can redistribute it and/or modify
 * it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 * the Free Software Foundation, version 3.
 *
 * Convivium is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 * You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 * along with Convivium. If not, see <https://www.gnu.org/licenses/>.
 */

package com.khjxiaogu.convivium.blocks.kinetics;

import com.khjxiaogu.convivium.CVBlockEntityTypes;
import com.khjxiaogu.convivium.CVBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CogCageBlock extends KineticBasedBlock<CogeCageBlockEntity> {
	VoxelShape shapeCog=Shapes.or(Block.box(-3, 3, -3, 19, 5, 19),Block.box(6, 0, 6, 10, 9, 10));
	VoxelShape shapeCage=Shapes.or(Block.box(2, 1, 2, 14, 7, 14),Block.box(6, 0, 6, 10, 9, 10));
	public CogCageBlock(Properties blockProps) {
		super(CVBlockEntityTypes.COG_CAGE, blockProps);
	}

	public VoxelShape getVisualShape(BlockState pState, BlockGetter pReader, BlockPos pPos, CollisionContext pContext) {
		return Shapes.empty();
	}

	public float getShadeBrightness(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
		return 1.0F;
	}

	public boolean propagatesSkylightDown(BlockState pState, BlockGetter pReader, BlockPos pPos) {
		return true;
	}

	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		if(pState.is(CVBlocks.cage.get()))
			return shapeCage;
		return shapeCog;
	}


}
