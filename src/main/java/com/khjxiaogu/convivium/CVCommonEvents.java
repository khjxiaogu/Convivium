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

import java.util.Optional;

import com.khjxiaogu.convivium.data.recipes.ContainingRecipe;
import com.khjxiaogu.convivium.data.recipes.RecipeReloadListener;
import com.teammoeg.caupona.api.CauponaApi;
import com.teammoeg.caupona.api.events.ContanerContainFoodEvent;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class CVCommonEvents {
	@SubscribeEvent
	public static void addReloadListeners(AddReloadListenerEvent event) {
		event.addListener(new RecipeReloadListener(event.getServerResources()));
	}

	@SubscribeEvent
	public static void bowlContainerFood(ContanerContainFoodEvent ev) {
		if(ev.origin.getItem()==Items.GLASS_BOTTLE) {
			if(!ev.isBlockAccess) {
				ContainingRecipe recipe=ContainingRecipe.recipes.get(ev.fs.getFluid());
				if(recipe!=null) {
					ev.out=recipe.handle(ev.fs);
					ev.setResult(Result.ALLOW);
				}
			}
		}
	}
	@SuppressWarnings("resource")
	@SubscribeEvent
	public static void onBlockClick(PlayerInteractEvent.RightClickBlock event) {
		ItemStack is = event.getItemStack();
		Player playerIn = event.getEntity();
		if (is.is(Items.POTION)&&playerIn.isShiftKeyDown()) {
			ItemStack replace=new ItemStack(CVItems.POTION.get(),is.getCount());
			is.save(replace.getOrCreateTagElement("potion"));
			playerIn.setItemInHand(event.getHand(), replace);
			ForgeHooks.onPlaceItemIntoWorld(new UseOnContext(playerIn,event.getHand(), event.getHitVec()));
			is.setCount(replace.getCount());
			playerIn.setItemInHand(event.getHand(), is);
		}

	}
}
