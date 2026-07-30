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

package com.khjxiaogu.convivium.datagen;

import com.khjxiaogu.convivium.data.recipes.TasteRecipe;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class TasteRecipeBuilder{
	private int priority;
	private Ingredient item;
	private Identifier rl;
	VariantDataBuilder<TasteRecipeBuilder> vars=new VariantDataBuilder<TasteRecipeBuilder>(this);
	private final Provider registries;
	public TasteRecipeBuilder(Identifier rl,Provider registries) {
		this.rl = rl;
		this.registries=registries;
	}
	public VariantDataBuilder<TasteRecipeBuilder> vars(){
		return vars;
	}
	public TasteRecipeBuilder item(Ingredient igd) {
		item=igd;
		return this;
	}
	public TasteRecipeBuilder item(ItemStack igd) {
		return item(Ingredient.of(igd.getItem()));
	}
	public TasteRecipeBuilder item(Item igd) {
		return item(Ingredient.of(igd));
	}
	public TasteRecipeBuilder item(TagKey<Item> igd) {
		return item(Ingredient.of(registries.getOrThrow(igd)));
	}
	public TasteRecipeBuilder priority(int ig) {
		priority=ig;
		return this;
	}
	public void end(RecipeOutput out) {
		out.accept(ResourceKey.create(Registries.RECIPE, rl),new TasteRecipe(vars.variantData, priority, item),null);
	}
}
