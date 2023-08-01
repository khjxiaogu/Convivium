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
	public FluidStack out= FluidStack.EMPTY;
	public List<ItemStack> output;
	public int processTime=200;
	public boolean keepInfo = false;
	
	





	public BasinRecipe(ResourceLocation id, ResourceLocation base,
			float density, FluidStack in, FluidStack out, List<ItemStack> output, int processTime, boolean keepInfo) {
		super(id);
		this.base = base;
		this.density = density;
		this.in = in;
		this.out = out;
		this.output = output;
		this.processTime = processTime;
		this.keepInfo = keepInfo;
	}

	public BasinRecipe(ResourceLocation id, ResourceLocation base,
			float density, List<ItemStack> output, int processTime,boolean keepInfo) {
		super(id);
		this.base = base;
		this.density = density;
		this.output = output;
		this.processTime = processTime;
		this.keepInfo = keepInfo;
	}

	public BasinRecipe(ResourceLocation id, JsonObject jo) {
		super(id);
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
		else if(jo.has("outputs")) {
			output = SerializeUtil.parseJsonElmList(jo.get("outputs"),t->Ingredient.fromJson(t).getItems()[0]);
		}else
			throw new InvalidRecipeException("cannot load" + id + ": no output found!");
		if(jo.has("time"))
			processTime=jo.get("time").getAsInt();

	}




	public static BasinRecipe testAll(FluidStack f) {

		return recipes.stream().filter(t -> t.test(f)).findFirst().orElse(null);
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

	public List<ItemStack> handle(FluidStack f) {
		
		if (keepInfo) {
			StewInfo info = SoupFluid.getInfo(f);
			SoupFluid.setInfo(out, info);
		}
		f.shrink(in.getAmount());
		List<ItemStack> fss=new ArrayList<>();
		for(ItemStack is:output)
			fss.add(is.copy());
		return fss;
	}


	public BasinRecipe(ResourceLocation id, FriendlyByteBuf data) {
		super(id);
		base = SerializeUtil.readOptional(data, FriendlyByteBuf::readResourceLocation).orElse(null);
		this.in = SerializeUtil.readFluidStack(data);
		this.out = SerializeUtil.readFluidStack(data);
		density = data.readFloat();
		keepInfo = data.readBoolean();
		output = SerializeUtil.readList(data, t->t.readItem());
		processTime=data.readVarInt();
	}



	public void write(FriendlyByteBuf data) {
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
