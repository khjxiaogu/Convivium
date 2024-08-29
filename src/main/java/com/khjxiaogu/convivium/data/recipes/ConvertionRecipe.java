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

import com.google.common.collect.ImmutableList;
import com.khjxiaogu.convivium.util.FloatSizedOrCatalystIngredient;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teammoeg.caupona.data.IDataRecipe;
import com.teammoeg.caupona.util.FloatemStack;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ConvertionRecipe extends IDataRecipe {
	public static List<RecipeHolder<ConvertionRecipe>> recipes;
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
	public static final MapCodec<ConvertionRecipe> CODEC=RecordCodecBuilder.mapCodec(t->t.group(
		Codec.list(FloatSizedOrCatalystIngredient.FLAT_CODEC).optionalFieldOf("items",ImmutableList.of()).forGetter(o->o.items),
		Codec.list(FloatemStack.CODEC).optionalFieldOf("outputs",ImmutableList.of()).forGetter(o->o.output),
		FluidStack.OPTIONAL_CODEC.optionalFieldOf("fluidIn",FluidStack.EMPTY).forGetter(o->o.in),
		FluidStack.OPTIONAL_CODEC.optionalFieldOf("fluidOut",FluidStack.EMPTY).forGetter(o->o.out),
		Codec.INT.fieldOf("temperature").forGetter(o->o.temperature),
		Codec.INT.fieldOf("time").forGetter(o->o.processTime),
		Codec.BOOL.fieldOf("consumeAll").forGetter(o->o.consumeExtra)
		).apply(t, ConvertionRecipe::new));
	public List<FloatSizedOrCatalystIngredient> items;
	public List<FloatemStack> output=new ArrayList<>();
	public FluidStack in= FluidStack.EMPTY;
	public FluidStack out= FluidStack.EMPTY;
	public int temperature=0;
	public int processTime=200;
	public boolean consumeExtra;
	

	public ConvertionRecipe(List<FloatSizedOrCatalystIngredient> items, FluidStack in, FluidStack out,
			int temperature, int processTime, boolean consumeExtra) {
		this.items = items;
		this.in = in;
		this.out = out;
		this.temperature = temperature;
		this.processTime = processTime;
		this.consumeExtra = consumeExtra;
	}
	public ConvertionRecipe(List<FloatSizedOrCatalystIngredient> items, List<FloatemStack> output, FluidStack in,FluidStack out, int temperature, int processTime, boolean consumeExtra) {
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
	public ConvertionRecipe(ResourceLocation id, FriendlyByteBuf data) {
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
