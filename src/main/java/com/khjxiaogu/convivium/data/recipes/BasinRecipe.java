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
import net.minecraftforge.registries.RegistryObject;

public class BasinRecipe extends IDataRecipe {
	public static List<BasinRecipe> recipes;
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
	public ResourceLocation base;
	public float density = 0;
	public FluidStack in= FluidStack.EMPTY;
	public List<ItemStack> output;
	public Ingredient item;
	public int inputCount=1;
	public int processTime=200;

	public BasinRecipe(ResourceLocation id, FluidStack in,Ingredient item, List<ItemStack> output, int inputCount, int processTime) {
		super(id);
		this.in = in;
		this.output = output;
		this.item = item;
		this.inputCount = inputCount;
		this.processTime = processTime;
	}
	public BasinRecipe(ResourceLocation id, ResourceLocation base, float density, FluidStack in, 
			Ingredient item,List<ItemStack> output,int inputCount, int processTime) {
		super(id);
		this.base = base;
		this.density = density;
		this.in = in;
		this.output = output;
		this.item = item;
		this.inputCount = inputCount;
		this.processTime = processTime;
	}
	public BasinRecipe(ResourceLocation id, JsonObject jo) {
		super(id);
		if (jo.has("base"))
			base = new ResourceLocation(jo.get("base").getAsString());
		if (jo.has("fluidIn"))
			in=SerializeUtil.readFluidStack(jo.get("fluidIn"));
		else
			throw new InvalidRecipeException("cannot load" + id + ": no input found!");
		if (jo.has("density"))
			density = jo.get("density").getAsFloat();
		if(jo.has("output"))
			output = List.of(Ingredient.fromJson(jo.get("output")).getItems()[0]);
		else if(jo.has("outputs")) {
			output = SerializeUtil.parseJsonElmList(jo.get("outputs"),t->Ingredient.fromJson(t).getItems()[0]);
		}else
			throw new InvalidRecipeException("cannot load" + id + ": no output found!");
		if(jo.has("item"))
			item=Ingredient.fromJson(jo.get("item"));
		if(jo.has("time"))
			processTime=jo.get("time").getAsInt();
		if(jo.has("count"))
			inputCount=jo.get("count").getAsInt();

	}




	public static BasinRecipe testAll(FluidStack f,ItemStack is) {
		return recipes.stream().filter(t -> t.test(f)).filter(t->t.item==null||(t.item.test(is)&&is.getCount()>=t.inputCount)).findFirst().orElse(null);
	}

	public boolean test(FluidStack f) {
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
		return true;
	}

	public List<ItemStack> handle(FluidStack f,ItemStack isi) {
		f.shrink(in.getAmount());
		isi.shrink(inputCount);
		List<ItemStack> fss=new ArrayList<>();
		for(ItemStack is:output)
			fss.add(is.copy());
		return fss;
	}


	public BasinRecipe(ResourceLocation id, FriendlyByteBuf data) {
		super(id);
		base = SerializeUtil.readOptional(data, FriendlyByteBuf::readResourceLocation).orElse(null);
		this.in = SerializeUtil.readFluidStack(data);
		this.item = SerializeUtil.readOptional(data,Ingredient::fromNetwork).orElse(null);
		density = data.readFloat();
		this.inputCount=data.readVarInt();
		output = SerializeUtil.readList(data, t->t.readItem());
		processTime=data.readVarInt();
	}



	public void write(FriendlyByteBuf data) {
		SerializeUtil.writeOptional2(data, base, FriendlyByteBuf::writeResourceLocation);
		SerializeUtil.writeFluidStack(data,in);
		SerializeUtil.writeOptional(data,item,Ingredient::toNetwork);
		data.writeFloat(density);
		data.writeVarInt(inputCount);
		SerializeUtil.writeList(data, output, (t,d)->d.writeItem(t));
		data.writeVarInt(processTime);
	}

	@Override
	public void serializeRecipeData(JsonObject json) {

		
		
		if (base != null)
			json.addProperty("base", base.toString());
		if(!in.isEmpty())
			json.add("fluidIn", SerializeUtil.writeFluidStack(in));
		if(item!=null)
			json.add("item", item.toJson());
		json.addProperty("density", density);
		json.add("outputs",SerializeUtil.toJsonList(output, t->StrictNBTIngredient.of(t).toJson()));
		json.addProperty("time", processTime);
		json.addProperty("count", inputCount);
	}

	public static boolean testInput(ItemStack stack) {
		return recipes.stream().anyMatch(t->t.item.test(stack));
	}

}
