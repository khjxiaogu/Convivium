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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.khjxiaogu.convivium.util.BeverageInfo;
import com.khjxiaogu.convivium.util.SUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teammoeg.caupona.data.IDataRecipe;
import com.teammoeg.caupona.util.SizedOrCatalystIngredient;

import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ConvertionRecipe extends IDataRecipe {
	public static Map<Object, RecipeHolder<ConvertionRecipe>> recipes;

	public static List<RecipeHolder<ConvertionRecipe>> heatedRecipes;
	public static List<RecipeHolder<ConvertionRecipe>> unheatedRecipes;
	public static Set<Integer> activeLevel;
	public static DeferredHolder<RecipeType<?>,RecipeType<ConvertionRecipe>> TYPE;
	public static DeferredHolder<RecipeSerializer<?>,RecipeSerializer<ConvertionRecipe>> SERIALIZER;

	@Override
	public RecipeSerializer<ConvertionRecipe> getSerializer() {
		return SERIALIZER.get();
	}

	@Override
	public RecipeType<ConvertionRecipe> getType() {
		return TYPE.get();
	}
	public static final MapCodec<ConvertionRecipe> CODEC=RecordCodecBuilder.mapCodec(t->t.group(
		SizedOrCatalystIngredient.NESTED_CODEC.optionalFieldOf("items").forGetter(o->o.item),
		SUtils.VARIANTS_CODEC.fieldOf("vairants").forGetter(o->o.variantData),
		SizedFluidIngredient.CODEC.fieldOf("fluidIn").forGetter(o->o.in),
		FluidStackTemplate.CODEC.fieldOf("fluidOut").forGetter(o->o.out),
		Codec.BOOL.fieldOf("heated").forGetter(o->o.heated),
		Codec.INT.fieldOf("time").forGetter(o->o.processTime)
		).apply(t, ConvertionRecipe::new));
	public static final StreamCodec<RegistryFriendlyByteBuf,ConvertionRecipe> STREAM_CODEC=StreamCodec.composite(
		ByteBufCodecs.optional(SizedOrCatalystIngredient.STREAM_CODEC),o->o.item,
		SUtils.VARIANTS_STREAM_CODEC,o->o.variantData,
		SizedFluidIngredient.STREAM_CODEC,o->o.in,
		FluidStackTemplate.STREAM_CODEC,o->o.out,
		ByteBufCodecs.BOOL,o->o.heated,
		ByteBufCodecs.VAR_INT,o->o.processTime,
		ConvertionRecipe::new);
	public final Optional<SizedOrCatalystIngredient> item;
	public final Object2FloatOpenHashMap<String> variantData;
	public final SizedFluidIngredient in;
	public final FluidStackTemplate out;
	public final boolean heated;
	public final int processTime;
	
	public static RecipeHolder<ConvertionRecipe> test(ItemStack stack,BeverageInfo in,int inAmount,boolean heated) {
		int total=0;
		for(int ent:in.relishes.values()) {
			total+=ent;
		}
		List<FluidStack> stacks=new ArrayList<>();
		for(Entry<Holder<Fluid>> fs:in.relishes.object2IntEntrySet())
			stacks.add(new FluidStack(fs.getKey(),inAmount*fs.getIntValue()/total));
		if(heated) {
			Optional<RecipeHolder<ConvertionRecipe>> recipe=heatedRecipes.stream().filter(t->t.value().test(stack, stacks)).findFirst();
			if(recipe.isPresent())
				return recipe.get();
		}
		Optional<RecipeHolder<ConvertionRecipe>> recipe=unheatedRecipes.stream().filter(t->t.value().test(stack, stacks)).findFirst();

		return recipe.orElse(null);
	}
	public boolean test(ItemStack stack,List<FluidStack> stacks) {
		if(item.isPresent()) {
			if(!item.get().test(stack))
				return false;
		}
		
		for(FluidStack fs:stacks) {
			if(in.test(fs))
				return true;
		}
		return false;
	}
	public ConvertionRecipe(Optional<SizedOrCatalystIngredient> item, SizedFluidIngredient in, FluidStackTemplate out,
			boolean heated, int processTime) {
		this(item,new Object2FloatOpenHashMap<String>(),in,out,heated,processTime);
	}
	public ConvertionRecipe(SizedOrCatalystIngredient item, SizedFluidIngredient in,FluidStackTemplate out, boolean heated, int processTime) {
		this(Optional.of(item),in,out,heated,processTime);
	}
	public ConvertionRecipe(SizedFluidIngredient in,FluidStackTemplate out, boolean heated, int processTime) {
		this(Optional.empty(),in,out,heated,processTime);
	}
	public ConvertionRecipe(Optional<SizedOrCatalystIngredient> item, Map<String,Float> variantData, SizedFluidIngredient in,FluidStackTemplate out, boolean heated, int processTime) {
		
		this.item = item;
		this.in = in;
		this.out = out;
		this.heated = heated;
		this.processTime = processTime;
		this.variantData=new Object2FloatOpenHashMap<String>(variantData);
	}
	public ConvertionRecipe(SizedOrCatalystIngredient item, Map<String,Float> variantData, SizedFluidIngredient in,FluidStackTemplate out, boolean heated, int processTime) {
		this(Optional.of(item),variantData,in,out,heated,processTime);
	}
	public ConvertionRecipe(Map<String,Float> variantData, SizedFluidIngredient in,FluidStackTemplate out, boolean heated, int processTime) {
		this(Optional.empty(),variantData,in,out,heated,processTime);
	}



}
