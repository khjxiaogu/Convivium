/*
 * Copyright (c) 2022 TeamMoeg
 *
 * This file is part of Caupona.
 *
 * Caupona is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Caupona is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * Specially, we allow this software to be used alongside with closed source software Minecraft(R) and Forge or other modloader.
 * Any mods or plugins can also use apis provided by forge or com.teammoeg.caupona.api without using GPL or open source.
 *
 * You should have received a copy of the GNU General Public License
 * along with Caupona. If not, see <https://www.gnu.org/licenses/>.
 */

package com.khjxiaogu.convivium.blocks.kinetics;

import java.util.HashSet;
import java.util.Set;

import com.khjxiaogu.convivium.CVBlockEntityTypes;
import com.khjxiaogu.convivium.CVBlocks;
import com.khjxiaogu.convivium.CVConfig;
import com.teammoeg.caupona.blocks.CPHorizontalBlock;
import com.teammoeg.caupona.blocks.pot.StewPotBlockEntity;
import com.teammoeg.caupona.blocks.stove.IStove;
import com.teammoeg.caupona.network.CPBaseBlockEntity;
import com.teammoeg.caupona.util.LazyTickWorker;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class AeolipileBlockEntity extends CPBaseBlockEntity {
	LazyTickWorker process;
	public int speed;
	public int waterTick;
	private int r;

	public AeolipileBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
		super(CVBlockEntityTypes.AOELIPILE.get(), pWorldPosition, pBlockState);
		r = CVConfig.SERVER.kineticRange.get();
		process = new LazyTickWorker(Mth.ceil(CVConfig.SERVER.kineticValidation.get() / 2f),()->{
			Set<BlockPos> pss = getAll();
			for (BlockPos pos : pss) {
				if (level.getBlockEntity(pos) instanceof KineticTransferBlockEntity bath)
					bath.setSpeed(speed);
			}
			return true;
		});
	}

	@Override
	public void handleMessage(short type, int data) {
	}

	@Override
	public void readCustomNBT(CompoundTag nbt, boolean isClient) {
		speed = nbt.getInt("heatSpeed");
		waterTick = nbt.getInt("water");
	}

	@Override
	public void writeCustomNBT(CompoundTag nbt, boolean isClient) {
		nbt.putInt("heatSpeed", speed);
		nbt.putInt("water", waterTick);
	}

	private boolean dist(BlockPos crn, BlockPos orig) {
		return Mth.abs(crn.getX() - orig.getX()) <= r && Mth.abs(crn.getZ() - orig.getZ()) <= r && Mth.abs(crn.getY() - orig.getY()) <= r;
	}
	public void findNext(Level l, BlockPos orig,Direction dir,Set<BlockPos> pos) {
		BlockPos crn=orig.relative(dir);
		if(l.isLoaded(crn)&&isCage(l.getBlockState(crn),dir.getOpposite()))
			findNext(l,crn,crn,true,pos);
	}
	public boolean isCage(BlockState bs,Direction dir) {
		return bs.is(CVBlocks.cage.get());
	}
	public boolean isCog(BlockState bs,Direction dir) {
		return bs.is(CVBlocks.cog.get());
	}
	public void findNext(Level l, BlockPos crn, BlockPos orig,boolean isCage,Set<BlockPos> pos) {
		if (dist(crn, orig)) {
			if (pos.add(crn)) {
				for (Direction dir : Utils.horizontals) {
					BlockPos act = crn.relative(dir);
					if (l.isLoaded(act) && 
							((isCage&&isCog(l.getBlockState(act),dir.getOpposite()))||
							(!isCage&&this.isCage(l.getBlockState(act),dir.getOpposite())))) {
						findNext(l, act, orig,!isCage,pos);
					}
				}
			}
		}
	}
	public BlockPos getFacingPos() {
		return this.getBlockPos().relative(this.getBlockState().getValue(CPHorizontalBlock.FACING).getClockWise());
	}
	@SuppressWarnings("resource")
	public Set<BlockPos> getAll() {
		Set<BlockPos> poss = new HashSet<>();
		findNext(this.getLevel(), this.getBlockPos(),this.getBlockState().getValue(CPHorizontalBlock.FACING).getClockWise(), poss);
		return poss;
	}

	@Override
	public void tick() {
		if (this.level.isClientSide)
			return;
		process.tick();
		if (level.getBlockEntity(worldPosition.below(2)) instanceof IStove stove&&level.getBlockEntity(worldPosition.below()) instanceof StewPotBlockEntity stew_pot&&stew_pot.canAddFluid()) {
			FluidTank tank=stew_pot.getTank();
			if(tank.getFluid().getFluid().isSame(Fluids.WATER)) {
				if(waterTick==0) {
					if(!tank.drain(new FluidStack(Fluids.WATER,1),FluidAction.EXECUTE).isEmpty())
						waterTick=20;
				}
				if(waterTick>0) {
					waterTick--;
					int nh = stove.requestHeat();
					if (speed != nh) {
						if(speed==0) {
							this.level.setBlockAndUpdate(worldPosition,this.getBlockState().setValue(KineticBasedBlock.ACTIVE, true));
						}
						process.enqueue();
						speed = nh;
					}
					this.setChanged();
					return;
				}
			}
			
		}
		if (speed != 0) {
			process.enqueue();
			this.level.setBlockAndUpdate(worldPosition,this.getBlockState().setValue(KineticBasedBlock.ACTIVE, false));
			speed = 0;
			this.setChanged();
		}
	}

}
