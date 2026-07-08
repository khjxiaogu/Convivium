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

import com.khjxiaogu.convivium.util.SUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teammoeg.caupona.data.IDataRecipe;

import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;

public class TasteRecipe extends IDataRecipe {
	public Object2FloatOpenHashMap<String> variantData;
	public int priority;
	public Ingredient item;
	public static DeferredHolder<RecipeSerializer<?>,RecipeSerializer<TasteRecipe>> SERIALIZER;
	public static DeferredHolder<RecipeType<?>,RecipeType<TasteRecipe>> TYPE;
	public static List<RecipeHolder<TasteRecipe>> recipes;
	public static final MapCodec<TasteRecipe> CODEC=RecordCodecBuilder.mapCodec(t->t.group(
		SUtils.VARIANTS_CODEC.fieldOf("variants").forGetter(o->o.variantData),
		Codec.INT.optionalFieldOf("priority",0).forGetter(o->o.priority),
		Ingredient.CODEC.fieldOf("item").forGetter(o->o.item)
	).apply(t, TasteRecipe::new));
	public static final StreamCodec<RegistryFriendlyByteBuf,TasteRecipe> STREAM_CODEC=StreamCodec.composite(
			SUtils.VARIANTS_STREAM_CODEC,o->o.variantData,
			ByteBufCodecs.VAR_INT,o->o.priority,
			Ingredient.CONTENTS_STREAM_CODEC,o->o.item,
			TasteRecipe::new
			);
	public TasteRecipe(Map<String,Float> variantData, int priority, Ingredient item) {
		super();
		this.variantData = new Object2FloatOpenHashMap<String>(variantData);
		this.priority = priority;
		this.item = item;
	}
	@Override
	public RecipeSerializer<? extends IDataRecipe> getSerializer() {
		return SERIALIZER.get();
	}

	@Override
	public RecipeType<? extends IDataRecipe> getType() {
		return TYPE.get();
	}
}
