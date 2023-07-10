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

import com.khjxiaogu.convivium.data.recipes.TasteRecipe;
import com.teammoeg.caupona.data.CPRecipeSerializer;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class CVRecipes {
	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister
			.create(ForgeRegistries.RECIPE_SERIALIZERS, CVMain.MODID);
	public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister
			.create(ForgeRegistries.RECIPE_TYPES, CVMain.MODID);
	static {
		/*StewCookingRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("cooking",
				() -> new CPRecipeSerializer<StewCookingRecipe>(StewCookingRecipe::new, StewCookingRecipe::new,
						StewCookingRecipe::write));*/
		TasteRecipe.SERIALIZER=RECIPE_SERIALIZERS.register("taste",()->new CPRecipeSerializer<>(TasteRecipe::new,TasteRecipe::new,TasteRecipe::write));
	}

	static {
		//StewCookingRecipe.TYPE = RECIPE_TYPES.register("stew",()->RecipeType.simple(new ResourceLocation(CPMain.MODID ,"stew")));
	}
}