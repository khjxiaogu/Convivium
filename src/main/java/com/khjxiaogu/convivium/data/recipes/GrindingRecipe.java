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

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.teammoeg.caupona.data.IDataRecipe;
import com.teammoeg.caupona.data.InvalidRecipeException;
import com.teammoeg.caupona.data.SerializeUtil;
import com.teammoeg.caupona.fluid.SoupFluid;
import com.teammoeg.caupona.util.StewInfo;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.crafting.StrictNBTIngredient;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.RegistryObject;

public class GrindingRecipe extends IDataRecipe {
	public static List<GrindingRecipe> recipes;
	public static RegistryObject<RecipeType<Recipe<?>>> TYPE;
	public static RegistryObject<RecipeSerializer<?>> SERIALIZER;

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER.get();
	}

	@Override
	public RecipeType<?> getType() {
		return TYPE.get();
	}

	public List<Pair<Ingredient, Integer>> items;
	public ResourceLocation base;
	public float density = 0;
	public FluidStack in= FluidStack.EMPTY;
	public FluidStack out= FluidStack.EMPTY;
	public List<ItemStack> output;
	public int processTime=200;
	public boolean keepInfo = false;
	
	





	public GrindingRecipe(ResourceLocation id, List<Pair<Ingredient, Integer>> items, ResourceLocation base,
			float density, FluidStack in, FluidStack out, List<ItemStack> output, int processTime, boolean keepInfo) {
		super(id);
		this.items = items;
		this.base = base;
		this.density = density;
		this.in = in;
		this.out = out;
		this.output = output;
		this.processTime = processTime;
		this.keepInfo = keepInfo;
	}

	public GrindingRecipe(ResourceLocation id, List<Pair<Ingredient, Integer>> items, ResourceLocation base,
			float density, List<ItemStack> output, int processTime,boolean keepInfo) {
		super(id);
		this.items = items;
		this.base = base;
		this.density = density;
		this.output = output;
		this.processTime = processTime;
		this.keepInfo = keepInfo;
	}

	public GrindingRecipe(ResourceLocation id, JsonObject jo) {
		super(id);
		if (jo.has("items"))
			items = SerializeUtil.parseJsonList(jo.get("items"),
					j -> Pair.of(Ingredient.fromJson(j.get("item")), (j.has("count") ? j.get("count").getAsInt() : 1)));

		if (jo.has("base"))
			base = new ResourceLocation(jo.get("base").getAsString());
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



	public static boolean testInput(ItemStack stack) {
		return recipes.stream().anyMatch(t -> t.items.stream().anyMatch(i -> i.getFirst().test(stack)));
	}

	public static GrindingRecipe test(FluidStack f, ItemStackHandler inv) {
		ItemStack is0 = inv.getStackInSlot(0);
		ItemStack is1 = inv.getStackInSlot(1);
		ItemStack is2 = inv.getStackInSlot(2);
		return recipes.stream().filter(t -> t.test(f, is0, is1, is2)).findFirst().orElse(null);
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
		if (in.getFluid().isSame(Fluids.EMPTY) && f.isEmpty()) {
		} else if (!f.getFluid().isSame(in.getFluid()))
			return false;
		if (in.getAmount() > 0 && f.getAmount() < in.getAmount())
			return false;

		if (density != 0 || base != null) {
			StewInfo info = SoupFluid.getInfo(f);
			if (base != null && !info.base.equals(base))
				return false;
			if (info.getDensity() < density)
				return false;
		}
		for (Pair<Ingredient, Integer> igd : items) {
			boolean flag = false;
			for (ItemStack is : ss) {
				if (igd.getFirst().test(is) && is.getCount() >= igd.getSecond()) {
					flag = true;
					break;
				}
			}
			if (!flag)
				return false;
		}
		return true;
	}

	private List<ItemStack> handle(FluidStack f) {
		
		if (keepInfo) {
			StewInfo info = SoupFluid.getInfo(f);
			SoupFluid.setInfo(out, info);
		}
		if(!f.isEmpty())
			f.shrink(in.getAmount());
		List<ItemStack> fss=new ArrayList<>();
		for(ItemStack is:output)
			fss.add(is.copy());
		return fss;
	}

	public List<ItemStack> handle(FluidStack f, ItemStackHandler inv) {
		for (Pair<Ingredient, Integer> igd : items) {
			if (igd.getSecond() == 0)
				continue;
			for (int i = 0; i < 3; i++) {
				ItemStack is = inv.getStackInSlot(i);
				if (igd.getFirst().test(is)) {
					is.shrink(igd.getSecond());
					break;
				}
			}
		}
		return handle(f);
	}

	public GrindingRecipe(ResourceLocation id, FriendlyByteBuf data) {
		super(id);
		
		items = SerializeUtil.readList(data, d -> Pair.of(Ingredient.fromNetwork(d), d.readVarInt()));
		base = SerializeUtil.readOptional(data, FriendlyByteBuf::readResourceLocation).orElse(null);
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
		SerializeUtil.writeOptional2(data, base, FriendlyByteBuf::writeResourceLocation);
		SerializeUtil.writeFluidStack(data,in);
		SerializeUtil.writeFluidStack(data,out);
		data.writeFloat(density);
		data.writeBoolean(keepInfo);
		SerializeUtil.writeList(data, output, (t,d)->d.writeItem(t));
		data.writeVarInt(processTime);
	}

	@Override
	public void serializeRecipeData(JsonObject json) {
		json.add("items", SerializeUtil.toJsonList(items, (r) -> {
			JsonObject jo = new JsonObject();
			jo.add("item", r.getFirst().toJson());
			jo.addProperty("count", r.getSecond());
			return jo;
		}));
		
		
		if (base != null)
			json.addProperty("base", base.toString());
		if(!in.isEmpty())
			json.add("fluidIn", SerializeUtil.writeFluidStack(in));
		if(!out.isEmpty())
			json.add("fluidOut", SerializeUtil.writeFluidStack(out));

		json.addProperty("density", density);
		json.addProperty("keepInfo", keepInfo);
		json.add("outputs",SerializeUtil.toJsonList(output, t->StrictNBTIngredient.of(t).toJson()));
		json.addProperty("time", processTime);
	}

}
