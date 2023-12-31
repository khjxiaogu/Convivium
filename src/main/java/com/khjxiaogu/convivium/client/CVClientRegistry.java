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
import com.khjxiaogu.convivium.CVBlocks;
import com.khjxiaogu.convivium.CVGui;
import com.khjxiaogu.convivium.CVMain;
import com.khjxiaogu.convivium.client.gui.BasinScreen;
import com.khjxiaogu.convivium.client.gui.BeverageVendingScreen;
import com.khjxiaogu.convivium.client.gui.PamScreen;
import com.khjxiaogu.convivium.client.gui.PlatterScreen;
import com.khjxiaogu.convivium.client.gui.WhiskScreen;
import com.khjxiaogu.convivium.client.renderer.AeolipileRenderer;
import com.khjxiaogu.convivium.client.renderer.AqueductMainRenderer;
import com.khjxiaogu.convivium.client.renderer.AqueductRenderer;
import com.khjxiaogu.convivium.client.renderer.BasinRenderer;
import com.khjxiaogu.convivium.client.renderer.BeverageRenderer;
import com.khjxiaogu.convivium.client.renderer.CogRenderer;
import com.khjxiaogu.convivium.client.renderer.FruitModel;
import com.khjxiaogu.convivium.client.renderer.FruitPlatterRenderer;
import com.khjxiaogu.convivium.client.renderer.PamRenderer;
import com.khjxiaogu.convivium.client.renderer.VendingRenderer;
import com.khjxiaogu.convivium.client.renderer.WhiskRenderer;
import com.khjxiaogu.convivium.client.util.CVRenderType;
import com.khjxiaogu.convivium.util.BeverageInfo;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.teammoeg.caupona.CPMain;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.ItemRenderer;
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
		MenuScreens.register(CVGui.PAM.get(), PamScreen::new);
		MenuScreens.register(CVGui.WHISK.get(), WhiskScreen::new);
		MenuScreens.register(CVGui.VENDING.get(), BeverageVendingScreen::new);
		MenuScreens.register(CVGui.BASIN.get(), BasinScreen::new);
		BlockEntityRenderers.register(CVBlockEntityTypes.COG_CAGE.get(), CogRenderer::new);
		BlockEntityRenderers.register(CVBlockEntityTypes.AOELIPILE.get(), AeolipileRenderer::new);
		BlockEntityRenderers.register(CVBlockEntityTypes.PLATTER.get(),FruitPlatterRenderer::new);
		BlockEntityRenderers.register(CVBlockEntityTypes.WHISK.get(),WhiskRenderer::new);
		BlockEntityRenderers.register(CVBlockEntityTypes.PAM.get(),PamRenderer::new);
		BlockEntityRenderers.register(CVBlockEntityTypes.AQUEDUCT.get(), AqueductRenderer::new);
		BlockEntityRenderers.register(CVBlockEntityTypes.AQUEDUCT_MAIN.get(), AqueductMainRenderer::new);
		BlockEntityRenderers.register(CVBlockEntityTypes.BEVERAGE.get(), BeverageRenderer::new);
		BlockEntityRenderers.register(CVBlockEntityTypes.BEVERAGE_VENDING_MACHINE.get(), VendingRenderer::new);
		BlockEntityRenderers.register(CVBlockEntityTypes.BASIN.get(), BasinRenderer::new);
	}
	
	@SubscribeEvent
	public static void onCommonSetup(@SuppressWarnings("unused") FMLClientSetupEvent event) {
		registerFruitModel(Items.APPLE,"apple",FruitModel.ModelType.ROUND);
		registerFruitModel(get(CPMain.MODID,"fig"),"fig",FruitModel.ModelType.ROUND);
		registerFruitModel(Items.GLISTERING_MELON_SLICE,"glistering_melon",FruitModel.ModelType.SLICE);
		registerFruitModel(Items.GLOW_BERRIES,"glow_berries",FruitModel.ModelType.MISC);
		registerFruitModel(Items.ENCHANTED_GOLDEN_APPLE,"golden_apple",FruitModel.ModelType.ROUND,RenderType.glint(),RenderType.cutout());
		registerFruitModel(Items.GOLDEN_APPLE,"golden_apple",FruitModel.ModelType.ROUND);
		registerFruitModel(Items.MELON_SLICE,"melon",FruitModel.ModelType.SLICE);
		registerFruitModel(Items.SWEET_BERRIES,"sweet_berries",FruitModel.ModelType.MISC);
		registerFruitModel(get(CPMain.MODID,"walnut"),"walnut",FruitModel.ModelType.ROUND);
		registerFruitModel(get(CPMain.MODID,"wolfberries"),"wolfberries",FruitModel.ModelType.MISC);
		
	}
	public static void registerFruitModel(Item item,String name,FruitModel.ModelType type,RenderType rt1,RenderType rt2) {
		FruitPlatterRenderer.models.put(item,new FruitModel(name,type,rt1,rt2));
	}
	public static void registerFruitModel(Item item,String name,FruitModel.ModelType type) {
		FruitPlatterRenderer.models.put(item,new FruitModel(name,type));
	}
	private static Item get(String modid,String id) {
		return ForgeRegistries.ITEMS.getValue(new ResourceLocation(modid,id));
	}
	@SubscribeEvent
	public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
		event.registerSpriteSet(CVParticles.SPLASH.get(), SplashParticle.Provider::new);
	}


	@SubscribeEvent
	public static void onTint(RegisterColorHandlersEvent.Item ev) {
		ev.register((a, idx) -> {
			//System.out.println(idx);
			return idx==0?-1: BeverageInfo.getIColor(Utils.extractData(a));
			
	      },CVBlocks.BEVERAGE.get());
	}
}