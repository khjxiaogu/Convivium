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

package com.khjxiaogu.convivium.client;

import java.util.Map.Entry;
import java.util.function.Supplier;

import com.khjxiaogu.convivium.CVBlockEntityTypes;
import com.khjxiaogu.convivium.CVBlocks;
import com.khjxiaogu.convivium.CVComponents;
import com.khjxiaogu.convivium.CVEntityTypes;
import com.khjxiaogu.convivium.CVFluids;
import com.khjxiaogu.convivium.CVFluids.TextureColorPair;
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
import com.khjxiaogu.convivium.client.renderer.WolfFountainProjectileRenderer;
import com.khjxiaogu.convivium.client.renderer.WolfFountainRenderer;
import com.khjxiaogu.convivium.util.BeverageInfo;
import com.teammoeg.caupona.CPMain;

import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterFluidModelsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.fluid.FluidTintSource;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;

@EventBusSubscriber(value = Dist.CLIENT, modid = CVMain.MODID)
public class CVClientRegistry {

	@SubscribeEvent
	public static void registerParticleFactories(RegisterMenuScreensEvent event) {
		event.register(CVGui.PLATTER.get(), PlatterScreen::new);
		event.register(CVGui.PAM.get(), PamScreen::new);
		event.register(CVGui.WHISK.get(), WhiskScreen::new);
		event.register(CVGui.VENDING.get(), BeverageVendingScreen::new);
		event.register(CVGui.BASIN.get(), BasinScreen::new);
		
	}

	@SuppressWarnings("unused")
	@SubscribeEvent
	public static void onClientSetupEvent(FMLClientSetupEvent event) {

		BlockEntityRenderers.register(CVBlockEntityTypes.COG_CAGE.get(), CogRenderer::new);
		BlockEntityRenderers.register(CVBlockEntityTypes.AOELIPILE.get(), AeolipileRenderer::new);
		BlockEntityRenderers.register(CVBlockEntityTypes.PLATTER.get(), FruitPlatterRenderer::new);
		BlockEntityRenderers.register(CVBlockEntityTypes.WHISK.get(), WhiskRenderer::new);
		BlockEntityRenderers.register(CVBlockEntityTypes.PAM.get(), PamRenderer::new);
		BlockEntityRenderers.register(CVBlockEntityTypes.AQUEDUCT.get(), AqueductRenderer::new);
		BlockEntityRenderers.register(CVBlockEntityTypes.AQUEDUCT_MAIN.get(), AqueductMainRenderer::new);
		BlockEntityRenderers.register(CVBlockEntityTypes.BEVERAGE.get(), BeverageRenderer::new);
		BlockEntityRenderers.register(CVBlockEntityTypes.BEVERAGE_VENDING_MACHINE.get(), VendingRenderer::new);
		BlockEntityRenderers.register(CVBlockEntityTypes.BASIN.get(), BasinRenderer::new);
		BlockEntityRenderers.register(CVBlockEntityTypes.WOLF_FOUNTAIN.get(), WolfFountainRenderer::new);
		EntityRenderers.register(CVEntityTypes.WOLF_FOUNTAIN_DROP.get(),WolfFountainProjectileRenderer::new);
		
	}
	private static final Identifier STILL_BEVERAGE_TEXTURE = Identifier.fromNamespaceAndPath(CVMain.MODID, "block/beverage_fluid");

	@SubscribeEvent
	public static void onRegisterFluidModels(RegisterFluidModelsEvent event) {
		event.register(new FluidModel.Unbaked(
			new Material(STILL_BEVERAGE_TEXTURE),
            new Material(STILL_BEVERAGE_TEXTURE),
            null,
            new FluidTintSource() {

				@Override
				public int color(FluidState state) {
					return 0xffee9999;
				}
				@Override
				public int colorAsStack(FluidStack stack) {
					BeverageInfo cmp=stack.get(CVComponents.BEVERAGE_INFO);
					if(cmp==null)
						return 0xffee9999;
					return cmp.getIColor();
			    }
			},
            null
            ),CVFluids.mixedf.get());


		for(Entry<Supplier<Fluid>, TextureColorPair> s:CVFluids.clientExtensiondata.entrySet()) {
			TextureColorPair tcp=s.getValue();
			event.register(new FluidModel.Unbaked(
				new Material(tcp.texture()),
	            new Material(tcp.texture()),
	            null,
	            new FluidTintSource() {

					@Override
					public int color(FluidState state) {
						return tcp.c();
					}
				},
	            null
	            ),s.getKey());
		}
	}

	@SubscribeEvent
	public static void onCommonSetup(@SuppressWarnings("unused") FMLClientSetupEvent event) {
		registerFruitModel(Items.APPLE, "apple", FruitModel.ModelType.ROUND);
		registerFruitModel(get(CPMain.MODID, "fig"), "fig", FruitModel.ModelType.ROUND);
		registerFruitModel(Items.GLISTERING_MELON_SLICE, "glistering_melon", FruitModel.ModelType.SLICE);
		registerFruitModel(Items.GLOW_BERRIES, "glow_berries", FruitModel.ModelType.MISC);
		registerFruitModelGlint(Items.ENCHANTED_GOLDEN_APPLE, "golden_apple", FruitModel.ModelType.ROUND);
		registerFruitModel(Items.GOLDEN_APPLE, "golden_apple", FruitModel.ModelType.ROUND);
		registerFruitModel(Items.MELON_SLICE, "melon", FruitModel.ModelType.SLICE);
		registerFruitModel(Items.SWEET_BERRIES, "sweet_berries", FruitModel.ModelType.MISC);
		registerFruitModel(get(CPMain.MODID, "walnut"), "walnut", FruitModel.ModelType.ROUND);
		registerFruitModel(get(CPMain.MODID, "wolfberries"), "wolfberries", FruitModel.ModelType.MISC);

	}


	public static void registerFruitModelGlint(Item item, String name, FruitModel.ModelType type) {
		FruitPlatterRenderer.models.put(item, new FruitModel(name, type, RenderTypes.glint()));
	}

	public static void registerFruitModel(Item item, String name, FruitModel.ModelType type) {
		FruitPlatterRenderer.models.put(item, new FruitModel(name, type, RenderTypes.translucentMovingBlock()));
	}

	private static Item get(String modid, String id) {
		return BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(modid, id));
	}

	@SubscribeEvent
	public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
		event.registerSpriteSet(CVParticles.FLOW.get(), FlowParticle.Provider::new);
		event.registerSpriteSet(CVParticles.SPLASH.get(), FountainSplashParticle.Provider::new);
	}

	@SubscribeEvent
	public static void registerItemTint(RegisterColorHandlersEvent.ItemTintSources ev) {
		ev.register(CVMain.rl("beverage"), BeverageTint.MAP_CODEC);
		
	}
}