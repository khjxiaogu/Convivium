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

import com.khjxiaogu.convivium.data.recipes.BeverageTypeRecipe;
import com.khjxiaogu.convivium.data.recipes.ContainingRecipe;
import com.khjxiaogu.convivium.data.recipes.ConvertionRecipe;
import com.khjxiaogu.convivium.data.recipes.GrindingRecipe;
import com.khjxiaogu.convivium.data.recipes.RelishFluidRecipe;
import com.khjxiaogu.convivium.data.recipes.RelishRecipe;
import com.khjxiaogu.convivium.data.recipes.SwayRecipe;
import com.khjxiaogu.convivium.data.recipes.TasteRecipe;
import com.teammoeg.caupona.data.CPRecipeSerializer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CVRecipes {
	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister
			.create(ForgeRegistries.RECIPE_SERIALIZERS, CVMain.MODID);
	public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister
			.create(ForgeRegistries.RECIPE_TYPES, CVMain.MODID);
	static {
		TasteRecipe.SERIALIZER=RECIPE_SERIALIZERS.register("taste",()->new CPRecipeSerializer<>(TasteRecipe::new,TasteRecipe::new,TasteRecipe::write));
		GrindingRecipe.SERIALIZER=RECIPE_SERIALIZERS.register("grinding",()->new CPRecipeSerializer<>(GrindingRecipe::new,GrindingRecipe::new,GrindingRecipe::write));
		ContainingRecipe.SERIALIZER=RECIPE_SERIALIZERS.register("containing",()->new CPRecipeSerializer<>(ContainingRecipe::new,ContainingRecipe::new,ContainingRecipe::write));
		ConvertionRecipe.SERIALIZER=RECIPE_SERIALIZERS.register("convertion",()->new CPRecipeSerializer<>(ConvertionRecipe::new,ConvertionRecipe::new,ConvertionRecipe::write));
		RelishFluidRecipe.SERIALIZER=RECIPE_SERIALIZERS.register("relish_fluid",()->new CPRecipeSerializer<>(RelishFluidRecipe::new,RelishFluidRecipe::new,RelishFluidRecipe::write));
		RelishRecipe.SERIALIZER=RECIPE_SERIALIZERS.register("relish",()->new CPRecipeSerializer<>(RelishRecipe::new,RelishRecipe::new,RelishRecipe::write));
		BeverageTypeRecipe.SERIALIZER=RECIPE_SERIALIZERS.register("beverage",()->new CPRecipeSerializer<>(BeverageTypeRecipe::new,BeverageTypeRecipe::new,BeverageTypeRecipe::write));
		SwayRecipe.SERIALIZER=RECIPE_SERIALIZERS.register("sway",()->new CPRecipeSerializer<>(SwayRecipe::new,SwayRecipe::new,SwayRecipe::write));
	}

	static {
		TasteRecipe.TYPE=createType("taste");
		GrindingRecipe.TYPE=createType("grinding");
		ContainingRecipe.TYPE=createType("containing");
		ConvertionRecipe.TYPE=createType("convertion");
		RelishFluidRecipe.TYPE=createType("relish_fluid");
		RelishRecipe.TYPE=createType("relish");
		BeverageTypeRecipe.TYPE=createType("beverage");
		SwayRecipe.TYPE=createType("sway");
	}
	public static RegistryObject<RecipeType<Recipe<?>>> createType(String s){
		return RECIPE_TYPES.register(s,()->RecipeType.simple(new ResourceLocation(CVMain.MODID ,s)));
	}
}