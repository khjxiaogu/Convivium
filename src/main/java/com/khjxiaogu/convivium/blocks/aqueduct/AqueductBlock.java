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

package com.khjxiaogu.convivium.blocks.aqueduct;

import java.util.EnumMap;

import com.khjxiaogu.convivium.CVBlockEntityTypes;
import com.khjxiaogu.convivium.CVTags;
import com.teammoeg.caupona.blocks.CPRegisteredEntityBlock;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AqueductBlock extends CPRegisteredEntityBlock<AqueductBlockEntity> implements AqueductConnectable{
	public static final EnumProperty<AqueductConnection> CONN=EnumProperty.create("connection", AqueductConnection.class);
	public AqueductBlock(Properties blockProps) {
		super(blockProps, CVBlockEntityTypes.AQUEDUCT);
		this.registerDefaultState(this.defaultBlockState().setValue(CONN, AqueductConnection.A));
		// TODO Auto-generated constructor stub
	}
	
	private static VoxelShape nw=Block.box( 0,10, 0, 2,16, 2);
	private static VoxelShape se=Block.box(14,10,14,16,16,16);
	private static VoxelShape sw=Block.box(14,10, 0,16,16, 2);
	private static VoxelShape ne=Block.box( 0,10,14, 2,16,16);
	private static VoxelShape base=Shapes.or(Block.box(2, 0, 2, 14, 10, 14),nw,sw,ne,se);
	private static VoxelShape n= Block.box( 2,10, 0,14,16, 2);
	private static VoxelShape s= Block.box( 2,10,14,14,16,16);
	private static VoxelShape e= Block.box(14,10, 2,16,16,14);
	private static VoxelShape w= Block.box( 0,10, 2, 2,16,14);
	private static EnumMap<AqueductConnection,VoxelShape> shapes=new EnumMap<>(AqueductConnection.class);
	static {
		for(AqueductConnection ac:AqueductConnection.values()) {
			VoxelShape shape=base;
			if(ac.n)
				shape=Shapes.or(shape, n);
			if(ac.e)
				shape=Shapes.or(shape, e);
			if(ac.s)
				shape=Shapes.or(shape, s);
			if(ac.w)
				shape=Shapes.or(shape, w);
			shapes.put(ac, shape);
		}
	}
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
		// TODO Auto-generated method stub
		super.createBlockStateDefinition(pBuilder);
		pBuilder.add(CONN);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		AqueductConnection conn=AqueductConnection.A;
		for(Direction d:Utils.horizontals) {
			BlockPos pos=pContext.getClickedPos().relative(d);
			BlockState rel=pContext.getLevel().getBlockState(pos);
			if(rel.is(CVTags.Blocks.AQUEDUCT)) {
				boolean canConnect=true;
				if(rel.getBlock() instanceof AqueductConnectable con) {
					canConnect=con.canConnect(pos, rel, d.getOpposite());
				}
				if(canConnect)
					conn=conn.connects(d);
			}
		}
		
		return super.getStateForPlacement(pContext).setValue(CONN, conn);
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
		if(pFacingState.is(CVTags.Blocks.AQUEDUCT)&&pFacing.getAxis()!=Axis.Y) {
			AqueductConnection c=pState.getValue(CONN).connects(pFacing);
			
			boolean canConnect=true;
			if(pFacingState.getBlock() instanceof AqueductConnectable con) {
				canConnect=con.canConnect(pFacingPos, pFacingState,pFacing.getOpposite());
			}
			if(c!=null&&canConnect) {
				return pState.setValue(CONN, c);
			}
		}
		AqueductConnection c=pState.getValue(CONN).disconnects(pFacing);
		if(c!=null)
			return pState.setValue(CONN, c);
		
		return pState;
	}

	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return Shapes.block();
	}

	@Override
	public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos,
			CollisionContext pContext) {
		// TODO Auto-generated method stub
		return shapes.get(pState.getValue(CONN));
	}

	@Override
	public void stepOn(Level pLevel, BlockPos pPos, BlockState pState, Entity pEntity) {
		if(pPos.equals(pEntity.blockPosition()))
			if(pLevel.getBlockEntity(pPos) instanceof AqueductBlockEntity aq) {
				if(aq.tonxt>0&&aq.from!=null) {
					Direction[] dirs=pState.getValue(AqueductBlock.CONN).getNext(aq.from);
					if(dirs.length>0) {
						Vec3i v3=dirs[pLevel.random.nextInt(dirs.length)].getNormal();
						Vec3i vd=v3.offset(aq.from.getOpposite().getNormal());
						//System.out.println(v3);
						float spd=40f/aq.tonxt;
						pEntity.addDeltaMovement(Vec3.atLowerCornerOf(v3).scale(0.0125).add(Vec3.atLowerCornerOf(vd).scale(0.0125)).scale(spd*0.5));
						
					}
				}
			}
		
	}

	@Override
	public boolean canConnect(BlockPos pos, BlockState state, Direction from) {
		if(state.getValue(CONN).connects(from).canConnectTo(from)) {
			return true;
		}
		return false;
	}




}
