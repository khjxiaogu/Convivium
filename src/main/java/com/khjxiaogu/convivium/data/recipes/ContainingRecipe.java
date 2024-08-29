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

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teammoeg.caupona.CPCapability;
import com.teammoeg.caupona.components.ItemHoldedFluidData;
import com.teammoeg.caupona.data.IDataRecipe;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ContainingRecipe extends IDataRecipe {
	public static Map<Fluid, RecipeHolder<ContainingRecipe>> recipes;
	public static DeferredHolder<RecipeType<?>,RecipeType<Recipe<?>>> TYPE;
	public static DeferredHolder<RecipeSerializer<?>,RecipeSerializer<?>> SERIALIZER;
	public static final MapCodec<ContainingRecipe> CODEC=RecordCodecBuilder.mapCodec(t->t.group(
		BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(o->o.output),
		BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").forGetter(o->o.fluid)
		).apply(t, ContainingRecipe::new));
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
/*

	public ContainingRecipe(ResourceLocation id, FriendlyByteBuf pb) {
		super(id);
		output = pb.readRegistryIdUnsafe(ForgeRegistries.ITEMS);
		fluid = pb.readRegistryIdUnsafe(ForgeRegistries.FLUIDS);
	}*/


	public ContainingRecipe(Item output, Fluid fluid) {
		this.output = output;
		this.fluid = fluid;
	}
/*
	public void write(FriendlyByteBuf pack) {
		pack.writeRegistryIdUnsafe(ForgeRegistries.ITEMS, output);
		pack.writeRegistryIdUnsafe(ForgeRegistries.FLUIDS, fluid);
	}
*/

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
		is.applyComponents(stack.getComponentsPatch());
		is.set(CPCapability.ITEM_FLUID,new ItemHoldedFluidData(stack.getFluid()));
		return is;
	}
	public static Fluid reverseFluidType(Item item) {
		//if(recipes==null)
		//	return Fluids.EMPTY;
		return recipes.values().stream().map(t->t.value()).filter(t->t.output==item).map(t->t.fluid).findFirst().orElse(Fluids.EMPTY);
	}
}
