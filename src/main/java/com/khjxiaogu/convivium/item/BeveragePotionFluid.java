package com.khjxiaogu.convivium.item;

import com.khjxiaogu.convivium.CVBlocks;
import com.teammoeg.caupona.item.EdibleBlock;
import com.teammoeg.caupona.util.CreativeTabItemHelper;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

public class BeveragePotionFluid extends EdibleBlock {

	public BeveragePotionFluid(Properties props) {
		super(CVBlocks.BEVERAGE.get(), props);
	}
	@Override
	public void fillItemCategory(CreativeTabItemHelper helper) {
	}
	@Override
	public SoundEvent getEatingSound() {
		return SoundEvents.GENERIC_DRINK;
	}
	
}
