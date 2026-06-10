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

package com.khjxiaogu.convivium.datagen;

import java.util.concurrent.CompletableFuture;

import com.khjxiaogu.convivium.CVMain;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.server.packs.PackType;
import net.minecraft.util.Util;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = CVMain.MODID)
public class CVDataGenerator {
	@SubscribeEvent
	public static void gatherData(GatherDataEvent.Server event) {
		System.out.println("Gather server data");
		DataGenerator gen = event.getGenerator();

		
		CompletableFuture<HolderLookup.Provider> completablefuture = CompletableFuture.supplyAsync(VanillaRegistries::createLookup, Util.backgroundExecutor());
		gen.addProvider(true,new CVItemTagGenerator(gen, CVMain.MODID,event.getLookupProvider()));
		gen.addProvider(true,new CVBlockTagGenerator(gen, CVMain.MODID,event.getLookupProvider()));
		gen.addProvider(true,new CVFluidTagGenerator(gen, CVMain.MODID,event.getLookupProvider()));
		gen.addProvider(true,new CVLootGenerator(gen,completablefuture));
		gen.addProvider(true,new CVRegistryGenerator(gen.getPackOutput(),completablefuture));
		gen.addProvider(true, new CVRecipeProvider.Runner(gen.getPackOutput(),event.getLookupProvider()));
		
	}
	@SubscribeEvent
	public static void gatherData(GatherDataEvent.Client event) {
		System.out.println("Gather client data");
		DataGenerator gen = event.getGenerator();
		@SuppressWarnings("unused")
		CompletableFuture<HolderLookup.Provider> completablefuture = CompletableFuture.supplyAsync(VanillaRegistries::createLookup, Util.backgroundExecutor());
		gen.addProvider(true,new CVModelProvider(gen.getPackOutput(), CVMain.MODID,event.getResourceManager(PackType.CLIENT_RESOURCES)));
		gen.addProvider(true,new CVBookGenerator(gen.getPackOutput(), event.getResourceManager(PackType.CLIENT_RESOURCES)));
	}
}
