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
import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teammoeg.caupona.api.CauponaHooks;
import com.teammoeg.caupona.components.IFoodInfo;
import com.teammoeg.caupona.data.IDataRecipe;
import com.teammoeg.caupona.data.recipes.TimedRecipe;
import com.teammoeg.caupona.util.SizedOrCatalystFluidIngredient;
import com.teammoeg.caupona.util.SizedOrCatalystIngredient;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;

public class GrindingRecipe extends IDataRecipe implements TimedRecipe{
	public static Map<Identifier,RecipeHolder<GrindingRecipe>> recipes;
	public static DeferredHolder<RecipeType<?>,RecipeType<GrindingRecipe>> TYPE;
	public static DeferredHolder<RecipeSerializer<?>,RecipeSerializer<GrindingRecipe>> SERIALIZER;

	@Override
	public RecipeSerializer<GrindingRecipe> getSerializer() {
		return SERIALIZER.get();
	}

	@Override
	public RecipeType<GrindingRecipe> getType() {
		return TYPE.get();
	}

	public List<SizedOrCatalystIngredient> items;
	public Fluid base;
	public float density = 0;
	public SizedOrCatalystFluidIngredient in;
	public Optional<FluidStackTemplate> out;
	public List<ItemStackTemplate> output;
	public int processTime=200;
	public boolean keepInfo = false;
	public GrindingRecipe(List<SizedOrCatalystIngredient> items, Fluid base, float density, SizedOrCatalystFluidIngredient in, Optional<FluidStackTemplate> out, List<ItemStackTemplate> output, int processTime,
		boolean keepInfo) {
		super();
		this.items = items;
		this.base = base;
		this.density = density;
		this.in = in;
		this.out = out;
		this.output = output;
		this.processTime = processTime;
		this.keepInfo = keepInfo;
	}

	public static final MapCodec<GrindingRecipe> CODEC=RecordCodecBuilder.mapCodec(t->t.group(
		Codec.list(SizedOrCatalystIngredient.NESTED_CODEC).fieldOf("items").forGetter(o->o.items),
		BuiltInRegistries.FLUID.byNameCodec().optionalFieldOf("base").forGetter(o->Optional.ofNullable(o.base)),
		Codec.FLOAT.fieldOf("density").forGetter(o->o.density),
		SizedOrCatalystFluidIngredient.NESTED_CODEC.optionalFieldOf("fluidIn").forGetter(o->Optional.ofNullable(o.in)),
		FluidStackTemplate.CODEC.optionalFieldOf("fluidOut").forGetter(o->o.out),
		Codec.list(ItemStackTemplate.CODEC).fieldOf("outputs").forGetter(o->o.output),
		Codec.INT.fieldOf("time").forGetter(o->o.processTime),
		Codec.BOOL.fieldOf("keepInfo").forGetter(o->o.keepInfo)
		).apply(t, GrindingRecipe::new));
	public static final StreamCodec<RegistryFriendlyByteBuf,GrindingRecipe> STREAM_CODEC=StreamCodec.composite(
		SizedOrCatalystIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()),o->o.items,
		ByteBufCodecs.optional(ByteBufCodecs.registry(Registries.FLUID)),o->Optional.ofNullable(o.base),
		ByteBufCodecs.FLOAT,o->o.density,
		ByteBufCodecs.optional(SizedOrCatalystFluidIngredient.STREAM_CODEC),o->Optional.ofNullable(o.in),
		ByteBufCodecs.optional(FluidStackTemplate.STREAM_CODEC),o->o.out,
		ItemStackTemplate.STREAM_CODEC.apply(ByteBufCodecs.list()),o->o.output,
		ByteBufCodecs.INT,o->o.processTime,
		ByteBufCodecs.BOOL,o->o.keepInfo,
		GrindingRecipe::new);
	public GrindingRecipe(List<SizedOrCatalystIngredient> items, Optional<Fluid> base,
			float density, Optional<SizedOrCatalystFluidIngredient> in, Optional<FluidStackTemplate> out, List<ItemStackTemplate> output, int processTime, boolean keepInfo) {
		this.items = items;
		this.base = base.orElse(null);
		this.density = density;
		this.in = in.orElse(null);
		this.out = out;
		this.output = output;
		this.processTime = processTime;
		this.keepInfo = keepInfo;
	}

	public GrindingRecipe(List<SizedOrCatalystIngredient> items, Fluid base,
			float density, List<ItemStackTemplate> output, int processTime,boolean keepInfo) {
		this.items = items;
		this.base = base;
		this.density = density;
		this.output = output;
		this.processTime = processTime;
		this.keepInfo = keepInfo;
		this.out=Optional.empty();
	}
/*
	public GrindingRecipe(Identifier id, JsonObject jo) {
		super(id);
		if (jo.has("items"))
			items = SerializeUtil.parseJsonList(jo.get("items"),
					j -> Pair.of(Ingredient.fromJson(j.get("item")), (j.has("count") ? j.get("count").getAsInt() : 1)));

		if (jo.has("base"))
			base = new Identifier(jo.get("base").getAsString());
		if (jo.has("fluidIn"))
			in=SerializeUtil.readFluidStack(jo.get("fluidIn"));
		if (jo.has("fluidOut"))
			out=SerializeUtil.readFluidStack(jo.get("fluidOut"));
		if (jo.has("density"))
			density = jo.get("density").getAsFloat();
		if (jo.has("keepInfo"))
			keepInfo = jo.get("keepInfo").getAsBoolean();
		if(jo.has("output"))
			output = List.of(Ingredient.fromJson(jo.get("output")).getItems()[0]);
		else if(jo.has("outputs")) 
			output = SerializeUtil.parseJsonElmList(jo.get("outputs"),t->Ingredient.fromJson(t).getItems()[0]);
		if(output==null&&out.isEmpty())
			throw new InvalidRecipeException("cannot load" + id + ": no output found!");
		if(jo.has("time"))
			processTime=jo.get("time").getAsInt();

	}

*/

	public static boolean testInput(ItemStack stack) {
		return recipes.values().stream().anyMatch(t -> t.value().items.stream().anyMatch(i -> i.test(stack)));
	}

	public static RecipeHolder<GrindingRecipe> test(ResourceHandler<FluidResource> f, ResourceHandler<ItemResource> inv) {
		ItemStack is0 = inv.getResource(0).toStack(inv.getAmountAsInt(0));
		ItemStack is1 = inv.getResource(1).toStack(inv.getAmountAsInt(1));
		ItemStack is2 = inv.getResource(2).toStack(inv.getAmountAsInt(2));
		FluidStack fs=f.getResource(0).toStack(f.getAmountAsInt(0));
		return recipes.values().stream().filter(t -> t.value().test(fs, is0, is1, is2)).findFirst().orElse(null);
	}

	public boolean test(FluidStack f, ItemStack... ss) {
		if (items.size() > 0) {
			if (ss.length < items.size())
				return false;
			int notEmpty = 0;
			for (ItemStack is : ss)
				if (!is.isEmpty())
					notEmpty++;
			if (notEmpty < items.size())
				return false;
		}
		if(in!=null) {
			if(!in.test(f))
				return false;
		}

		if (density != 0 || base != null) {
			IFoodInfo info = CauponaHooks.getInfo(f).orElse(null);
			if(info==null)
				return false;
			if (base != null && info.getBase()!=base)
				return false;
			if (info.getDensity() < density)
				return false;
		}
		for (SizedOrCatalystIngredient igd : items) {
			boolean flag = false;
			for (ItemStack is : ss) {
				if (igd.test(is)) {
					flag = true;
					break;
				}
			}
			if (!flag)
				return false;
		}
		return true;
	}



/*
	public GrindingRecipe(Identifier id, FriendlyByteBuf data) {
		super(id);
		
		items = SerializeUtil.readList(data, d -> Pair.of(Ingredient.fromNetwork(d), d.readVarInt()));
		base = SerializeUtil.readOptional(data, FriendlyByteBuf::readIdentifier).orElse(null);
		this.in = SerializeUtil.readFluidStack(data);
		this.out = SerializeUtil.readFluidStack(data);
		density = data.readFloat();
		keepInfo = data.readBoolean();
		output = SerializeUtil.readList(data, t->t.readItem());
		processTime=data.readVarInt();
	}



	public void write(FriendlyByteBuf data) {
		SerializeUtil.writeList(data, items, (r, d) -> {
			r.getFirst().toNetwork(data);
			data.writeVarInt(r.getSecond());
		});
		SerializeUtil.writeOptional2(data, base, FriendlyByteBuf::writeIdentifier);
		SerializeUtil.writeFluidStack(data,in);
		SerializeUtil.writeFluidStack(data,out);
		data.writeFloat(density);
		data.writeBoolean(keepInfo);
		SerializeUtil.writeList(data, output, (t,d)->d.writeItem(t));
		data.writeVarInt(processTime);
	}
*/

	@Override
	public int getTime() {
		return processTime;
	}
}
