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

package com.khjxiaogu.convivium.datagen;

import com.khjxiaogu.convivium.data.recipes.TasteRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

public class TasteRecipeBuilder{
	private int priority;
	private Ingredient item;
	private ResourceLocation rl;
	VariantDataBuilder<TasteRecipeBuilder> vars=new VariantDataBuilder<TasteRecipeBuilder>(this);
	public TasteRecipeBuilder(ResourceLocation rl) {
		this.rl = rl;
	}
	public VariantDataBuilder<TasteRecipeBuilder> vars(){
		return vars;
	}
	public TasteRecipeBuilder item(Ingredient igd) {
		item=igd;
		return this;
	}
	public TasteRecipeBuilder priority(int ig) {
		priority=ig;
		return this;
	}
	public void end(RecipeOutput out) {
		out.accept(rl,new TasteRecipe(vars.variantData, priority, item),null);
	}
}
