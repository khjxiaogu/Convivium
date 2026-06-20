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

package com.khjxiaogu.convivium.data.recipes;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Stopwatch;
import com.khjxiaogu.convivium.CVMain;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredHolder;
@EventBusSubscriber
public class RecipeReloadListener{
	public static final Logger logger = LogManager.getLogger(CVMain.MODNAME + " recipe generator");

	private RecipeReloadListener() {
	}

	static int generated_fv = 0;


	public static void buildRecipeLists(RecipeMap recipes) {
	
		logger.info("Building recipes...");
		Stopwatch sw = Stopwatch.createStarted();
		BeverageTypeRecipe.sorted = filterRecipes(recipes, BeverageTypeRecipe.class, BeverageTypeRecipe.TYPE).collect(Collectors.toList());
		BeverageTypeRecipe.sorted.sort((t2, t1) -> t1.value().getPriority() - t2.value().getPriority());
		ConvertionRecipe.recipes=filterRecipes(recipes,ConvertionRecipe.class,ConvertionRecipe.TYPE).collect(Collectors.toMap(t->t.id().identifier(), t->t));
		ConvertionRecipe.heatedRecipes=ConvertionRecipe.recipes.values().stream().filter(t->t.value().heated).collect(Collectors.toList());
		ConvertionRecipe.unheatedRecipes=ConvertionRecipe.recipes.values().stream().filter(t->!t.value().heated).collect(Collectors.toList());
		GrindingRecipe.recipes=filterRecipes(recipes,GrindingRecipe.class,GrindingRecipe.TYPE).collect(Collectors.toMap(t->t.id().identifier(), t->t));
		RelishFluidRecipe.recipes=filterRecipes(recipes,RelishFluidRecipe.class,RelishFluidRecipe.TYPE).collect(Collectors.toMap(t->t.value().fluid, t->t));
		RelishRecipe.recipes=filterRecipes(recipes,RelishRecipe.class,RelishRecipe.TYPE).collect(Collectors.toMap(t->t.value().relishName, t->t));
		SwayRecipe.recipes=filterRecipes(recipes,SwayRecipe.class,SwayRecipe.TYPE).collect(Collectors.toList());
		TasteRecipe.recipes=filterRecipes(recipes,TasteRecipe.class,TasteRecipe.TYPE).collect(Collectors.toList());
		BasinRecipe.recipes=filterRecipes(recipes,BasinRecipe.class,BasinRecipe.TYPE).collect(Collectors.toMap(t->t.id().identifier(), t->t));
		sw.stop();
		logger.info("Recipes built, cost {}", sw);
	}


	static <I extends RecipeInput,R extends Recipe<I>> Stream<RecipeHolder<R>> filterRecipes(RecipeMap recipes, Class<R> class1,
			DeferredHolder<RecipeType<?>,RecipeType<R>> recipeType) {
		return recipes.byType(recipeType.get()).stream().filter(t->class1.isInstance(t.value())).map(t->(RecipeHolder<R>)t);
	}
}
