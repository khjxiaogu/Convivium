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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.khjxiaogu.convivium.data.recipes.relishcondition.RelishCondition;
import com.khjxiaogu.convivium.data.recipes.relishcondition.RelishConditions;
import com.khjxiaogu.convivium.util.BeveragePendingContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teammoeg.caupona.data.IDataRecipe;
import com.teammoeg.caupona.util.FloatemTagStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.registries.DeferredHolder;

public class BeverageTypeRecipe extends IDataRecipe {
	public static List<RecipeHolder<BeverageTypeRecipe>> sorted;
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

	public List<Ingredient> must;
	public List<Ingredient> optional;
	public List<RelishCondition> relish;
	public List<String> allowedRelish;
	public int priority = 0;
	public int time;
	public float density;
	
	public Fluid output;
	public boolean removeNBT=false;
	public static final MapCodec<BeverageTypeRecipe> CODEC=RecordCodecBuilder.mapCodec(t->t.group(
		Codec.list(Ingredient.CODEC).optionalFieldOf("required").forGetter(o->Optional.ofNullable(o.must)),
		Codec.list(Ingredient.CODEC).optionalFieldOf("optional").forGetter(o->Optional.ofNullable(o.optional)),
		Codec.list(RelishConditions.CODEC).optionalFieldOf("relish").forGetter(o->Optional.ofNullable(o.relish)),
		Codec.list(Codec.STRING).optionalFieldOf("allowedRelish").forGetter(o->Optional.ofNullable(o.allowedRelish)),
		Codec.INT.fieldOf("priority").forGetter(o->o.priority),
		Codec.INT.fieldOf("time").forGetter(o->o.time), 
		Codec.FLOAT.fieldOf("density").forGetter(o->o.density),
		BuiltInRegistries.FLUID.byNameCodec().validate(n->n==Fluids.EMPTY?DataResult.error(()->"Output fluid can not be empty"):DataResult.success(n)).fieldOf("output").forGetter(o->o.output),
		Codec.BOOL.fieldOf("removeNBT").forGetter(o->o.removeNBT)
		).apply(t, BeverageTypeRecipe::new));
		
	public BeverageTypeRecipe(Optional<List<Ingredient>> must, Optional<List<Ingredient>> optional, Optional<List<RelishCondition>> relish, Optional<List<String>> allowedRelish, int priority, int time, float density, Fluid output,
		boolean removeNBT) {
		this.must = must.orElse(Arrays.asList());
		this.optional = optional.orElse(Arrays.asList());
		this.relish = relish.orElse(Arrays.asList());
		this.allowedRelish = allowedRelish.orElse(Arrays.asList());
		this.priority = priority;
		this.time = time;
		this.density = density;
		this.output = output;
		this.removeNBT = removeNBT;
	}

	public BeverageTypeRecipe() {
		must=new ArrayList<>();
		optional=new ArrayList<>();
		relish=new ArrayList<>();
		allowedRelish=new ArrayList<>();
		output=Fluids.EMPTY;
	}
/*
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
*/
	public boolean matches(BeveragePendingContext ctx) {
		if (ctx.getTotalItems() < density)
			return false;
		
		if(!relish.isEmpty())
			if(!relish.stream().anyMatch(t->t.test(ctx)))
				return false;
		if(!allowedRelish.isEmpty()) {
			if(ctx.relishes.keySet().stream().anyMatch(t->!allowedRelish.contains(t)))
				return false;
		}
		List<FloatemTagStack> filtered=new ArrayList<>(ctx.getItems());
		if(!must.isEmpty()) {
			for(Ingredient i:must) {
				if(!filtered.removeIf(t->i.test(t.getStack())))
					return false;
			}
		}
		if(!optional.isEmpty())
			for(FloatemTagStack is:filtered) {
				if(!optional.stream().anyMatch(e->e.test(is.getStack())))
					return false;
			}

		return true;
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
