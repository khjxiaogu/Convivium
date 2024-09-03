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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teammoeg.caupona.api.CauponaHooks;
import com.teammoeg.caupona.components.IFoodInfo;
import com.teammoeg.caupona.data.IDataRecipe;
import com.teammoeg.caupona.util.SizedOrCatalystFluidIngredient;
import com.teammoeg.caupona.util.SizedOrCatalystIngredient;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.DeferredHolder;

public class BasinRecipe extends IDataRecipe {
	public static List<RecipeHolder<BasinRecipe>> recipes;
	public static DeferredHolder<RecipeType<?>,RecipeType<Recipe<?>>> TYPE;
	public static DeferredHolder<RecipeSerializer<?>,RecipeSerializer<?>> SERIALIZER;

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER.get();
	}

	@Override
	public RecipeType<?> getType() {
		return TYPE.get();
	}
	public Fluid base;
	public float density = 0;
	public SizedOrCatalystFluidIngredient in;
	public List<ItemStack> output;
	public SizedOrCatalystIngredient item;
	public int processTime=200;
	public boolean requireBasin;
	public static final MapCodec<BasinRecipe> CODEC=RecordCodecBuilder.mapCodec(t->t.group(
		BuiltInRegistries.FLUID.byNameCodec().optionalFieldOf("base").forGetter(o->Optional.ofNullable(o.base)),
		Codec.FLOAT.optionalFieldOf("density", 0f).forGetter(o->o.density),
		SizedOrCatalystFluidIngredient.FLAT_CODEC.optionalFieldOf("fluidIn").forGetter(o->Optional.ofNullable(o.in)),
		SizedOrCatalystIngredient.FLAT_CODEC.optionalFieldOf("item").forGetter(o->Optional.ofNullable(o.item)),
		Codec.list(ItemStack.CODEC).fieldOf("outputs").forGetter(o->o.output),
		Codec.INT.fieldOf("time").forGetter(o->o.processTime),
		Codec.BOOL.fieldOf("leadBasin").forGetter(o->o.requireBasin)
		).apply(t, BasinRecipe::new));
	public BasinRecipe( SizedOrCatalystFluidIngredient in,SizedOrCatalystIngredient item, List<ItemStack> output, int inputCount, int processTime,boolean requireBasin) {
		this.in = in;
		this.output = output;
		this.item = item;
		this.processTime = processTime;
		this.requireBasin=requireBasin;

	}
	public BasinRecipe(Optional<Fluid> base, float density, Optional<SizedOrCatalystFluidIngredient> in, 
		Optional<SizedOrCatalystIngredient> item,List<ItemStack> output, int processTime,boolean requireBasin) {
		this.base = base.orElse(null);
		this.density = density;
		this.in = in.orElse(null);
		this.output = output;
		this.item = item.orElse(null);
		this.processTime = processTime;
		this.requireBasin=requireBasin;
	}


	public static BasinRecipe testAll(FluidStack f,ItemStack is,boolean isLead) {
		return recipes.stream().map(t->t.value()).filter(t->!t.requireBasin||isLead).filter(t -> t.test(f)).filter(t->t.item==null||t.item.test(is)).findFirst().orElse(null);
	}

	public boolean test(FluidStack f) {
		if(!in.test(f))
			return false;

		if (density != 0 || base != null) {
			IFoodInfo info=CauponaHooks.getInfo(f).orElse(null);
			if(info==null)
				return false;
			if (base != null && base!=info.getBase())
				return false;
			if (info.getDensity() < density)
				return false;
		}
		return true;
	}

	public List<ItemStack> handle(FluidStack f,ItemStack isi) {
		f.shrink(in.amount());
		isi.shrink(item.count());
		List<ItemStack> fss=new ArrayList<>();
		for(ItemStack is:output)
			fss.add(is.copy());
		return fss;
	}



	public static boolean testInput(ItemStack stack) {
		return recipes.stream().map(t->t.value()).anyMatch(t->t.item.test(stack));
	}

}
