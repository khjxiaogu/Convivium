/*
 * Copyright (c) 2022 TeamMoeg
 *
 * This file is part of Caupona.
 *
 * Caupona is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Caupona is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * Specially, we allow this software to be used alongside with closed source software Minecraft(R) and Forge or other modloader.
 * Any mods or plugins can also use apis provided by forge or com.teammoeg.caupona.api without using GPL or open source.
 *
 * You should have received a copy of the GNU General Public License
 * along with Caupona. If not, see <https://www.gnu.org/licenses/>.
 */

package com.khjxiaogu.convivium.data.recipes;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.teammoeg.caupona.data.IDataRecipe;
import com.teammoeg.caupona.data.SerializeUtil;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.crafting.StrictNBTIngredient;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ConvertionRecipe extends IDataRecipe {
	public static List<ConvertionRecipe> recipes;
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

	public List<Pair<Ingredient, Float>> items;
	public List<Pair<ItemStack, Float>> output=new ArrayList<>();
	public Fluid in= Fluids.EMPTY;
	public Fluid out= Fluids.EMPTY;
	public int temperature=0;
	public int processTime=200;
	public int inpart=1;
	public int outpart=1;
	public boolean consumeExtra;


	public ConvertionRecipe(ResourceLocation id, List<Pair<Ingredient, Float>> items, Fluid in, Fluid out,
			int temperature, int processTime, boolean consumeExtra) {
		super(id);
		this.items = items;
		this.in = in;
		this.out = out;
		this.temperature = temperature;
		this.processTime = processTime;
		this.consumeExtra = consumeExtra;
	}

	public ConvertionRecipe(ResourceLocation id, JsonObject jo) {
		super(id);
		if (jo.has("items"))
			items = SerializeUtil.parseJsonList(jo.get("items"),
					j -> Pair.of(Ingredient.fromJson(j.get("item")), (j.has("count") ? j.get("count").getAsFloat() : 1)));
		if (jo.has("fluidIn")) {
			in=ForgeRegistries.FLUIDS.getValue(new ResourceLocation(GsonHelper.getAsString(jo, "fluidIn")));
			inpart=GsonHelper.getAsInt(jo, "inNum",1);
		}
		if (jo.has("fluidOut")) {
			out=ForgeRegistries.FLUIDS.getValue(new ResourceLocation(GsonHelper.getAsString(jo, "fluidOut")));
			outpart=GsonHelper.getAsInt(jo, "outNum",1);
		}
		if(jo.has("outputs")) 
			output = SerializeUtil.parseJsonList(jo.get("outputs"),t->Pair.of(Ingredient.fromJson(t.get("item")).getItems()[0],t.has("count")?t.get("count").getAsFloat():1));
		
		
		
		if(jo.has("time"))
			processTime=jo.get("time").getAsInt();
		if(jo.has("temperature"))
			temperature=jo.get("temperature").getAsInt();
		consumeExtra=GsonHelper.getAsBoolean(jo,"consumeAll",false);
	}

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
		in=data.readRegistryIdUnsafe(ForgeRegistries.FLUIDS);
		out=data.readRegistryIdUnsafe(ForgeRegistries.FLUIDS);
		data.writeVarInt(inpart);
		data.writeVarInt(outpart);
		data.writeVarInt(processTime);
		data.writeVarInt(temperature);
		data.writeBoolean(consumeExtra);
	}

	@Override
	public void serializeRecipeData(JsonObject json) {
		if(items.size()>0)
		json.add("items", SerializeUtil.toJsonList(items, (r) -> {
			JsonObject jo = new JsonObject();
			jo.add("item", r.getFirst().toJson());
			jo.addProperty("count", r.getSecond());
			return jo;
		}));
		if(output.size()>0)
		json.add("outputs", SerializeUtil.toJsonList(output, (r) -> {
			JsonObject jo = new JsonObject();
			jo.add("item", StrictNBTIngredient.of(r.getFirst()).toJson());
			jo.addProperty("count", r.getSecond());
			return jo;
		}));
		if(in!=Fluids.EMPTY)
			json.addProperty("fluidIn", Utils.getRegistryName(in).toString());
		if(out!=Fluids.EMPTY)
			json.addProperty("fluidOut", Utils.getRegistryName(out).toString());
		json.addProperty("inNum", inpart);
		json.addProperty("outNum", outpart);
		json.addProperty("time", processTime);
		json.addProperty("temperature", temperature);
		json.addProperty("consumeAll", consumeExtra);
	}

}
