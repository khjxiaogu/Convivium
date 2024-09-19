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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.khjxiaogu.convivium.blocks.foods.BeverageItem;
import com.khjxiaogu.convivium.blocks.foods.SorbetItem;
import com.khjxiaogu.convivium.item.BeveragePotionFluid;
import com.khjxiaogu.convivium.item.CVMaterialItem;
import com.khjxiaogu.convivium.item.JugItem;
import com.teammoeg.caupona.CPMain;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CVItems {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, CVMain.MODID);
	public static final DeferredHolder<Item, JugItem> JUG = ITEMS.register("jug", () -> new JugItem(createProps().stacksTo(1)));
	public static final String[] base_material = new String[] { "camellia_flower", "camellia_seeds", "clay_basin", "dolium_lid", "fresh_camellia_shoots", "cocoa_powder", "neroli", "spice_blend",
		"powdered_tea", "steamed_camellia_shoots" };
	public static final String[] base_drinks = new String[] { "berry_juice", "berry_must", "drupe_juice", "drupe_must", "pome_juice", "pome_must", "tea", "hot_chocolate", "milk", "water" };
	public static final DeferredHolder<Item, BeveragePotionFluid> POTION = ITEMS.register("potion_dummy", () -> new BeveragePotionFluid(createProps()));
	//public static final DeferredHolder<Item, SorbetItem> FLAT_BREAD=ITEMS.register("flat_bread", () -> new SorbetItem(CVBlocks.FLAT_BREAD.get(), createProps(), true));
	public static List<DeferredHolder<Item, BeverageItem>> beverages=new ArrayList<>();
	public static List<DeferredHolder<Item, SorbetItem>> sorbets=new ArrayList<>();
	static {
		for (String s : base_material) {
			item(s, createProps());
		}
		for (String s : base_drinks) {
			Supplier<Fluid> drink_fluids;
			if("water".equals(s)) {
				drink_fluids=()->Fluids.WATER;
			}else if("milk".equals(s)) {
				drink_fluids=NeoForgeMod.MILK;
			}else {
				drink_fluids=Lazy.of(()->BuiltInRegistries.FLUID.get(CVMain.rl(s)));
			}
			beverages.add(ITEMS.register(s, () -> new BeverageItem(CVBlocks.BEVERAGE.get(),drink_fluids, createProps(), true)));
		}
		for (String s : CVFluids.intern.keySet()) {
			beverages.add(ITEMS.register(s, () -> new BeverageItem(CVBlocks.BEVERAGE.get(),Lazy.of(()->BuiltInRegistries.FLUID.get(CVMain.rl(s))), createProps(), false)));
		}
	/*	for (String s : CVFluids.sorbets) {
			sorbets.add(ITEMS.register(s, () -> ));
		}*/
		
	}

	public static DeferredHolder<Item, CVMaterialItem> item(String name, Properties props) {
		return ITEMS.register(name, () -> new CVMaterialItem(props));
	}

	static Properties createProps() {
		return new Item.Properties();
	}
}