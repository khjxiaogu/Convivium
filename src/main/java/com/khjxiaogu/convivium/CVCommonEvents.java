/*
 * Copyright (c) 2023 IEEM
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

import javax.annotation.Nonnull;

import com.teammoeg.caupona.data.RecipeReloadListener;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class CVCommonEvents {
	@SubscribeEvent
	public static void addReloadListeners(AddReloadListenerEvent event) {
		event.addListener(new RecipeReloadListener(event.getServerResources()));
	}

	private static TagKey<Item> container = ItemTags.create(new ResourceLocation(CVMain.MODID, "container"));
	@SubscribeEvent
	public static void addManualToPlayer(@Nonnull PlayerEvent.PlayerLoggedInEvent event) {
	}
	@SuppressWarnings("resource")
	@SubscribeEvent
	public static void onBlockClick(PlayerInteractEvent.RightClickBlock event) {
		ItemStack is = event.getItemStack();
		if (is.getItem() == Items.BOWL) {
			Player playerIn = event.getEntity();
			Level worldIn = event.getLevel();
			BlockPos blockpos = event.getPos();
			BlockEntity blockEntity = worldIn.getBlockEntity(blockpos);
			if (blockEntity != null) {
				blockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER, event.getFace())
						.ifPresent(handler -> {

						});
			}

		}
	}


	@SubscribeEvent
	public static void onBowlUse(PlayerInteractEvent.RightClickItem event) {
		if (event.getEntity() != null && !event.getEntity().level().isClientSide
				&& event.getEntity() instanceof ServerPlayer) {
			ItemStack stack = event.getItemStack();
			LazyOptional<IFluidHandlerItem> cap = stack
					.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
			if (cap.isPresent() && stack.is(container)) {
				IFluidHandlerItem data = cap.resolve().get();
			}
		}
	}

	@SubscribeEvent
	public static void onItemUseFinish(LivingEntityUseItemEvent.Finish event) {
		if (event.getEntity() != null && !event.getEntity().level().isClientSide
				&& event.getEntity() instanceof ServerPlayer) {
			ItemStack stack = event.getItem();
			LazyOptional<IFluidHandlerItem> cap = stack
					.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
			if (cap.isPresent() && stack.is(container)) {
				IFluidHandlerItem data = cap.resolve().get();
			}
		}
	}
/*
	@SubscribeEvent(priority = EventPriority.LOW)
	public static void addFeatures(BiomeLoadingEvent event) {
		if (event.getName() != null) {
			BiomeCategory category = event.getCategory();
			// WALNUT
			if (category != BiomeCategory.NETHER && category != BiomeCategory.THEEND) {
				if (Config.SERVER.genWalnut.get() && category == BiomeCategory.FOREST) {
					event.getGeneration().addFeature(Decoration.VEGETAL_DECORATION, CPPlacements.TREES_WALNUT);
				}
				if (Config.SERVER.genFig.get())
					if (category == BiomeCategory.PLAINS || category == BiomeCategory.SAVANNA) {
						event.getGeneration().addFeature(Decoration.VEGETAL_DECORATION, CPPlacements.TREES_FIG);
					}
				if (Config.SERVER.genWolfberry.get())
					if (category == BiomeCategory.EXTREME_HILLS) {
						event.getGeneration().addFeature(Decoration.VEGETAL_DECORATION, CPPlacements.TREES_WOLFBERRY);
					}

			}
			// Structures

		}
	}*/
}
