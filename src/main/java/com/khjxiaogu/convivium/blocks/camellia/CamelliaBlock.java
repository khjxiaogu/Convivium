/*
 * Copyright (c) 2024 IEEM Trivium Society/khjxiaogu
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

package com.khjxiaogu.convivium.blocks.camellia;

import com.khjxiaogu.convivium.CVBlocks;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CamelliaBlock extends BushBlock implements BonemealableBlock {
    public static final MapCodec<CamelliaBlock> CODEC = simpleCodec(CamelliaBlock::new);
	public CamelliaBlock(Properties pProperties) {
		super(pProperties);
		// TODO Auto-generated constructor stub
	}
	static final VoxelShape shape = Block.box(4, 0, 4, 12, 16, 12);
	static final VoxelShape shapesel=Shapes.or(Block.box(0, 3, 0, 16, 16, 16), shape);
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return shapesel;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos,
			CollisionContext pContext) {
		// TODO Auto-generated method stub
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

	public boolean isBonemealSuccess(Level pLevel, RandomSource pRandom, BlockPos pPos, BlockState pStatex) {
		BlockPos above = pPos.above();
		BlockState abovebs = pLevel.getBlockState(above);
		return (abovebs.isAir() && pRandom.nextInt(3) == 0) || (abovebs.is(CVBlocks.CAMELLIA_FLOWER.get())
				&& ((BonemealableBlock) abovebs.getBlock()).isBonemealSuccess(pLevel, pRandom, above, abovebs));
	}

	public void performBonemeal(ServerLevel pLevel, RandomSource pRandom, BlockPos pPos, BlockState pState) {
		BlockPos above = pPos.above();
		BlockState abovebs = pLevel.getBlockState(above);
		if (abovebs.isAir()) {
			pLevel.setBlockAndUpdate(above, CVBlocks.CAMELLIA_FLOWER.get().defaultBlockState());
		}else if(abovebs.is(CVBlocks.CAMELLIA_FLOWER.get())){
			((BonemealableBlock) abovebs.getBlock()).performBonemeal(pLevel, pRandom, above, abovebs);
		}
	}

	@Override
	public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
		BlockPos above = pos.above();
		BlockState abovebs = level.getBlockState(above);
		return abovebs.isAir() || (abovebs.is(CVBlocks.CAMELLIA_FLOWER.get())
				&& ((BonemealableBlock) abovebs.getBlock()).isValidBonemealTarget(level, above, abovebs));
	}

	@Override
	protected MapCodec<? extends BushBlock> codec() {
		return CODEC;
	}

}
