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

public class RelishItemRecipe extends IDataRecipe {
	public Ingredient item;
	public String relish;
	public Map<String,Float> variantData=new HashMap<>();
	public static DeferredHolder<RecipeSerializer<?>,RecipeSerializer<?>> SERIALIZER;
	public static DeferredHolder<RecipeType<?>,RecipeType<Recipe<?>>> TYPE;
	public static List<RecipeHolder<RelishItemRecipe>> recipes;
	public static final MapCodec<RelishItemRecipe> CODEC=RecordCodecBuilder.mapCodec(t->t.group(
		Ingredient.CODEC.fieldOf("item").forGetter(o->o.item),
		Codec.STRING.fieldOf("relish").forGetter(o->o.relish),
		Codec.compoundList(Codec.STRING, Codec.FLOAT).optionalFieldOf("variants").forGetter(o->Optional.of(o.variantData.entrySet().stream().map(e->Pair.of(e.getKey(),e.getValue())).collect(Collectors.toList())))
	).apply(t, RelishItemRecipe::new));
	public RelishItemRecipe(Ingredient item, String relish) {
		this.item = item;
		this.relish = relish;
	}
	
	public RelishItemRecipe(Ingredient item, String relish, Optional<List<Pair<String, Float>>> variantData) {
		super();
		this.item = item;
		this.relish = relish;
		variantData.ifPresent(o->o.stream().forEach(p->this.variantData.put(p.getFirst(),p.getSecond())));
	}

	/*
	public RelishItemRecipe(ResourceLocation id,FriendlyByteBuf pb) {
		super(id);
		item=Ingredient.fromNetwork(pb);
		relish=pb.readUtf();
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
/*
	public void write(FriendlyByteBuf pb) {
		item.toNetwork(pb);
		pb.writeUtf(relish);
		SUtils.toPacket(pb, variantData);
	}*/
}
