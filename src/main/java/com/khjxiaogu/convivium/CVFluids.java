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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

import com.khjxiaogu.convivium.fluid.BaseFluid;
import com.khjxiaogu.convivium.fluid.BeverageFluid;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries.Keys;

public class CVFluids {
	public static final Map<Supplier<? extends Fluid>,TextureColorPair> FLUID_MODELS=new HashMap<>();
	public static record TextureColorPair (Identifier texture, int c) {
	}

	public static FluidType create(String n) {
		
		FluidType ft = new FluidType(FluidType.Properties.create().viscosity(1200)
			.temperature(333).rarity(Rarity.UNCOMMON).descriptionId("item." + CVMain.MODID + "." + n));
		
		return ft;
	}
	public static final String[] SORBETS=new String[] {
		"cocoa_juice",
		"cocoa_milk",
		"cocoa_tea",
		"cocoa_wine",
		"cocoa",
		"fallback",
		"juice_milk",
		"juice_tea",
		"juice_wine",
		"juice",
		"milk_tea",
		"milk_wine",
		"milk",
		"tea_wine",
		"tea",
		"wine"
	};
	private static final Identifier STILL_WATER_TEXTURE = Identifier.withDefaultNamespace("block/water_still");
	private static final Identifier STILL_MILK_TEXTURE = Identifier.fromNamespaceAndPath("neoforge", "block/milk_still");
	private static final Identifier STILL_BEVERAGE_TEXTURE = Identifier.fromNamespaceAndPath(CVMain.MODID, "block/beverage_fluid");
	static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(BuiltInRegistries.FLUID, CVMain.MODID);
	static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(Keys.FLUID_TYPES, CVMain.MODID);

	// private static final Map<String, TextureColorPair> soupfluids = new
	// HashMap<>();
	public static final DeferredHolder<FluidType, FluidType> COCOA = FLUID_TYPES.register("hot_chocolate", () -> create("hot_chocolate"));
	public static final DeferredHolder<FluidType, FluidType> TEA = FLUID_TYPES.register("tea", () -> create("tea"));
	public static final DeferredHolder<FluidType, FluidType> BERRY_JUICE = FLUID_TYPES.register("berry_juice", () -> create("berry_juice"));
	public static final DeferredHolder<FluidType, FluidType> DRUPE_JUICE = FLUID_TYPES.register("drupe_juice", () -> create("drupe_juice"));
	public static final DeferredHolder<FluidType, FluidType> POME_JUICE = FLUID_TYPES.register("pome_juice", () -> create("pome_juice"));
	public static final DeferredHolder<FluidType, FluidType> BERRY_WINE = FLUID_TYPES.register("berry_must", () -> create("berry_must"));
	public static final DeferredHolder<FluidType, FluidType> DRUPE_WINE = FLUID_TYPES.register("drupe_must", () -> create("drupe_must"));
	public static final DeferredHolder<FluidType, FluidType> POME_MUST = FLUID_TYPES.register("pome_must", () -> create("pome_must"));
	public static final DeferredHolder<FluidType, FluidType> MIXED = FLUID_TYPES.register("mixed", () -> create("beverage"));

	public static final DeferredHolder<Fluid, BaseFluid> COCOA_FLUID = register("hot_chocolate", () -> new BaseFluid(new BaseFlowingFluid.Properties(COCOA, null,
		null).slopeFindDistance(1).explosionResistance(100F)),bvg(0xff734e3d));
	public static final DeferredHolder<Fluid, BaseFluid> TEA_FLUID = register("tea", () -> new BaseFluid(new BaseFlowingFluid.Properties(TEA, null,
		null).slopeFindDistance(1).explosionResistance(100F)),water(0xff6c902e));
	public static final DeferredHolder<Fluid, BaseFluid> BERRY_JUICE_FLUID = register("berry_juice", () -> new BaseFluid(new BaseFlowingFluid.Properties(BERRY_JUICE, null,
		null).slopeFindDistance(1).explosionResistance(100F)),water(0xffcc6d57));
	public static final DeferredHolder<Fluid, BaseFluid> DRUPE_JUICE_FLUID = register("drupe_juice", () -> new BaseFluid(new BaseFlowingFluid.Properties(DRUPE_JUICE, null,
		null).slopeFindDistance(1).explosionResistance(100F)),water(0xffd48e2d));
	public static final DeferredHolder<Fluid, BaseFluid> POME_JUICE_FLUID = register("pome_juice", () -> new BaseFluid(new BaseFlowingFluid.Properties(POME_JUICE, null,
		null).slopeFindDistance(1).explosionResistance(100F)),water(0xffe3c25e));
	public static final DeferredHolder<Fluid, BaseFluid> BERRY_WINE_FLUID = register("berry_must", () -> new BaseFluid(new BaseFlowingFluid.Properties(BERRY_WINE, null,
		null).slopeFindDistance(1).explosionResistance(100F)),bvg(0xffcc6d57));
	public static final DeferredHolder<Fluid, BaseFluid> DRUPE_WINE_FLUID = register("drupe_must", () -> new BaseFluid(new BaseFlowingFluid.Properties(DRUPE_WINE, null,
		null).slopeFindDistance(1).explosionResistance(100F)),bvg(0xffd48e2d));
	public static final DeferredHolder<Fluid, BaseFluid> POME_WINE_FLUID = register("pome_must", () -> new BaseFluid(new BaseFlowingFluid.Properties(POME_MUST, null,
		null).slopeFindDistance(1).explosionResistance(100F)),bvg(0xffe3c25e));
	public static final DeferredHolder<Fluid, BeverageFluid> MIXED_FLUID = FLUIDS.register("mixed", () -> new BeverageFluid(new BaseFlowingFluid.Properties(MIXED, null,
		null).slopeFindDistance(1).explosionResistance(100F)));
	public static final Map<String, TextureColorPair> SPECIAL_FLUIDS = new HashMap<>();
	static {
		SPECIAL_FLUIDS.put("mulled_wine", bvg(0xffac3543));
		SPECIAL_FLUIDS.put("jaegertee", bvg(0xffc34534));
		SPECIAL_FLUIDS.put("posca", bvg(0xeff7bc71));
		SPECIAL_FLUIDS.put("leicha", bvg(0xff90a82f));
		SPECIAL_FLUIDS.put("te_mocha", bvg(0xffddab85));
		SPECIAL_FLUIDS.put("kahwa_tea", bvg(0xffd77032));
		SPECIAL_FLUIDS.put("saidi_tea", bvg(0xff6d221f));
		SPECIAL_FLUIDS.put("milk_tea", bvg(0xffd8b285));
		SPECIAL_FLUIDS.put("sweet_tea", bvg(0xffbe6336));
		SPECIAL_FLUIDS.put("fruit_tongsui", bvg(0xffefe18b));
		SPECIAL_FLUIDS.put("ade", bvg(0xfff0e3ac));
		SPECIAL_FLUIDS.put("punch", bvg(0xffe89f56));
		SPECIAL_FLUIDS.put("syllabub", bvg(0xfff5e6a4));
		SPECIAL_FLUIDS.put("posset", bvg(0xfffdf7c3));
		SPECIAL_FLUIDS.put("chocolate_tea", bvg(0xffd3af96));
		SPECIAL_FLUIDS.put("cocoa_wine", bvg(0xffca9787));
		SPECIAL_FLUIDS.put("chocolate_milk", bvg(0xffc69f8f));
		for (Entry<String, TextureColorPair> ent : SPECIAL_FLUIDS.entrySet()) {
			DeferredHolder<FluidType, FluidType> type = FLUID_TYPES.register(ent.getKey(), () -> create(ent.getKey()));
			FLUID_MODELS.put(
			FLUIDS.register(ent.getKey(), () -> new BeverageFluid(new BaseFlowingFluid.Properties(type, null,
				null).slopeFindDistance(1).explosionResistance(100F)))
			, ent.getValue());
		}
		for(String s:SORBETS) {
			TextureColorPair tcp=new TextureColorPair(CVMain.rl("block/sorbets/"+s), 0xffffffff);
			DeferredHolder<FluidType, FluidType> type=FLUID_TYPES.register(s+"_sorbet",t -> create(t.getPath()));
			FLUID_MODELS.put(
			FLUIDS.register(s+"_sorbet", () -> new BeverageFluid(new BaseFlowingFluid.Properties(type, null,null).slopeFindDistance(1).explosionResistance(100F)))
			, tcp);
		}
	}
    public static <I extends Fluid> DeferredHolder<Fluid, I> register(final String name, final Supplier<? extends I> sup,TextureColorPair tcp) {
       DeferredHolder<Fluid, I> data=FLUIDS.register(name,sup);
       FLUID_MODELS.put(data,tcp);
       return data;
    }
	public static TextureColorPair water(int c) {
		return new TextureColorPair(STILL_WATER_TEXTURE, c);
	}

	public static TextureColorPair milk(int c) {
		return new TextureColorPair(STILL_MILK_TEXTURE, c);
	}

	public static TextureColorPair bvg(int c) {
		return new TextureColorPair(STILL_BEVERAGE_TEXTURE, c);
	}

	static {
	}
}
