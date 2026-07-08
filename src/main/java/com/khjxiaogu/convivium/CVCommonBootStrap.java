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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.mojang.datafixers.util.Pair;
import com.teammoeg.caupona.CPCapability;
import com.teammoeg.caupona.blocks.foods.IFoodContainer;
import com.teammoeg.caupona.blocks.stove.IStove;
import com.teammoeg.caupona.network.CPBaseBlockEntity;
import com.teammoeg.caupona.util.CreativeTabItemHelper;
import com.teammoeg.caupona.util.FluidItemWrapper;
import com.teammoeg.caupona.util.ICreativeModeTabItem;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.ItemAccessFluidHandler;

@EventBusSubscriber(modid = CVMain.MODID)
public class CVCommonBootStrap {
	public static final List<Pair<Supplier<? extends ItemLike>, Float>> compositables = new ArrayList<>();

	@SubscribeEvent
	public static void onCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
		CreativeTabItemHelper helper = new CreativeTabItemHelper(event.getTabKey(), event.getTab());
		CVItems.ITEMS.getEntries().forEach(e -> {
			if (e.get() instanceof ICreativeModeTabItem item) {
				item.fillItemCategory(helper);
			}
		});
		helper.register(event);

	}

	public static <R extends ItemLike, T extends R> DeferredHolder<R, T> asCompositable(DeferredHolder<R, T> obj, float val) {
		compositables.add(Pair.of(obj, val));
		return obj;
	}

	@SuppressWarnings("deprecation")
	@SubscribeEvent
	public static void onCommonSetup(@SuppressWarnings("unused") FMLCommonSetupEvent event) {
		registerDispensers();
		compositables.forEach(p -> ComposterBlock.COMPOSTABLES.put(p.getFirst().get(), (float) p.getSecond()));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SubscribeEvent
	public static void onCapabilityInject(RegisterCapabilitiesEvent event) {
		event.registerItem(Capabilities.Fluid.ITEM, (_, o) -> new ItemAccessFluidHandler(o,CPCapability.SIMPLE_FLUID.get(), 1250), CVItems.JUG.get());
		// event.registerItem(Capabilities.FluidHandler.ITEM,(stack,o)->new
		// FluidHandlerItemStack(CPCapability.SIMPLE_FLUID,stack,1250),
		// CPItems.situla.get());
		// event.registerItem(CPCapability.FOOD_INFO,(stack,o)->stack.get(CPCapability.STEW_INFO.get()),
		// CPItems.stews.toArray(Item[]::new));
		// event.registerItem(CPCapability.FOOD_INFO,(stack,o)->stack.get(CPCapability.SAUTEED_INFO.get()),
		// CPItems.dish.toArray(Item[]::new));
		CVBlockEntityTypes.REGISTER.getEntries().stream().map(t->t.get()).forEach(be->{
			event.registerBlockEntity(Capabilities.Item.BLOCK, (BlockEntityType<?>)be,
				(block,ctx)->(block instanceof CPBaseBlockEntity)?(ResourceHandler)((CPBaseBlockEntity)block).getCapability(Capabilities.Item.BLOCK, ctx):null);
			event.registerBlockEntity(Capabilities.Fluid.BLOCK, (BlockEntityType<?>)be,
				(block,ctx)->(block instanceof CPBaseBlockEntity)?(ResourceHandler)((CPBaseBlockEntity)block).getCapability(Capabilities.Fluid.BLOCK, ctx):null);
			event.registerBlockEntity(CPCapability.HEAT_STOVE, (BlockEntityType<?>)be,
					(block,ctx)->(block instanceof CPBaseBlockEntity)?(IStove)((CPBaseBlockEntity)block).getCapability(CPCapability.HEAT_STOVE, ctx):null);
			event.registerBlockEntity(CPCapability.FOOD_CONTAINER, (BlockEntityType<?>)be,
				(block,ctx)->(block instanceof CPBaseBlockEntity)?(IFoodContainer)((CPBaseBlockEntity)block).getCapability(CPCapability.FOOD_CONTAINER, ctx):null);
	
		});
		event.registerItem(Capabilities.Fluid.ITEM, (_, o) -> new FluidItemWrapper(o), CVItems.beverages.stream().map(t->t.get()).toArray(Item[]::new));
		event.registerItem(Capabilities.Fluid.ITEM, (_, o) -> new FluidItemWrapper(o), CVBlocks.BEVERAGE.get().asItem());

		
		event.registerItem(Capabilities.Fluid.ITEM, (_, o) -> new FluidItemWrapper(o), CVBlocks.BOWL.get().asItem());
		event.registerItem(Capabilities.Fluid.ITEM, (_, o) -> new FluidItemWrapper(o), CVBlocks.JUG.get().asItem());
		event.registerItem(Capabilities.Fluid.ITEM, (_, o) -> new FluidItemWrapper(o), CVBlocks.MUG.get().asItem());
		event.registerItem(Capabilities.Fluid.ITEM, (_, o) -> new FluidItemWrapper(o), CVBlocks.CUP.get().asItem());
		
	}

	public static void registerDispensers() {

	}

}
