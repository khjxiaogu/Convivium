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

import javax.annotation.Nonnull;

import com.khjxiaogu.convivium.data.recipes.ContainingRecipe;
import com.khjxiaogu.convivium.data.recipes.RecipeReloadListener;
import com.teammoeg.caupona.CPTags;
import com.teammoeg.caupona.api.CauponaApi;
import com.teammoeg.caupona.fluid.SoupFluid;
import com.teammoeg.caupona.util.StewInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class CVCommonEvents {
	@SubscribeEvent
	public static void addReloadListeners(AddReloadListenerEvent event) {
		event.addListener(new RecipeReloadListener(event.getServerResources()));
	}

	@SubscribeEvent
	public static void addManualToPlayer(@Nonnull PlayerEvent.PlayerLoggedInEvent event) {
	}
	@SuppressWarnings("resource")
	@SubscribeEvent
	public static void onBlockClick(PlayerInteractEvent.RightClickBlock event) {
		ItemStack is = event.getItemStack();
		if (is.getItem() == Items.GLASS_BOTTLE) {
			Player playerIn = event.getEntity();
			Level worldIn = event.getLevel();
			BlockPos blockpos = event.getPos();
			BlockEntity blockEntity = worldIn.getBlockEntity(blockpos);
			if (blockEntity != null) {
				blockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER, event.getFace())
						.ifPresent(handler -> {
							FluidStack stack = handler.drain(250, FluidAction.SIMULATE);
							ContainingRecipe recipe = ContainingRecipe.recipes.get(stack.getFluid());
							if (recipe != null && stack.getAmount() == 250) {
								stack = handler.drain(250, FluidAction.EXECUTE);
								if (stack.getAmount() == 250) {

									ItemStack ret = recipe.handle(stack);
									event.setCanceled(true);
									event.setCancellationResult(InteractionResult.sidedSuccess(worldIn.isClientSide));
									if (is.getCount() > 1) {
										is.shrink(1);
										if (!playerIn.addItem(ret)) {
											playerIn.drop(ret, false);
										}
									} else
										playerIn.setItemInHand(event.getHand(), ret);
								}
							}
						});
			}

		}
	}

	@SuppressWarnings("resource")
	@SubscribeEvent
	public static void onItemUse(PlayerInteractEvent.RightClickItem event) {
		ItemStack is = event.getItemStack();
		if (is.getItem() == Items.GLASS_BOTTLE) {
			Level worldIn = event.getLevel();
			Player playerIn = event.getEntity();
			BlockHitResult ray = Item.getPlayerPOVHitResult(worldIn, playerIn, Fluid.SOURCE_ONLY);
			if (ray.getType() == Type.BLOCK) {
				BlockPos blockpos = ray.getBlockPos();
				BlockState blockstate1 = worldIn.getBlockState(blockpos);
				net.minecraft.world.level.material.Fluid f = blockstate1.getFluidState().getType();
				if (f != Fluids.EMPTY) {
					ContainingRecipe recipe = ContainingRecipe.recipes.get(f);
					if (recipe == null)
						return;
					ItemStack ret = recipe.handle(f);
					event.setCanceled(true);
					event.setCancellationResult(InteractionResult.sidedSuccess(worldIn.isClientSide));
					if (is.getCount() > 1) {
						is.shrink(1);
						if (!playerIn.addItem(ret)) {
							playerIn.drop(ret, false);
						}
					} else
						playerIn.setItemInHand(event.getHand(), ret);
				}
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
			if (cap.isPresent() && stack.is(CPTags.Items.CONTAINER)) {
				IFluidHandlerItem data = cap.resolve().get();
				if (data.getFluidInTank(0).getFluid() instanceof SoupFluid) {
					StewInfo si = SoupFluid.getInfo(data.getFluidInTank(0));
					if (!event.getEntity().canEat(si.canAlwaysEat())) {
						event.setCancellationResult(InteractionResult.FAIL);
						event.setCanceled(true);
					}
				}
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
			if (cap.isPresent() && stack.is(CPTags.Items.CONTAINER)) {
				IFluidHandlerItem data = cap.resolve().get();
				if (data.getFluidInTank(0).getFluid() instanceof SoupFluid)
					CauponaApi.apply(event.getEntity().level(), event.getEntity(),
							SoupFluid.getInfo(data.getFluidInTank(0)));
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
