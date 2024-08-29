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

import com.khjxiaogu.convivium.CVBlocks;
import com.khjxiaogu.convivium.CVMain;
import com.khjxiaogu.convivium.CVWorldGen;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

public class CVRegistryGenerator extends DatapackBuiltinEntriesProvider {

	public CVRegistryGenerator(PackOutput output, CompletableFuture<Provider> registries) {
		super(output, registries, new RegistrySetBuilder()

			.add(Registries.CONFIGURED_FEATURE, CVRegistryGenerator::bootstrapCFeatures)
			.add(Registries.PLACED_FEATURE, CVRegistryGenerator::bootstrapPFeatures),
			Set.of(CVMain.MODID));

	}

	public static void bootstrapPFeatures(BootstrapContext<PlacedFeature> pContext) {
		HolderGetter<ConfiguredFeature<?, ?>> holder = pContext.lookup(Registries.CONFIGURED_FEATURE);
		PlacementUtils.register(pContext, CVWorldGen.PATCH_CAMELLIA, holder.getOrThrow(CVWorldGen.CAMELLIA),
			RarityFilter.onAverageOnceEvery(10), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
	}

	public static void bootstrapCFeatures(BootstrapContext<ConfiguredFeature<?, ?>> pContext) {
		FeatureUtils.register(pContext, CVWorldGen.CAMELLIA, Feature.RANDOM_PATCH,
			new RandomPatchConfiguration(12, 4, 3,
				PlacementUtils.filtered(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(CVBlocks.CAMELLIA.get())), BlockPredicate.ONLY_IN_AIR_PREDICATE)));

	}

	public static Block leave(String type) {
		return block(type + "_leaves");
	}

	public static Block sap(String type) {
		return block(type + "_sapling");
	}

	public static Block log(String type) {
		return block(type + "_log");
	}

	public static Block block(String type) {
		return BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(CVMain.MODID, type));
	}

	@Override
	public String getName() {
		return CVMain.MODNAME + " Registry Generator";
	}
}
