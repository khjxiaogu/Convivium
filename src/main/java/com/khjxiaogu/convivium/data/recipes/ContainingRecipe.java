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

import java.util.Map;

import com.google.gson.JsonObject;
import com.teammoeg.caupona.data.IDataRecipe;
import com.teammoeg.caupona.data.InvalidRecipeException;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.nbt.CompoundTag;
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
		is.getOrCreateTag().putString("type", Utils.getRegistryName(f).toString());
		return is;
	}

	public boolean matches(Fluid f) {
		return fluid == f;
	}

	public ItemStack handle(FluidStack stack) {
		ItemStack is = new ItemStack(output);
		if (stack.hasTag())
			is.setTag(stack.getTag());
		is.getOrCreateTag().putString("type", Utils.getRegistryName(stack).toString());
		return is;
	}

	public static FluidStack extractFluid(ItemStack item) {
		if (item.hasTag()) {
			CompoundTag tag = item.getTag();
			if (tag.contains("type")) {
				Fluid f = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(tag.getString("type")));
				if (f != null&&f!=Fluids.EMPTY) {
					FluidStack res = new FluidStack(f, 250);
					CompoundTag ntag = tag.copy();
					ntag.remove("type");
					if (!ntag.isEmpty())
						res.setTag(ntag);
					return res;
				}
			}
		}
		return FluidStack.EMPTY;
	}
	public static Fluid getFluidType(ItemStack item) {
		if (item.hasTag()) {
			CompoundTag tag = item.getTag();
			if (tag.contains("type")) {
				return ForgeRegistries.FLUIDS.getValue(new ResourceLocation(tag.getString("type")));
				
			}
		}
		return Fluids.EMPTY;
	}
	public static Fluid reverseFluidType(Item item) {
		return recipes.values().stream().filter(t->t.output==item).map(t->t.fluid).findFirst().orElse(Fluids.EMPTY);
	}
}
