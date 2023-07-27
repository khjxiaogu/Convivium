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

package com.khjxiaogu.convivium;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import com.khjxiaogu.convivium.fluid.BaseFluid;
import com.khjxiaogu.convivium.fluid.BeverageFluid;
import com.khjxiaogu.convivium.util.BeverageInfo;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistries.Keys;
import net.minecraftforge.registries.RegistryObject;

public class CVFluids {
	private static class TextureColorPair {
		ResourceLocation texture;
		int c;

		public TextureColorPair(ResourceLocation t, int c) {
			super();
			this.texture = t;
			this.c = c;
		}
		public FluidType create(String n){
			FluidType ft=new FluidType(FluidType.Properties.create().viscosity(1200)
					.temperature(333).rarity(Rarity.UNCOMMON).descriptionId("item."+CVMain.MODID+"."+n)) {

						@Override
						public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
							consumer.accept(new IClientFluidTypeExtensions() {

								@Override
								public int getTintColor() {
									return c;
								}

								@Override
								public ResourceLocation getStillTexture() {
									return texture;
								}

								@Override
								public ResourceLocation getFlowingTexture() {
									return texture;
								}
								
							});
						}
				
			};

			return ft;
		}
		public FluidType createBVG(String n){
			FluidType ft=new FluidType(FluidType.Properties.create().viscosity(1200)
					.temperature(333).rarity(Rarity.UNCOMMON).descriptionId("item."+CVMain.MODID+"."+n)) {

						@Override
						public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
							consumer.accept(new IClientFluidTypeExtensions() {

								@Override
								public int getTintColor(FluidStack stack) {
									return BeverageInfo.getIColor(stack.getOrCreateTag());
								}

								@Override
								public int getTintColor() {
									return c;
								}

								@Override
								public ResourceLocation getStillTexture() {
									return texture;
								}

								@Override
								public ResourceLocation getFlowingTexture() {
									return texture;
								}
								
							});
						}
				
			};

			return ft;
		}
	}

	private static final ResourceLocation STILL_WATER_TEXTURE = new ResourceLocation("block/water_still");
	private static final ResourceLocation STILL_MILK_TEXTURE = new ResourceLocation("forge", "block/milk_still");
	private static final ResourceLocation STILL_BEVERAGE_TEXTURE = new ResourceLocation(CVMain.MODID, "block/beverage_fluid");
	static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, CVMain.MODID);
	static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(Keys.FLUID_TYPES, CVMain.MODID);
	//private static final Map<String, TextureColorPair> soupfluids = new HashMap<>();
	public static final RegistryObject<FluidType> cocoa=FLUID_TYPES.register("hot_chocolate",()->bvg(0x734e3d).create("hot_chocolate"));
	public static final RegistryObject<FluidType> tea=FLUID_TYPES.register("tea",()->water(0x6c902e).create("tea"));
	public static final RegistryObject<FluidType> b_juice=FLUID_TYPES.register("berry_juice",()->water(0xcc6d57).create("berry_juice"));
	public static final RegistryObject<FluidType> d_juice=FLUID_TYPES.register("drupe_juice",()->water(0xd48e2d).create("drupe_juice"));
	public static final RegistryObject<FluidType> p_juice=FLUID_TYPES.register("pome_juice",()->water(0xe3c25e).create("pome_juice"));
	public static final RegistryObject<FluidType> b_wine=FLUID_TYPES.register("berry_must",()->bvg(0xcc6d57).create("berry_must"));
	public static final RegistryObject<FluidType> d_wine=FLUID_TYPES.register("drupe_must",()->bvg(0xd48e2d).create("drupe_must"));
	public static final RegistryObject<FluidType> p_wine=FLUID_TYPES.register("pome_must",()->bvg(0xe3c25e).create("pome_must"));
	public static final RegistryObject<FluidType> mixed=FLUID_TYPES.register("mixed",()->bvg(0xee99999).createBVG("beverage"));

	public static final RegistryObject<Fluid> cocoaf=FLUIDS.register("hot_chocolate", () -> new BaseFluid(new ForgeFlowingFluid.Properties(cocoa,null,
			null).slopeFindDistance(1).explosionResistance(100F)));
	public static final RegistryObject<Fluid> teaf=FLUIDS.register("tea", () -> new BaseFluid(new ForgeFlowingFluid.Properties(tea,null,
			null).slopeFindDistance(1).explosionResistance(100F)));
	public static final RegistryObject<Fluid> bjuicef=FLUIDS.register("berry_juice", () -> new BaseFluid(new ForgeFlowingFluid.Properties(b_juice,null,
			null).slopeFindDistance(1).explosionResistance(100F)));
	public static final RegistryObject<Fluid> djuicef=FLUIDS.register("drupe_juice", () -> new BaseFluid(new ForgeFlowingFluid.Properties(d_juice,null,
			null).slopeFindDistance(1).explosionResistance(100F)));
	public static final RegistryObject<Fluid> pjuicef=FLUIDS.register("pome_juice", () -> new BaseFluid(new ForgeFlowingFluid.Properties(p_juice,null,
			null).slopeFindDistance(1).explosionResistance(100F)));
	public static final RegistryObject<Fluid> bwinef=FLUIDS.register("berry_must", () -> new BaseFluid(new ForgeFlowingFluid.Properties(b_wine,null,
			null).slopeFindDistance(1).explosionResistance(100F)));
	public static final RegistryObject<Fluid> dwinef=FLUIDS.register("drupe_must", () -> new BaseFluid(new ForgeFlowingFluid.Properties(d_wine,null,
			null).slopeFindDistance(1).explosionResistance(100F)));
	public static final RegistryObject<Fluid> pwinef=FLUIDS.register("pome_must", () -> new BaseFluid(new ForgeFlowingFluid.Properties(p_wine,null,
			null).slopeFindDistance(1).explosionResistance(100F)));
	public static final RegistryObject<Fluid> mixedf=FLUIDS.register("mixed", () -> new BeverageFluid(new ForgeFlowingFluid.Properties(mixed,null,
			null).slopeFindDistance(1).explosionResistance(100F)));
	public static Map<String,TextureColorPair> intern=new HashMap<>();
	static {
		intern.put("mulled_wine",bvg(0xac3543));
		intern.put("jaegertee",bvg(0xc34534));
		intern.put("posca",bvg(0xe7bc71));
		intern.put("leicha",bvg(0x90a82f));
		intern.put("te_mocha",bvg(0xddab85));
		intern.put("kahwa_tea",bvg(0xd77032));
		intern.put("saidi_tea",bvg(0x6d221f));
		intern.put("milk_tea",bvg(0xd8b285));
		intern.put("sweet_tea",bvg(0xbe6336));
		intern.put("fruit_tongsui",bvg(0xefe18b));
		intern.put("ade",bvg(0xf0e3ac));
		intern.put("punch",bvg(0xe89f56));
		intern.put("syllabub",bvg(0xf5e6a4));
		intern.put("posset",bvg(0xfdf7c3));
		intern.put("chocolate_tea",bvg(0xd3af96));
		intern.put("cocoa_wine",bvg(0xca9787));
		intern.put("chocolate_milk",bvg(0xc69f8f));
		for(Entry<String, TextureColorPair> ent:intern.entrySet()) {
			RegistryObject<FluidType> type=FLUID_TYPES.register(ent.getKey(),()->ent.getValue().create(ent.getKey()));
			FLUIDS.register(ent.getKey(), () -> new BeverageFluid(new ForgeFlowingFluid.Properties(type,null,
					null).slopeFindDistance(1).explosionResistance(100F)));
		}
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
