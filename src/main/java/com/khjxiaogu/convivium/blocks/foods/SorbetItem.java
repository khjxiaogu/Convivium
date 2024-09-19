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

package com.khjxiaogu.convivium.blocks.foods;

import java.util.List;
import java.util.function.Supplier;

import com.khjxiaogu.convivium.CVBlocks;
import com.khjxiaogu.convivium.CVComponents;
import com.khjxiaogu.convivium.CVMain;
import com.khjxiaogu.convivium.util.BeverageInfo;
import com.teammoeg.caupona.data.recipes.BowlContainingRecipe;
import com.teammoeg.caupona.item.EdibleBlock;
import com.teammoeg.caupona.util.CreativeTabItemHelper;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public class SorbetItem extends EdibleBlock {
	public static final FoodProperties fakefood = new FoodProperties.Builder().nutrition(5).saturationModifier(0.8f)
			.build();
	public final Block bl;
	public final boolean isSmpl;
	Supplier<Fluid> fluid;
	public SorbetItem(Block block,Supplier<Fluid> fluid,Properties props,boolean isSmpl) {
		super(block,props.food(fakefood).craftRemainder(CVBlocks.FLAT_BREAD.get().asItem()).stacksTo(1));
		bl = block;
		this.isSmpl=isSmpl;
		this.fluid=fluid;
	}


	@Override
	public InteractionResult place(BlockPlaceContext context) {
		return InteractionResult.PASS;
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

	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.DRINK;
	}
	@Override
	public void fillItemCategory(CreativeTabItemHelper helper) {
		if (helper.isType(CVMain.MAIN_TAB)) {
			ItemStack is = new ItemStack(this);
			if(fluid!=null)
			Utils.writeItemFluid(is,fluid.get());
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

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		BeverageInfo info = SorbetItem.getInfo(stack);
		info.appendTooltip(tooltip);
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
	}
	@Override
	public FoodProperties getFoodProperties(ItemStack stack, LivingEntity entity) {
		BeverageInfo info= stack.get(CVComponents.BEVERAGE_INFO);
		if(info==null)
			return fakefood;
		return info.getFood(5,4).usingConvertsTo(Items.GLASS_BOTTLE).build();
		
	}

}
