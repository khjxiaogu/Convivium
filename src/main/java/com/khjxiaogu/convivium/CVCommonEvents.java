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

package com.khjxiaogu.convivium;

import com.khjxiaogu.convivium.data.recipes.RecipeReloadListener;
import com.khjxiaogu.convivium.util.PotionItemInfo;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber
public class CVCommonEvents {

	@SubscribeEvent
	public static void addReloadListeners(AddReloadListenerEvent event) {
		event.addListener(new RecipeReloadListener(event.getServerResources()));
	}

	/*@SubscribeEvent
	public static void bowlContainerFood(ContanerContainFoodEvent ev) {
		if (ev.origin.getItem() == Items.GLASS_BOTTLE) {
			if (!ev.isBlockAccess) {
				RecipeHolder<ContainingRecipe> recipe = ContainingRecipe.recipes.get(ev.fs.getFluid());
				if (recipe != null) {
					ev.out = recipe.value().handle(ev.fs);
					ev.setResult(EventResult.ALLOW);
				}
			}
		}
	}*/

	/*@SubscribeEvent
	public static void isExtractAllowed(FoodExchangeItemEvent.Pre event) {
		if (!event.getOrigin().is(Items.GLASS_BOTTLE))
			event.setResult(EventResult.ALLOW);
	}

	@SubscribeEvent
	public static void isExchangeAllowed(FoodExchangeItemEvent.Post event) {
		if ((!event.getOrigin().is(Items.GLASS_BOTTLE)) && event.getTarget().is(Items.GLASS_BOTTLE))
			event.setResult(EventResult.ALLOW);
	}*/

	@SuppressWarnings("resource")
	@SubscribeEvent
	public static void onBlockClick(PlayerInteractEvent.RightClickBlock event) {
		ItemStack is = event.getItemStack();
		Player playerIn = event.getEntity();
		if (CVConfig.COMMON.canPlacePotion.get() && is.is(Items.POTION) && playerIn.isShiftKeyDown()) {
			ItemStack replace = new ItemStack(CVItems.POTION.get(), is.getCount());
			replace.set(CVComponents.POTION_ITEM, new PotionItemInfo(is.copy()));
			playerIn.setItemInHand(event.getHand(), replace);
			CommonHooks.onPlaceItemIntoWorld(new UseOnContext(playerIn, event.getHand(), event.getHitVec()));
			is.setCount(replace.getCount());
			playerIn.setItemInHand(event.getHand(), is);
		}
		if (playerIn.isShiftKeyDown() && event.getLevel().getBlockState(event.getPos()).is(CVBlocks.platter.get())) {
			event.setUseItem(TriState.FALSE);
			event.setUseBlock(TriState.TRUE);
		}
	}

}
