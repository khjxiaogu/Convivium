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

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.khjxiaogu.convivium.util.FloatSizedOrCatalystIngredient;
import com.khjxiaogu.convivium.util.SUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teammoeg.caupona.data.IDataRecipe;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ConvertionRecipe extends IDataRecipe {
	public static Map<Object, RecipeHolder<ConvertionRecipe>> recipes;

	public static List<RecipeHolder<ConvertionRecipe>> sorted;
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
		FloatSizedOrCatalystIngredient.NESTED_CODEC.fieldOf("items").forGetter(o->o.item),
		SUtils.VARIANTS_CODEC.fieldOf("vairants").forGetter(o->o.variantData),
		SizedFluidIngredient.CODEC.fieldOf("fluidIn").forGetter(o->o.in),
		FluidStackTemplate.CODEC.fieldOf("fluidOut").forGetter(o->o.out),
		Codec.BOOL.fieldOf("heated").forGetter(o->o.heated),
		Codec.INT.fieldOf("time").forGetter(o->o.processTime),
		Codec.BOOL.fieldOf("consumeAll").forGetter(o->o.consumeExtra)
		).apply(t, ConvertionRecipe::new));
	public static final StreamCodec<RegistryFriendlyByteBuf,ConvertionRecipe> STREAM_CODEC=StreamCodec.composite(
		FloatSizedOrCatalystIngredient.STREAM_CODEC,o->o.item,
		SUtils.VARIANTS_STREAM_CODEC,o->o.variantData,
		SizedFluidIngredient.STREAM_CODEC,o->o.in,
		FluidStackTemplate.STREAM_CODEC,o->o.out,
		ByteBufCodecs.BOOL,o->o.heated,
		ByteBufCodecs.VAR_INT,o->o.processTime,
		ByteBufCodecs.BOOL,o->o.consumeExtra,
		ConvertionRecipe::new);
	public FloatSizedOrCatalystIngredient item;
	public Object2FloatOpenHashMap<String> variantData;
	public SizedFluidIngredient in;
	public FluidStackTemplate out;
	public boolean heated=false;
	public int processTime=200;
	public boolean consumeExtra;
	

	public ConvertionRecipe(FloatSizedOrCatalystIngredient item, SizedFluidIngredient in, FluidStackTemplate out,
			boolean heated, int processTime, boolean consumeExtra) {
		this.item = item;
		this.in = in;
		this.out = out;
		this.heated = heated;
		this.processTime = processTime;
		this.consumeExtra = consumeExtra;
	}
	public ConvertionRecipe(FloatSizedOrCatalystIngredient item, Object2FloatOpenHashMap<String> variantData, SizedFluidIngredient in,FluidStackTemplate out, boolean heated, int processTime, boolean consumeExtra) {
		this(item,in,out,heated,processTime,consumeExtra);
		this.variantData=variantData;
	}


}
