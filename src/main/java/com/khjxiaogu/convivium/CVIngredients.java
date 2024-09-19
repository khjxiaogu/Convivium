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
