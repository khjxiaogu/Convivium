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


import com.khjxiaogu.convivium.data.recipes.TasteRecipe;
import com.khjxiaogu.convivium.util.Constants;
import com.khjxiaogu.convivium.util.RotationUtils;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
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
