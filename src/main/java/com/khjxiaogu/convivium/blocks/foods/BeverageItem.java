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

package com.khjxiaogu.convivium.blocks.foods;

import java.util.List;

import com.khjxiaogu.convivium.CVMain;
import com.khjxiaogu.convivium.data.recipes.ContainingRecipe;
import com.khjxiaogu.convivium.util.BeverageInfo;
import com.teammoeg.caupona.item.EdibleBlock;
import com.teammoeg.caupona.util.CreativeTabItemHelper;
import com.teammoeg.caupona.util.SauteedFoodInfo;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class BeverageItem extends EdibleBlock {
	public static final FoodProperties fakefood = new FoodProperties.Builder().nutrition(0).saturationMod(0f)
			.build();
	public final BeverageBlock bl;
	public final boolean isSmpl;
	public BeverageItem(BeverageBlock block, Properties props,boolean isSmpl) {
		super(block, props.food(fakefood).craftRemainder(Items.GLASS_BOTTLE).stacksTo(1));
		bl = block;
		this.isSmpl=isSmpl;
	}


	public void addCreativeHints(ItemStack stack) {
		if(!isSmpl) {
			super.addCreativeHints(stack);
		}
	}
	@Override
	public int getUseDuration(ItemStack stack) {
		return 16;
	}

	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.DRINK;
	}
	@Override
	public void fillItemCategory(CreativeTabItemHelper helper) {
		if (helper.isType(CVMain.MAIN_TAB)) {
			ItemStack is = new ItemStack(this);
			
			is.getOrCreateTag().putString("type", Utils.getRegistryName(ContainingRecipe.reverseFluidType(this)).toString());
			addCreativeHints(is);
			helper.accept(is);
		}
	}
	public static BeverageInfo getInfo(ItemStack stack) {
		if (stack.hasTag()) {
			CompoundTag soupTag = stack.getTagElement("beverage");
			if (soupTag != null)
				return new BeverageInfo(soupTag);
		}
		return new BeverageInfo();
	}

	public static void setInfo(ItemStack stack, SauteedFoodInfo current) {
		if (!current.isEmpty())
			stack.getOrCreateTag().put("beverage", current.save());
	}

	@Override
	public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		BeverageInfo info = BeverageItem.getInfo(stack);
		info.appendTooltip(tooltip);
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
	}

	@Override
	public FoodProperties getFoodProperties(ItemStack stack, LivingEntity entity) {
		return getInfo(stack).getFood();
		
	}
}
