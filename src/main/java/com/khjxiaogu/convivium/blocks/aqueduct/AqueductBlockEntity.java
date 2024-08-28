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
import com.khjxiaogu.convivium.client.CVParticles;
import com.teammoeg.caupona.network.CPBaseBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class AqueductBlockEntity extends CPBaseBlockEntity {
	Direction from;
	int nxt;
	int tonxt;
	public AqueductBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
		super(CVBlockEntityTypes.AQUEDUCT.get(), pWorldPosition, pBlockState);
		// TODO Auto-generated constructor stub
	}

	public AqueductBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
		super(pType, pWorldPosition, pBlockState);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void handleMessage(short type, int data) {
		// TODO Auto-generated method stub

	}

	@Override
	public void readCustomNBT(CompoundTag nbt, boolean isClient,HolderLookup.Provider ra) {
		if(nbt.contains("from"))
			from=Direction.values()[nbt.getInt("from")];
		tonxt=nbt.getInt("processMax");
		if(!isClient) {
			
			nxt=nbt.getInt("process");
		}
	}

	@Override
	public void writeCustomNBT(CompoundTag nbt, boolean isClient,HolderLookup.Provider ra) {
		if(from!=null)
			nbt.putInt("from", from.ordinal());
		nbt.putInt("processMax", tonxt);
		if(!isClient) {
			
			nbt.putInt("process", nxt);
		}
	}
	
	public void addPush(Direction from,int nxt) {
		if(this.from!=null&&tonxt!=0)return;
		this.from=from;
		if(tonxt==0) {
			this.nxt=nxt;
		}
		tonxt=nxt;
		this.syncData();
	}
	@Override
	public void tick() {
		if(this.level.isClientSide) {
			if(tonxt>0) {
				if(from!=null) {
					Vec3 center=this.getBlockPos().getCenter();
		
					if(Math.random()<0.25d) {
						int dx=from.getStepX();
						int dz=from.getStepZ();
						double rx=from.getClockWise().getStepX()*(Math.random()-0.5)*6/8f;
						double rz=from.getClockWise().getStepZ()*(Math.random()-0.5)*6/8f;
						this.level.addParticle(CVParticles.SPLASH.get(),center.x()+0.5*dx+rx,center.y()+0.45,center.z()+0.5*dz+rz,-dx*0.048, 0,-dz*0.048);
						//System.out.println("tic"+dx+","+dz);
					}
					Direction[] dirs=this.getBlockState().getValue(AqueductBlock.CONN).getNext(from);
					for(Direction d:dirs) {
						if(Math.random()<0.25d/dirs.length) {
							Direction di=d.getOpposite();
							int dx=di.getStepX();
							int dz=di.getStepZ();
							double rx=di.getClockWise().getStepX()*(Math.random()-0.5)*6/8f;
							double rz=di.getClockWise().getStepZ()*(Math.random()-0.5)*6/8f;
							
							this.level.addParticle(CVParticles.SPLASH.get(),center.x()+0.46*dx+rx,center.y()+0.45,center.z()+0.46*dz+rz,-dx*0.048, 0,-dz*0.048);
							//System.out.println("tic"+dx+","+dz);
						}
					}
				}
				//System.out.println("tic"+center);
			}
			return;
		}
		// TODO Auto-generated method stub
		
		if(tonxt>0) {
			
			nxt--;
			if(nxt<=0) {
				Direction[] dirs=this.getBlockState().getValue(AqueductBlock.CONN).getNext(from);
				if(dirs.length>0) {
					Direction moving=dirs[this.level.random.nextInt(dirs.length)];
					move(moving);
					tonxt=0;
					nxt=0;
					this.syncData();
				}else {
					tonxt=0;
					nxt=0;
					this.syncData();
				}
			}
		}
	}
	public boolean move(Direction dir) {
		BlockPos src=this.getBlockPos().above();
		BlockPos rbe=this.getBlockPos().relative(dir);
		BlockPos dest=rbe.above();
		if(this.getLevel().getBlockEntity(rbe)instanceof AqueductBlockEntity aqueduct) {
			aqueduct.addPush(dir.getOpposite(), tonxt);
			BlockEntity be=this.level.getBlockEntity(src);
			BlockState bs=this.level.getBlockState(src);
			BlockState bs2=this.level.getBlockState(dest);
			if(bs.is(Blocks.AIR))return true;
			if(!bs.is(CVTags.Blocks.AQUEDUCT_MOVE))return false;
			if(!bs2.is(Blocks.AIR))return false;
			if(be!=null) {
				CompoundTag nbt=be.saveWithoutMetadata(this.getLevel().registryAccess());
				this.level.removeBlockEntity(src);
				this.level.setBlock(src, Blocks.AIR.defaultBlockState(), 2);
				this.level.removeBlockEntity(src);
				this.level.setBlock(dest, bs, 1|2);
				this.level.getBlockEntity(dest).loadWithComponents(nbt, this.getLevel().registryAccess());
			}else {
				this.level.setBlock(src, Blocks.AIR.defaultBlockState(), 2);
				this.level.setBlock(dest, bs, 1|2);
			}
			return true;
		}
		return false;
	}

}
