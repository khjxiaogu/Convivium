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
import com.teammoeg.caupona.client.CPParticles;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AeolipileBlock extends KineticBasedBlock<AeolipileBlockEntity> {
	public AeolipileBlock(Properties blockProps) {
		super(CVBlockEntityTypes.AOELIPILE, blockProps);
	}
	static final VoxelShape shape = Block.box(3, 0, 3, 13, 13, 13);
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return shape;
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
	public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, RandomSource rand) {
		
		if(stateIn.getValue(ACTIVE)&&rand.nextInt(4)==0) {
			Direction facing=stateIn.getValue(FACING);
			Axis axis=facing.getAxis();
			float dZ=0;
			float dX=0;
			float movX=0;
			float movZ=0;
			if(axis==Axis.X) {
				dZ=0.5f;
				movX=1;
			}else {
				dX=0.5f;
				movZ=1;
			}
			worldIn.addParticle(CPParticles.STEAM.get(), pos.getX() +dX, pos.getY() + 1,pos.getZ() + dZ, 0.0D,0.0D, 0.0D);
			worldIn.addParticle(CPParticles.STEAM.get(), pos.getX() +dX+movX, pos.getY() + 1,pos.getZ() + dZ+movZ, 0.0D,0.0D, 0.0D);
		}

	}


}
