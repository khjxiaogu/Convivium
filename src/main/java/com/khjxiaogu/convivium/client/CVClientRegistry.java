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

package com.khjxiaogu.convivium.client;

import com.khjxiaogu.convivium.CVBlockEntityTypes;
import com.khjxiaogu.convivium.CVGui;
import com.khjxiaogu.convivium.CVMain;
import com.khjxiaogu.convivium.client.gui.PlatterScreen;
import com.khjxiaogu.convivium.client.renderer.AeolipileRenderer;
import com.khjxiaogu.convivium.client.renderer.CogRenderer;
import com.khjxiaogu.convivium.client.renderer.FruitPlatterRenderer;
import com.teammoeg.caupona.CPMain;
import com.teammoeg.caupona.client.util.DynamicBlockModelReference;
import com.teammoeg.caupona.client.util.ModelUtils;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = CVMain.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CVClientRegistry {
	@SuppressWarnings("unused")
	@SubscribeEvent
	public static void onClientSetupEvent(FMLClientSetupEvent event) {
		MenuScreens.register(CVGui.PLATTER.get(), PlatterScreen::new);
		BlockEntityRenderers.register(CVBlockEntityTypes.COG_CAGE.get(), CogRenderer::new);
		BlockEntityRenderers.register(CVBlockEntityTypes.AOELIPILE.get(), AeolipileRenderer::new);
		BlockEntityRenderers.register(CVBlockEntityTypes.PLATTER.get(),FruitPlatterRenderer::new);
	}
	
	@SubscribeEvent
	public static void onCommonSetup(@SuppressWarnings("unused") FMLClientSetupEvent event) {
		registerFruitModel(Items.APPLE,"apple");
		registerFruitModel(get(CPMain.MODID,"fig"),"fig");
		registerFruitModel(Items.GLISTERING_MELON_SLICE,"glistering_melon");
		registerFruitModel(Items.GLOW_BERRIES,"glow_berries");
		registerFruitModel(Items.ENCHANTED_GOLDEN_APPLE,"golden_apple");
		registerFruitModel(Items.GOLDEN_APPLE,"golden_apple");
		registerFruitModel(Items.MELON_SLICE,"melon");
		registerFruitModel(Items.SWEET_BERRIES,"sweet_berries");
		registerFruitModel(get(CPMain.MODID,"walnut"),"walnut");
		registerFruitModel(get(CPMain.MODID,"wolfberries"),"wolfberries");
	}
	private static void registerFruitModel(Item item,String name) {
		FruitPlatterRenderer.models.put(item,ModelUtils.getModel(CVMain.MODID, name+"_components"));
		DynamicBlockModelReference[] drs=new DynamicBlockModelReference[4];
		for(int i=1;i<=4;i++) {
			drs[i-1]=ModelUtils.getModel(CVMain.MODID, name+"_center_"+i);
		}
		FruitPlatterRenderer.cmodels.put(item, drs);
	}
	private static Item get(String modid,String id) {
		return ForgeRegistries.ITEMS.getValue(new ResourceLocation(modid,id));
	}
	@SubscribeEvent
	public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
	}

	@SubscribeEvent
	public static void onTint(RegisterColorHandlersEvent.Block ev) {
	}

	@SubscribeEvent
	public static void onTint(RegisterColorHandlersEvent.Item ev) {
	}
}