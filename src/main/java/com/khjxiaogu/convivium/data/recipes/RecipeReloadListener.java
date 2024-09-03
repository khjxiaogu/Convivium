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

package com.khjxiaogu.convivium.data.recipes;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Stopwatch;
import com.khjxiaogu.convivium.CVMain;

import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RecipesUpdatedEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
@EventBusSubscriber
public class RecipeReloadListener implements ResourceManagerReloadListener {
	ReloadableServerResources data;
	public static final Logger logger = LogManager.getLogger(CVMain.MODNAME + " recipe generator");

	public RecipeReloadListener(ReloadableServerResources dpr) {
		data = dpr;
	}

	@Override
	public void onResourceManagerReload(@Nonnull ResourceManager resourceManager) {
		buildRecipeLists(data.getRecipeManager());
	}

	RecipeManager clientRecipeManager;


	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onRecipesUpdated(RecipesUpdatedEvent event) {
		System.out.println("triggered recipeUpdated");
		RecipeReloadListener.buildRecipeLists(event.getRecipeManager());
	}

	static int generated_fv = 0;


	public static void buildRecipeLists(RecipeManager recipeManager) {
		
		Collection<RecipeHolder<?>> recipes = recipeManager.getRecipes();
		if (recipes.size() == 0)
			return;
	
		logger.info("Building recipes...");
		Stopwatch sw = Stopwatch.createStarted();
		BeverageTypeRecipe.sorted = filterRecipes(recipes, BeverageTypeRecipe.class, BeverageTypeRecipe.TYPE).collect(Collectors.toList());
		BeverageTypeRecipe.sorted.sort((t2, t1) -> t1.value().getPriority() - t2.value().getPriority());
		ContainingRecipe.recipes=filterRecipes(recipes,ContainingRecipe.class,ContainingRecipe.TYPE).collect(Collectors.toMap(t->t.value().fluid, t->t));

		ConvertionRecipe.recipes=filterRecipes(recipes,ConvertionRecipe.class,ConvertionRecipe.TYPE).collect(Collectors.toList());
		ConvertionRecipe.activeLevel=new HashSet<>();
		ConvertionRecipe.recipes.forEach(t->ConvertionRecipe.activeLevel.add(t.value().temperature));
		GrindingRecipe.recipes=filterRecipes(recipes,GrindingRecipe.class,GrindingRecipe.TYPE).collect(Collectors.toList());
		RelishFluidRecipe.recipes=filterRecipes(recipes,RelishFluidRecipe.class,RelishFluidRecipe.TYPE).collect(Collectors.toMap(t->t.value().fluid, t->t));
		RelishRecipe.recipes=filterRecipes(recipes,RelishRecipe.class,RelishRecipe.TYPE).collect(Collectors.toMap(t->t.value().relishName, t->t));
		SwayRecipe.recipes=filterRecipes(recipes,SwayRecipe.class,SwayRecipe.TYPE).collect(Collectors.toList());
		TasteRecipe.recipes=filterRecipes(recipes,TasteRecipe.class,TasteRecipe.TYPE).collect(Collectors.toList());
		RelishItemRecipe.recipes=filterRecipes(recipes,RelishItemRecipe.class,RelishItemRecipe.TYPE).collect(Collectors.toList());
		BasinRecipe.recipes=filterRecipes(recipes,BasinRecipe.class,BasinRecipe.TYPE).collect(Collectors.toList());
		sw.stop();
		logger.info("Recipes built, cost {}", sw);
	}


	@SuppressWarnings("unchecked")
	static <R extends Recipe<?>> Stream<RecipeHolder<R>> filterRecipes(Collection<RecipeHolder<?>> recipes, Class<R> class1,
			DeferredHolder<RecipeType<?>,RecipeType<Recipe<?>>> recipeType) {
		return recipes.stream().filter(iRecipe -> iRecipe.value().getType() == recipeType.get()).map(t->(RecipeHolder<R>)t);
	}
}
