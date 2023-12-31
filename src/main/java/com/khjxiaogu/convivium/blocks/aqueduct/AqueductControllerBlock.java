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

import com.khjxiaogu.convivium.CVBlockEntityTypes;
import com.khjxiaogu.convivium.CVTags;
import com.khjxiaogu.convivium.blocks.kinetics.KineticBasedBlock;
import com.khjxiaogu.convivium.blocks.kinetics.KineticTransferBlockEntity;
import com.khjxiaogu.convivium.util.RotationUtils;
import com.teammoeg.caupona.blocks.CPHorizontalEntityBlock;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

public class AqueductControllerBlock extends CPHorizontalEntityBlock<AqueductControllerBlockEntity> implements AqueductConnectable{
	public static final EnumProperty<AqueductMainConnection> CONN=EnumProperty.create("connection", AqueductMainConnection.class);
	
	public AqueductControllerBlock(Properties blockProps) {
		super(CVBlockEntityTypes.AQUEDUCT_MAIN,blockProps);
		// TODO Auto-generated constructor stub
		this.registerDefaultState(this.defaultBlockState().setValue(KineticBasedBlock.ACTIVE, false).setValue(KineticBasedBlock.LOCKED, false).setValue(CONN,AqueductMainConnection.N));
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
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		BlockState p=super.getStateForPlacement(pContext);
		AqueductMainConnection conn=AqueductMainConnection.N;
		Direction dir=p.getValue(FACING);
		for(Direction d:Utils.horizontals) {
			BlockPos pos=pContext.getClickedPos().relative(d);
			BlockState rel=pContext.getLevel().getBlockState(pos);
			if(rel.is(CVTags.Blocks.AQUEDUCT)) {
				boolean canConnect=true;
				if(rel.getBlock() instanceof AqueductConnectable con) {
					canConnect=con.canConnect(pos,rel,d.getOpposite());
				}
				if(canConnect)
				conn=conn.connects(dir,d);
			}
		}
		return p.setValue(CONN, conn);
	}
	private static VoxelShape[] shapes=new VoxelShape[16];
	
	public static int getShapeIndex(Direction dir,AqueductMainConnection con) {
		return (dir.ordinal()-2)<<2|con.ordinal();
	}
	static {
		shapes[getShapeIndex(Direction.EAST,AqueductMainConnection.L)]=shapes[getShapeIndex(Direction.WEST,AqueductMainConnection.R)]=Shapes.or(base,s,e,w);
		shapes[getShapeIndex(Direction.EAST,AqueductMainConnection.R)]=shapes[getShapeIndex(Direction.WEST,AqueductMainConnection.L)]=Shapes.or(base,n,e,w);
		shapes[getShapeIndex(Direction.NORTH,AqueductMainConnection.L)]=shapes[getShapeIndex(Direction.SOUTH,AqueductMainConnection.R)]=Shapes.or(base,n,s,e);
		shapes[getShapeIndex(Direction.NORTH,AqueductMainConnection.R)]=shapes[getShapeIndex(Direction.SOUTH,AqueductMainConnection.L)]=Shapes.or(base,n,s,w);
		shapes[getShapeIndex(Direction.NORTH,AqueductMainConnection.A)]=shapes[getShapeIndex(Direction.SOUTH,AqueductMainConnection.A)]=Shapes.or(base, n,s);
		shapes[getShapeIndex(Direction.EAST,AqueductMainConnection.A)]=shapes[getShapeIndex(Direction.WEST,AqueductMainConnection.A)]=Shapes.or(base, e,w);
		shapes[getShapeIndex(Direction.EAST,AqueductMainConnection.N)]=shapes[getShapeIndex(Direction.WEST,AqueductMainConnection.N)]=shapes[getShapeIndex(Direction.NORTH,AqueductMainConnection.N)]=shapes[getShapeIndex(Direction.SOUTH,AqueductMainConnection.N)]=Shapes.or(base,n,s,e,w);
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
		Direction dir=pState.getValue(FACING);
		if(pFacingState.is(CVTags.Blocks.AQUEDUCT)) {
			AqueductMainConnection c=pState.getValue(CONN).connects(dir,pFacing);
			boolean canConnect=true;
			if(pFacingState.getBlock() instanceof AqueductConnectable con) {
				canConnect=con.canConnect(pFacingPos, pFacingState,pFacing.getOpposite());
			}
			if(c!=null&&canConnect)
				return pState.setValue(CONN, c);
		}
		AqueductMainConnection c=pState.getValue(CONN).disconnects(dir,pFacing);
		if(c!=null)
			return pState.setValue(CONN, c);
		
		return pState;
	}
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		// TODO Auto-generated method stub
		super.createBlockStateDefinition(builder);
		builder.add(KineticBasedBlock.ACTIVE).add(KineticBasedBlock.LOCKED).add(CONN);
	}
	@Override
	public void stepOn(Level pLevel, BlockPos pPos, BlockState pState, Entity pEntity) {
		if(pPos.equals(pEntity.blockPosition()))
			if(pState.getValue(KineticBasedBlock.ACTIVE)) {
				Direction dir=pState.getValue(AqueductControllerBlock.FACING);
				Direction moving;
				if(RotationUtils.isBlackGrid(pPos)) {
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
	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return Shapes.block();
	}
	@Override
	public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos,
			CollisionContext pContext) {
		return shapes[getShapeIndex(pState.getValue(FACING),pState.getValue(CONN))];
	}
	@Override
	public boolean canConnect(BlockPos pos, BlockState state, Direction from) {
		// TODO Auto-generated method stub
		return from.getClockWise().getAxis()==state.getValue(FACING).getAxis();
	}


}
