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
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.khjxiaogu.convivium.util.FloatSizedOrCatalystIngredient;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teammoeg.caupona.data.IDataRecipe;
import com.teammoeg.caupona.util.FloatemStack;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ConvertionRecipe extends IDataRecipe {
	public static List<RecipeHolder<ConvertionRecipe>> recipes;
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
		Codec.list(FloatSizedOrCatalystIngredient.NESTED_CODEC).optionalFieldOf("items",ImmutableList.of()).forGetter(o->o.items),
		Codec.list(FloatemStack.CODEC).optionalFieldOf("outputs",ImmutableList.of()).forGetter(o->o.output),
		SizedFluidIngredient.CODEC.fieldOf("fluidIn").forGetter(o->o.in),
		FluidStackTemplate.CODEC.fieldOf("fluidOut").forGetter(o->o.out),
		Codec.INT.fieldOf("temperature").forGetter(o->o.temperature),
		Codec.INT.fieldOf("time").forGetter(o->o.processTime),
		Codec.BOOL.fieldOf("consumeAll").forGetter(o->o.consumeExtra)
		).apply(t, ConvertionRecipe::new));
	public static final StreamCodec<RegistryFriendlyByteBuf,ConvertionRecipe> STREAM_CODEC=StreamCodec.composite(
		FloatSizedOrCatalystIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()),o->o.items,
		FloatemStack.STREAM_CODEC.apply(ByteBufCodecs.list()),o->o.output,
		SizedFluidIngredient.STREAM_CODEC,o->o.in,
		FluidStackTemplate.STREAM_CODEC,o->o.out,
		ByteBufCodecs.VAR_INT,o->o.temperature,
		ByteBufCodecs.VAR_INT,o->o.processTime,
		ByteBufCodecs.BOOL,o->o.consumeExtra,
		ConvertionRecipe::new);
	public List<FloatSizedOrCatalystIngredient> items;
	public List<FloatemStack> output=new ArrayList<>();
	public SizedFluidIngredient in;
	public FluidStackTemplate out;
	public int temperature=0;
	public int processTime=200;
	public boolean consumeExtra;
	

	public ConvertionRecipe(List<FloatSizedOrCatalystIngredient> items, SizedFluidIngredient in, FluidStackTemplate out,
			int temperature, int processTime, boolean consumeExtra) {
		this.items = items;
		this.in = in;
		this.out = out;
		this.temperature = temperature;
		this.processTime = processTime;
		this.consumeExtra = consumeExtra;
	}
	public ConvertionRecipe(List<FloatSizedOrCatalystIngredient> items, List<FloatemStack> output, SizedFluidIngredient in,FluidStackTemplate out, int temperature, int processTime, boolean consumeExtra) {
		super();
		this.items = items;
		this.output = output;
		this.in = in;
		this.out = out;
		this.temperature = temperature;
		this.processTime = processTime;
		this.consumeExtra = consumeExtra;
	}
/*
	public ConvertionRecipe(Identifier id, FriendlyByteBuf data) {
		super(id);
		
		items = SerializeUtil.readList(data, d -> Pair.of(Ingredient.fromNetwork(d), d.readFloat()));
		output=SerializeUtil.readList(data, d -> Pair.of(d.readItem(), d.readFloat()));
		this.in = data.readRegistryIdUnsafe(ForgeRegistries.FLUIDS);
		this.out = data.readRegistryIdUnsafe(ForgeRegistries.FLUIDS);
		inpart=data.readVarInt();
		outpart=data.readVarInt();
		processTime=data.readVarInt();
		temperature=data.readVarInt();
		consumeExtra=data.readBoolean();
	}
	public void write(FriendlyByteBuf data) {
		SerializeUtil.writeList(data, items, (r, d) -> {
			r.getFirst().toNetwork(data);
			data.writeFloat(r.getSecond());
		});
		SerializeUtil.writeList(data, output, (r, d) -> {
			d.writeItem(r.getFirst());
			data.writeFloat(r.getSecond());
		});
		data.writeRegistryIdUnsafe(ForgeRegistries.FLUIDS,in);
		data.writeRegistryIdUnsafe(ForgeRegistries.FLUIDS,out);
		data.writeVarInt(inpart);
		data.writeVarInt(outpart);
		data.writeVarInt(processTime);
		data.writeVarInt(temperature);
		data.writeBoolean(consumeExtra);
	}
*/


}
