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

package com.khjxiaogu.convivium.blocks.foods;

import java.util.function.Supplier;

import com.khjxiaogu.convivium.CVComponents;
import com.khjxiaogu.convivium.CVMain;
import com.khjxiaogu.convivium.util.BeverageInfo;
import com.teammoeg.caupona.item.EdibleBlock;
import com.teammoeg.caupona.util.CreativeTabItemHelper;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;

public class BeverageItem extends EdibleBlock {
	public final BeverageBlock bl;
	public final boolean isSmpl;
	public final boolean nameAfterFluid;
	Supplier<Fluid> fluid;
	public BeverageItem(BeverageBlock block,Supplier<Fluid> fluid, Properties props,boolean isSmpl,boolean nameAfterFluid) {
		super(block, props.stacksTo(1));
		bl = block;
		this.fluid=fluid;
		this.isSmpl=isSmpl;
		this.nameAfterFluid=nameAfterFluid;
	}


	@Override
	public Component getName(ItemStack itemStack) {
		if(!nameAfterFluid)
			return super.getName(itemStack);
		ResourceHandler<FluidResource> tint=Capabilities.Fluid.ITEM.getCapability(itemStack, ItemAccess.forStack(itemStack));
		if(tint==null)
			return super.getName(itemStack);
		FluidStack fs=FluidUtil.getStack(tint, 0);
		if(fs.isEmpty())
			return super.getName(itemStack);
		
		return Component.translatable(descriptionId+"_of",fs.getHoverName());
	}


	public void addCreativeHints(ItemStack stack) {
		if(!isSmpl) {
			super.addCreativeHints(stack);
		}
	}
	@Override
	public int getUseDuration(ItemStack stack,LivingEntity entity) {
		return 16;
	}
	@Override
	public void fillItemCategory(CreativeTabItemHelper helper) {
		if (helper.isType(CVMain.MAIN_TAB)) {
			ItemStack is = new ItemStack(this);
			if(fluid!=null)
			Utils.writeItemFluid(is, fluid.get());
			addCreativeHints(is);
			helper.accept(is,3);
		}
	}
	public static BeverageInfo getInfo(ItemStack stack) {
		BeverageInfo info= stack.get(CVComponents.BEVERAGE_INFO);
		if(info==null)
			return new BeverageInfo();
		return info;
	}

}
