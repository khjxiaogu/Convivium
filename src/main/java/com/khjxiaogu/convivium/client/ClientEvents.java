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


import com.khjxiaogu.convivium.client.renderer.FruitModel;
import com.khjxiaogu.convivium.client.renderer.FruitPlatterRenderer;
import com.khjxiaogu.convivium.data.recipes.TasteRecipe;
import com.khjxiaogu.convivium.util.Constants;
import com.khjxiaogu.convivium.util.RotationUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.teammoeg.caupona.CPMain;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.TextureTransform;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterRenderBuffersEvent;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

@EventBusSubscriber(Dist.CLIENT)
public class ClientEvents {
	/**
	 * @param load  
	 */
	@SubscribeEvent
	public static void JoinWorld(LevelEvent.Load load)
	{
		RotationUtils.resetTimer();
	}

	@SubscribeEvent
	public static void tick(LevelTickEvent.Pre tick)
	{
		if(!Minecraft.getInstance().isPaused())
			RotationUtils.tick();
	}

	@SubscribeEvent(priority=EventPriority.LOWEST)
	public static void registerModels(ModelEvent.RegisterStandalone ev){
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

	private static Item get(String modid, String id) {
		return BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(modid, id));
	}

	public static void registerFruitModelGlint(Item item, String name, FruitModel.ModelType type) {
		FruitPlatterRenderer.models.put(item, new FruitModel(name, type, RenderTypes.cutoutMovingBlock(),true));
	}

	public static void registerFruitModel(Item item, String name, FruitModel.ModelType type) {
		FruitPlatterRenderer.models.put(item, new FruitModel(name, type, RenderTypes.cutoutMovingBlock(), false));
	}
	@SubscribeEvent
	public static void addTooltip(ItemTooltipEvent ev)
	{
		if(ev.getEntity()!=null)
		for(RecipeHolder<TasteRecipe> ti:TasteRecipe.recipes) {
			if(ti.value().item.test(ev.getItemStack())) {
				for(int i=0;i<Constants.TASTES.length;i++) {
					String sway=Constants.TASTES[i];
					float sn=ti.value().variantData.getOrDefault(sway, 0f);
					if(sn==0)continue;
					String key="taste.convivium."+sway;
					if(sn<0)
						key+=".negate";
					sn=Mth.abs(sn);
					ev.getToolTip().add(Utils.translate(key,Component.translatable("enchantment.level." + Mth.ceil(sn))).withStyle(Style.EMPTY.withColor(Constants.COLOR_OF_TASTES[i])));
				}
			}
		}
	}
}
