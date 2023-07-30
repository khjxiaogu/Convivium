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

import java.util.Map;

import com.google.gson.JsonObject;
import com.teammoeg.caupona.data.IDataRecipe;
import com.teammoeg.caupona.data.InvalidRecipeException;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ContainingRecipe extends IDataRecipe {
	public static Map<Fluid,ContainingRecipe> recipes;
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

	public Item output;
	public Fluid fluid;

	public ContainingRecipe(ResourceLocation id, JsonObject jo) {
		super(id);
		output = ForgeRegistries.ITEMS.getValue(new ResourceLocation(jo.get("item").getAsString()));
		fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(jo.get("fluid").getAsString()));
		if (output == null || output == Items.AIR || fluid == null || fluid == Fluids.EMPTY)
			throw new InvalidRecipeException();
	}

	public ContainingRecipe(ResourceLocation id, FriendlyByteBuf pb) {
		super(id);
		output = pb.readRegistryIdUnsafe(ForgeRegistries.ITEMS);
		fluid = pb.readRegistryIdUnsafe(ForgeRegistries.FLUIDS);
	}


	public ContainingRecipe(ResourceLocation id, Item output, Fluid fluid) {
		super(id);
		this.output = output;
		this.fluid = fluid;
	}

	public void write(FriendlyByteBuf pack) {
		pack.writeRegistryIdUnsafe(ForgeRegistries.ITEMS, output);
		pack.writeRegistryIdUnsafe(ForgeRegistries.FLUIDS, fluid);
	}

	public void serializeRecipeData(JsonObject jo) {
		jo.addProperty("item", Utils.getRegistryName(output).toString());
		jo.addProperty("fluid", Utils.getRegistryName(fluid).toString());
	}

	public ItemStack handle(Fluid f) {
		ItemStack is = new ItemStack(output);
		Utils.writeItemFluid(is, f);
		return is;
	}

	public boolean matches(Fluid f) {
		return fluid == f;
	}

	public ItemStack handle(FluidStack stack) {
		ItemStack is = new ItemStack(output);
		Utils.writeItemFluid(is, stack);
		return is;
	}
	public static Fluid reverseFluidType(Item item) {
		return recipes.values().stream().filter(t->t.output==item).map(t->t.fluid).findFirst().orElse(Fluids.EMPTY);
	}
}
