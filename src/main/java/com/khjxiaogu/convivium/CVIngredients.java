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

package com.khjxiaogu.convivium;

import com.khjxiaogu.convivium.util.BeverageFluidIngredient;

import net.neoforged.neoforge.fluids.crafting.FluidIngredientType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class CVIngredients {
	public static final DeferredRegister<FluidIngredientType<?>> FLUID = DeferredRegister.create(NeoForgeRegistries.FLUID_INGREDIENT_TYPES, CVMain.MODID);
	public static final DeferredHolder<FluidIngredientType<?>, FluidIngredientType<BeverageFluidIngredient>> BEVERAGE_FLUID_INGREDIENT=FLUID.register("beverage",()->new FluidIngredientType<>(BeverageFluidIngredient.CODEC));
	public CVIngredients() {
		// TODO Auto-generated constructor stub
	}

}
