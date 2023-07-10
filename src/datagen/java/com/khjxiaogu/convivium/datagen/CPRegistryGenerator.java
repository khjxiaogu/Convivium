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

package com.khjxiaogu.convivium.datagen;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

import com.khjxiaogu.convivium.CVMain;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.registries.ForgeRegistries;

public class CPRegistryGenerator extends DatapackBuiltinEntriesProvider {

	@SuppressWarnings("unchecked")
	public CPRegistryGenerator(PackOutput output, CompletableFuture<Provider> registries) {
		super(output, registries,new RegistrySetBuilder()
				
				.add(Registries.CONFIGURED_FEATURE,(RegistrySetBuilder.RegistryBootstrap)CPRegistryGenerator::bootstrapCFeatures)
				.add(Registries.PLACED_FEATURE,CPRegistryGenerator::bootstrapPFeatures),
				Set.of(CVMain.MODID));
		
	}
	
	
	public static void bootstrapPFeatures(BootstapContext<PlacedFeature> pContext) {
		HolderGetter<ConfiguredFeature<?, ?>> holder=pContext.lookup(Registries.CONFIGURED_FEATURE);

	}
	public static void bootstrapCFeatures(BootstapContext<ConfiguredFeature<?,?>> pContext) {
	}
	public static Block leave(String type) {
		return block(type+"_leaves");
	}
	public static Block sap(String type) {
		return block(type+"_sapling");
	}
	public static Block log(String type) {
		return block(type+"_log");
	}
	public static Block block(String type) {
		return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(CVMain.MODID,type));
	}
	@Override
	public String getName() {
		return "Caupona Registry Generator";
	}
}
