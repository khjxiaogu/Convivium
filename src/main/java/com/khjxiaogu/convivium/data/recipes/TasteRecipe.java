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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teammoeg.caupona.data.IDataRecipe;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;

public class TasteRecipe extends IDataRecipe {
	public Map<String,Float> variantData;
	public int priority;
	public Ingredient item;
	public static DeferredHolder<RecipeSerializer<?>,RecipeSerializer<?>> SERIALIZER;
	public static DeferredHolder<RecipeType<?>,RecipeType<Recipe<?>>> TYPE;
	public static List<RecipeHolder<TasteRecipe>> recipes;
	public static final MapCodec<TasteRecipe> CODEC=RecordCodecBuilder.mapCodec(t->t.group(
		Codec.compoundList(Codec.STRING, Codec.FLOAT).optionalFieldOf("variants").forGetter(o->Optional.of(o.variantData.entrySet().stream().map(e->Pair.of(e.getKey(),e.getValue())).collect(Collectors.toList()))),
		Codec.INT.optionalFieldOf("priority",0).forGetter(o->o.priority),
		Ingredient.CODEC.fieldOf("item").forGetter(o->o.item)
	).apply(t, TasteRecipe::new));
	public TasteRecipe(Optional<List<Pair<String, Float>>> variantData, int priority, Ingredient item) {
		this.variantData=new HashMap<>();
		variantData.ifPresent(o->o.stream().forEach(p->this.variantData.put(p.getFirst(),p.getSecond())));
		this.priority = priority;
		this.item = item;
	}
	
	public TasteRecipe(Map<String, Float> variantData, int priority, Ingredient item) {
		super();
		this.variantData = variantData;
		this.priority = priority;
		this.item = item;
	}

	/*
	public TasteRecipe(ResourceLocation id,FriendlyByteBuf pb) {
		super(id);
		item=Ingredient.fromNetwork(pb);
		priority=pb.readVarInt();
		variantData=SUtils.fromPacket(pb);
		
	}*/
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER.get();
	}

	@Override
	public RecipeType<?> getType() {
		return TYPE.get();
	}
	/*public void write(FriendlyByteBuf pb) {
		item.toNetwork(pb);
		pb.writeVarInt(priority);
		SUtils.toPacket(pb, variantData);
	}*/
}
