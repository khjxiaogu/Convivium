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

import java.util.List;
import java.util.stream.Stream;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.khjxiaogu.convivium.data.recipes.relishcondition.RelishCondition;
import com.khjxiaogu.convivium.data.recipes.relishcondition.RelishConditions;
import com.khjxiaogu.convivium.util.BeveragePendingContext;
import com.teammoeg.caupona.data.IDataRecipe;
import com.teammoeg.caupona.data.InvalidRecipeException;
import com.teammoeg.caupona.data.SerializeUtil;
import com.teammoeg.caupona.util.FloatemTagStack;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BeverageTypeRecipe extends IDataRecipe {
	public static List<BeverageTypeRecipe> sorted;
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

	List<Ingredient> must;
	List<Ingredient> optional;
	List<RelishCondition> relish;
	List<String> allowedRelish;
	int priority = 0;
	public int time;
	float density;
	
	public Fluid output;
	public boolean removeNBT=false;
	public BeverageTypeRecipe(ResourceLocation id) {
		super(id);
	}

	public BeverageTypeRecipe(ResourceLocation id, JsonObject data) {
		super(id);
		if (data.has("required")) 
			must = SerializeUtil.parseJsonList(data.get("required"), Ingredient::fromJson);
		if (data.has("optional")) 
			optional = SerializeUtil.parseJsonList(data.get("optional"), Ingredient::fromJson);
		if(data.has("relish"))
			relish = SerializeUtil.parseJsonList(data.get("relish"), RelishConditions::of);
		if(data.has("allowedRelish"))
			allowedRelish = SerializeUtil.parseJsonElmList(data.get("allowedRelish"), JsonElement::getAsString);
		if (data.has("priority"))
			priority = data.get("priority").getAsInt();
		time = data.get("time").getAsInt();
		if (data.has("density"))
			density = data.get("density").getAsFloat();
		output = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(data.get("output").getAsString()));
		if (output == Fluids.EMPTY)
			throw new InvalidRecipeException();
		if(data.has("removeNBT"))
			removeNBT=data.get("removeNBT").getAsBoolean();
	}

	public BeverageTypeRecipe(ResourceLocation id, FriendlyByteBuf data) {
		super(id);
		must = SerializeUtil.readList(data,Ingredient::fromNetwork);
		optional = SerializeUtil.readList(data, Ingredient::fromNetwork);
		relish= SerializeUtil.readList(data, RelishConditions::of);
		allowedRelish = SerializeUtil.readList(data,FriendlyByteBuf::readUtf);
		priority = data.readVarInt();
		time = data.readVarInt();
		density = data.readFloat();
		output = data.readRegistryIdUnsafe(ForgeRegistries.FLUIDS);
		removeNBT=data.readBoolean();
	}



	public BeverageTypeRecipe(ResourceLocation id, List<Ingredient> must, List<Ingredient> optional,
			List<RelishCondition> relish, List<String> allowedRelish, int priority, int time, float density,
			Fluid output, boolean removeNBT) {
		super(id);
		this.must = must;
		this.optional = optional;
		this.relish = relish;
		this.allowedRelish = allowedRelish;
		this.priority = priority;
		this.time = time;
		this.density = density;
		this.output = output;
		this.removeNBT = removeNBT;
	}

	public void write(FriendlyByteBuf data) {
		SerializeUtil.writeList(data, must, Ingredient::toNetwork);
		SerializeUtil.writeList(data, optional, Ingredient::toNetwork);
		SerializeUtil.writeList(data, relish, RelishConditions::write);
		SerializeUtil.writeList2(data, allowedRelish, FriendlyByteBuf::writeUtf);
		data.writeVarInt(priority);
		data.writeVarInt(time);
		data.writeFloat(density);
		data.writeRegistryIdUnsafe(ForgeRegistries.FLUIDS, output);
		data.writeBoolean(removeNBT);
	}

	public boolean matches(BeveragePendingContext ctx) {
		if (ctx.getTotalItems() < density)
			return false;
		
		if(relish!=null)
			if(!relish.stream().anyMatch(t->t.test(ctx)))
				return false;
		if(allowedRelish!=null&&!allowedRelish.isEmpty()) {
			if(ctx.relishes.keySet().stream().anyMatch(t->!allowedRelish.contains(t)))
				return false;
		}
		if(must!=null&&!must.isEmpty()) {
			if(!must.stream().allMatch(t->ctx.getItems().stream().map(e->e.getStack()).anyMatch(t)))
				return false;
		}
		if(optional!=null&&!optional.isEmpty())
			for(FloatemTagStack is:ctx.getItems()) {
				if(must!=null) {
					if(must.stream().anyMatch(e->e.test(is.getStack())))
						continue;
				}
				if(!optional.stream().anyMatch(e->e.test(is.getStack())))
					return false;
			}

		return true;
	}

	@Override
	public void serializeRecipeData(JsonObject json) {
		if (must != null && !must.isEmpty()) {
			json.add("required", SerializeUtil.toJsonList(must, Ingredient::toJson));
		}
		if (optional != null && !optional.isEmpty()) {
			json.add("optional", SerializeUtil.toJsonList(optional, Ingredient::toJson));
		}
		if(relish!=null&&!relish.isEmpty()) {
			json.add("relish", SerializeUtil.toJsonList(relish, RelishCondition::serialize));
		}
		if(allowedRelish!=null&&!allowedRelish.isEmpty()) {
			json.add("allowedRelish", SerializeUtil.toJsonList(allowedRelish,e->new JsonPrimitive(e)));
		}
		if (priority != 0)
			json.addProperty("priority", priority);
		json.addProperty("density", density);
		json.addProperty("time", time);
		json.addProperty("output",Utils.getRegistryName(output).toString());
		if(removeNBT)
			json.addProperty("removeNBT",removeNBT);
	}

	public Stream<Ingredient> getAllIngredients() {
		return Stream.concat(
				must == null ? Stream.empty() : must.stream(),
				optional == null ? Stream.empty() : optional.stream());
	}


	public int getPriority() {
		return priority;
	}


	public float getDensity() {
		return density;
	}

}
