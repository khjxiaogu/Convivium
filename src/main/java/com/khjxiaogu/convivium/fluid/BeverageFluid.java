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

package com.khjxiaogu.convivium.fluid;

import java.util.Optional;

import com.khjxiaogu.convivium.util.BeverageInfo;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class BeverageFluid extends ForgeFlowingFluid {

	@Override
	public Fluid getSource() {
		return this;
		
	}

	@Override
	public Fluid getFlowing() {
		return this;
	}

	@Override
	public Item getBucket() {
		return Items.AIR;
	}

	@Override
	protected BlockState createLegacyBlock(FluidState state) {
		return Blocks.AIR.defaultBlockState();
	}

	@Override
	public boolean isSame(Fluid fluidIn) {
		return fluidIn == this;
	}

	@Override
	public boolean isSource(FluidState p_207193_1_) {
		return true;
	}

	public static Optional<BeverageInfo> getInfo(FluidStack stack) {
		if (stack.hasTag()) {
			CompoundTag nbt = stack.getChildTag("beverage");
			if (nbt != null)
				return Optional.of(new BeverageInfo(nbt));
		}
		return Optional.empty();
	}

	public static void setInfo(FluidStack stack, BeverageInfo si) {
		if(si!=null)
			stack.getOrCreateTag().put("beverage", si.save());
	}
	public static void setInfoForClient(FluidStack stack, BeverageInfo si) {
		if(si!=null)
			stack.getOrCreateTag().put("beverage", si.saveClient());
	}
	@Override
	public int getAmount(FluidState p_207192_1_) {
		return 0;
	}

	public BeverageFluid(Properties properties) {
		super(properties);
	}


}