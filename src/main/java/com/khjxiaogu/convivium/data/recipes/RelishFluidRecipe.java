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
import com.teammoeg.caupona.util.SerializeUtil;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.registries.DeferredHolder;

public class RelishFluidRecipe extends IDataRecipe {
	public Fluid fluid;
	public String relish;
	public Map<String,Float> variantData=new HashMap<>();
	public static DeferredHolder<RecipeSerializer<?>,RecipeSerializer<RelishFluidRecipe>> SERIALIZER;
	public static DeferredHolder<RecipeType<?>,RecipeType<RelishFluidRecipe>> TYPE;
	public static Map<Fluid, RecipeHolder<RelishFluidRecipe>> recipes;
	public static final MapCodec<RelishFluidRecipe> CODEC=RecordCodecBuilder.mapCodec(t->t.group(
			BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").forGetter(o->o.fluid),
			Codec.STRING.fieldOf("relish").forGetter(o->o.relish),
			Codec.compoundList(Codec.STRING, Codec.FLOAT).optionalFieldOf("variants").forGetter(o->Optional.of(o.variantData.entrySet().stream().map(e->Pair.of(e.getKey(),e.getValue())).collect(Collectors.toList())))
		).apply(t, RelishFluidRecipe::new));
	public static final StreamCodec<RegistryFriendlyByteBuf,RelishFluidRecipe> STREAM_CODEC=StreamCodec.composite(
		ByteBufCodecs.registry(Registries.FLUID),o->o.fluid,
		ByteBufCodecs.STRING_UTF8,o->o.relish,
		SerializeUtil.pair(ByteBufCodecs.STRING_UTF8, ByteBufCodecs.FLOAT).apply(ByteBufCodecs.list()), o->o.variantData.entrySet().stream().map(e->Pair.of(e.getKey(),e.getValue())).collect(Collectors.toList()),
	RelishFluidRecipe::new);
	public RelishFluidRecipe(Fluid fluid, String relish) {
		this.fluid = fluid;
		this.relish = relish;
	}
	public RelishFluidRecipe(Fluid fluid, String relish,List<Pair<String, Float>> variantData) {
		super();
		this.fluid = fluid;
		this.relish = relish;
		variantData.stream().forEach(p->this.variantData.put(p.getFirst(),p.getSecond()));
	}
	public RelishFluidRecipe(Fluid fluid, String relish,Optional<List<Pair<String, Float>>> variantData) {
		super();
		this.fluid = fluid;
		this.relish = relish;
		variantData.ifPresent(o->o.stream().forEach(p->this.variantData.put(p.getFirst(),p.getSecond())));
	}
/*	public RelishFluidRecipe(Identifier id,FriendlyByteBuf pb) {
		super(id);
		fluid=pb.readRegistryIdUnsafe(ForgeRegistries.FLUIDS);
		relish=pb.readUtf();
		variantData=SUtils.fromPacket(pb);
	}
*/
	@Override
	public RecipeSerializer<RelishFluidRecipe> getSerializer() {
		return SERIALIZER.get();
	}
	@Override
	public RecipeType<RelishFluidRecipe> getType() {
		return TYPE.get();
	}

/*
	public void write(FriendlyByteBuf pb) {
		pb.writeRegistryIdUnsafe(ForgeRegistries.FLUIDS, fluid);
		pb.writeUtf(relish);
		SUtils.toPacket(pb, variantData);
	}*/
}
