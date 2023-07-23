package com.khjxiaogu.convivium.fluid;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class BaseFluid extends ForgeFlowingFluid {

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

	@Override
	public int getAmount(FluidState p_207192_1_) {
		return 0;
	}

	public BaseFluid(Properties properties) {
		super(properties);
	}
}