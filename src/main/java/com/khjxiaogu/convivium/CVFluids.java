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

import java.util.function.Consumer;

import com.teammoeg.caupona.generated.CPStewTexture;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistries.Keys;

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
			ResourceLocation rt=CPStewTexture.texture.getOrDefault(n, texture);
			int cx=CPStewTexture.texture.containsKey(n)?0xffffffff:c;
			FluidType ft=new FluidType(FluidType.Properties.create().viscosity(1200)
					.temperature(333).rarity(Rarity.UNCOMMON).descriptionId("item."+CVMain.MODID+"."+n)) {

						@Override
						public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
							consumer.accept(new IClientFluidTypeExtensions() {

								@Override
								public int getTintColor() {
									return cx;
								}

								@Override
								public ResourceLocation getStillTexture() {
									return rt;
								}

								@Override
								public ResourceLocation getFlowingTexture() {
									return rt;
								}
								
							});
						}
				
			};

			return ft;
		}
	}

	private static final ResourceLocation STILL_WATER_TEXTURE = new ResourceLocation("block/water_still");
	private static final ResourceLocation STILL_MILK_TEXTURE = new ResourceLocation("forge", "block/milk_still");
	static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, CVMain.MODID);
	static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(Keys.FLUID_TYPES, CVMain.MODID);
	//private static final Map<String, TextureColorPair> soupfluids = new HashMap<>();

	public static TextureColorPair water(int c) {
		return new TextureColorPair(STILL_WATER_TEXTURE, c);
	}

	public static TextureColorPair milk(int c) {
		return new TextureColorPair(STILL_MILK_TEXTURE, c);
	}
	static {
	}
}
