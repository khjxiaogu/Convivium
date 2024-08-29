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

package com.khjxiaogu.convivium;

import com.khjxiaogu.convivium.data.recipes.BasinRecipe;
import com.khjxiaogu.convivium.data.recipes.BeverageTypeRecipe;
import com.khjxiaogu.convivium.data.recipes.ContainingRecipe;
import com.khjxiaogu.convivium.data.recipes.ConvertionRecipe;
import com.khjxiaogu.convivium.data.recipes.GrindingRecipe;
import com.khjxiaogu.convivium.data.recipes.RelishFluidRecipe;
import com.khjxiaogu.convivium.data.recipes.RelishItemRecipe;
import com.khjxiaogu.convivium.data.recipes.RelishRecipe;
import com.khjxiaogu.convivium.data.recipes.SwayRecipe;
import com.khjxiaogu.convivium.data.recipes.TasteRecipe;
import com.teammoeg.caupona.data.CPRecipeSerializer;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CVRecipes {
	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister
		.create(BuiltInRegistries.RECIPE_SERIALIZER, CVMain.MODID);
	public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister
		.create(BuiltInRegistries.RECIPE_TYPE, CVMain.MODID);
	static {
		TasteRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("taste", () -> new CPRecipeSerializer<>(TasteRecipe.CODEC));
		GrindingRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("grinding", () -> new CPRecipeSerializer<>(GrindingRecipe.CODEC));
		ContainingRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("containing", () -> new CPRecipeSerializer<>(ContainingRecipe.CODEC));
		ConvertionRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("convertion", () -> new CPRecipeSerializer<>(ConvertionRecipe.CODEC));
		RelishFluidRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("relish_fluid", () -> new CPRecipeSerializer<>(RelishFluidRecipe.CODEC));
		RelishRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("relish", () -> new CPRecipeSerializer<>(RelishRecipe.CODEC));
		BeverageTypeRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("beverage", () -> new CPRecipeSerializer<>(BeverageTypeRecipe.CODEC));
		SwayRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("sway", () -> new CPRecipeSerializer<>(SwayRecipe.CODEC));
		RelishItemRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("relish_item", () -> new CPRecipeSerializer<>(RelishItemRecipe.CODEC));
		BasinRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("basin", () -> new CPRecipeSerializer<>(BasinRecipe.CODEC));
	}

	static {
		TasteRecipe.TYPE = createType("taste");
		GrindingRecipe.TYPE = createType("grinding");
		ContainingRecipe.TYPE = createType("containing");
		ConvertionRecipe.TYPE = createType("convertion");
		RelishFluidRecipe.TYPE = createType("relish_fluid");
		RelishRecipe.TYPE = createType("relish");
		BeverageTypeRecipe.TYPE = createType("beverage");
		SwayRecipe.TYPE = createType("sway");
		RelishItemRecipe.TYPE = createType("relish_item");
		BasinRecipe.TYPE = createType("basin");
	}

	public static DeferredHolder<RecipeType<?>, RecipeType<Recipe<?>>> createType(String s) {
		return RECIPE_TYPES.register(s, RecipeType::simple);
	}
}