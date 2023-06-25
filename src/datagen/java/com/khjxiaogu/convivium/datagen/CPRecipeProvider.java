/*
 * Copyright (c) 2023 IEEM
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import com.google.gson.JsonObject;
import com.khjxiaogu.convivium.CVMain;
import com.teammoeg.caupona.data.IDataRecipe;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;

public class CPRecipeProvider extends RecipeProvider {
	private final HashMap<String, Integer> PATH_COUNT = new HashMap<>();

	static final Fluid water = fluid(mrl("nail_soup")), milk = fluid(mrl("scalded_milk")), stock = fluid(mrl("stock"));
	public static List<IDataRecipe> recipes = new ArrayList<>();

	public CPRecipeProvider(DataGenerator generatorIn) {
		super(generatorIn.getPackOutput());
	}

	@Override
	protected void buildRecipes(Consumer<FinishedRecipe> outx) {
		Consumer<IDataRecipe> out = r -> {
			outx.accept(new FinishedRecipe() {

				@Override
				public void serializeRecipeData(JsonObject jo) {
					r.serializeRecipeData(jo);
				}

				@Override
				public ResourceLocation getId() {
					return r.getId();
				}

				@Override
				public JsonObject serializeAdvancement() {
					return null;
				}

				@Override
				public ResourceLocation getAdvancementId() {
					return null;
				}

				@Override
				public RecipeSerializer<?> getType() {
					return r.getSerializer();
				}

			});
		};
		

	}



	private Fluid cpfluid(String name) {
		return ForgeRegistries.FLUIDS.getValue(new ResourceLocation(CVMain.MODID, name));
	}

	private Item cpitem(String name) {
		return ForgeRegistries.ITEMS.getValue(new ResourceLocation(CVMain.MODID, name));
	}

	private Item mitem(String name) {
		return ForgeRegistries.ITEMS.getValue(new ResourceLocation(name));
	}


	private Item item(ResourceLocation rl) {
		return ForgeRegistries.ITEMS.getValue(rl);
	}

	private static Fluid fluid(ResourceLocation rl) {
		return ForgeRegistries.FLUIDS.getValue(rl);
	}

	private static ResourceLocation mrl(String s) {
		return new ResourceLocation(CVMain.MODID, s);
	}

	private ResourceLocation ftag(String s) {
		return new ResourceLocation("forge", s);
	}

	private ResourceLocation mcrl(String s) {
		return new ResourceLocation(s);
	}

	private ResourceLocation rl(String s) {
		if (!s.contains("/"))
			s = "crafting/" + s;
		if (PATH_COUNT.containsKey(s)) {
			int count = PATH_COUNT.get(s) + 1;
			PATH_COUNT.put(s, count);
			return new ResourceLocation(CVMain.MODID, s + count);
		}
		PATH_COUNT.put(s, 1);
		return new ResourceLocation(CVMain.MODID, s);
	}


}
