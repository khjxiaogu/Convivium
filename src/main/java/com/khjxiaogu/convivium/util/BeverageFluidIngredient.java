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

package com.khjxiaogu.convivium.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;
import com.khjxiaogu.convivium.CVComponents;
import com.khjxiaogu.convivium.CVFluids;
import com.khjxiaogu.convivium.CVIngredients;
import com.khjxiaogu.convivium.data.recipes.BeverageTypeRecipe;
import com.khjxiaogu.convivium.data.recipes.RelishRecipe;
import com.khjxiaogu.convivium.data.recipes.relishcondition.RelishCondition;
import com.khjxiaogu.convivium.data.recipes.relishcondition.RelishConditions;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teammoeg.caupona.util.FloatemTagStack;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.FluidIngredientType;

public class BeverageFluidIngredient extends FluidIngredient {
	public static final MapCodec<BeverageFluidIngredient> CODEC=RecordCodecBuilder.mapCodec(t->t.group(
		Codec.list(Ingredient.CODEC).optionalFieldOf("required",ImmutableList.of()).forGetter(o->o.must),
		Codec.list(Ingredient.CODEC).optionalFieldOf("optional",ImmutableList.of()).forGetter(o->o.optional),
		Codec.list(RelishConditions.CODEC).optionalFieldOf("relish",ImmutableList.of()).forGetter(o->o.relish),
		Codec.list(Codec.STRING).optionalFieldOf("allowedRelish",ImmutableList.of()).forGetter(o->o.allowedRelish),
		Codec.FLOAT.fieldOf("density").forGetter(o->o.density)
		).apply(t, BeverageFluidIngredient::new));
	public static final StreamCodec<RegistryFriendlyByteBuf,BeverageFluidIngredient> STREAM_CODEC=StreamCodec.composite(
		Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()),o->o.must,
		Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()),o->o.optional,
		RelishConditions.STREAM_CODEC.apply(ByteBufCodecs.list()),o->o.relish,
		ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()),o->o.allowedRelish,
		ByteBufCodecs.FLOAT,o->o.density,
		BeverageFluidIngredient::new);
	public List<Ingredient> must;
	public List<Ingredient> optional;
	public List<RelishCondition> relish;
	public List<String> allowedRelish;
	public float density;


	public BeverageFluidIngredient(List<Ingredient> must, List<Ingredient> optional, List<RelishCondition> relish, List<String> allowedRelish, float density) {
		super();
		this.must = must;
		this.optional = optional;
		this.relish = relish;
		this.allowedRelish = allowedRelish;
		this.density = density;
	}

	@Override
	public boolean test(FluidStack fluidStack) {
		BeverageInfo info=fluidStack.get(CVComponents.BEVERAGE_INFO);
		if(info==null)return false;
		BeveragePendingContext ctx=new BeveragePendingContext(info);
		if (ctx.getTotalItems() < density)
			return false;
		
		if(!relish.isEmpty())
			if(!relish.stream().anyMatch(t->t.test(ctx)))
				return false;
		if(!allowedRelish.isEmpty()) {
			if(ctx.relishes.keySet().stream().anyMatch(t->!allowedRelish.contains(t)))
				return false;
		}
		List<FloatemTagStack> filtered=new ArrayList<>(ctx.getItems());
		if(!must.isEmpty()) {
			for(Ingredient i:must) {
				if(!filtered.removeIf(t->i.test(t.getStack())))
					return false;
			}
		}
		if(!optional.isEmpty())
			for(FloatemTagStack is:filtered) {
				if(!optional.stream().anyMatch(e->e.test(is.getStack())))
					return false;
			}

		return true;
	}
	private Set<Holder<Fluid>> display;
	@Override
	protected Stream<Holder<Fluid>> generateFluids() {
		if(display==null) {
			Set<Holder<Fluid>> disps=new HashSet<>();
			for(String s1:RelishRecipe.recipes.keySet())
				for(String s2:RelishRecipe.recipes.keySet()) {
					BeverageInfo bi=new BeverageInfo(s1,s2);
					BeveragePendingContext ctx = new BeveragePendingContext(bi);
					if(!relish.isEmpty())
						if(!relish.stream().anyMatch(t->t.test(ctx))) {
							disps.add(BuiltInRegistries.FLUID.wrapAsHolder(
							BeverageTypeRecipe.sorted.stream().filter(t -> t.value().matches(ctx)).map(t -> t.value().output).findFirst()
							.orElse(CVFluids.MIXED_FLUID.get())));
						}
				}
			if(disps.isEmpty()) {
				disps.add(CVFluids.MIXED_FLUID);
			}
			display=disps;
		}
		return display.stream();
	}

	@Override
	public boolean isSimple() {
		return false;
	}

	@Override
	public FluidIngredientType<?> getType() {
		return CVIngredients.BEVERAGE_FLUID_INGREDIENT.get();
	}

	@Override
	public int hashCode() {
		return Objects.hash(allowedRelish, density, must, optional, relish);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		BeverageFluidIngredient other = (BeverageFluidIngredient) obj;
		return Objects.equals(allowedRelish, other.allowedRelish) && Float.floatToIntBits(density) == Float.floatToIntBits(other.density) && Objects.equals(must, other.must)
			&& Objects.equals(optional, other.optional) && Objects.equals(relish, other.relish);
	}


}
