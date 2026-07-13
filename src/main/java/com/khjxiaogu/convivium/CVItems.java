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

import com.khjxiaogu.convivium.blocks.foods.BeverageItem;
import com.khjxiaogu.convivium.blocks.foods.SorbetItem;
import com.khjxiaogu.convivium.item.BeveragePotionFluid;
import com.khjxiaogu.convivium.item.CVMaterialItem;
import com.khjxiaogu.convivium.item.JugItem;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ClearAllStatusEffectsConsumeEffect;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CVItems {
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CVMain.MODID);
	public static final DeferredItem<JugItem> JUG = ITEMS.registerItem("jug", t -> new JugItem(t.stacksTo(1)));
	public static final String[] base_material = new String[] { "camellia_flower", "camellia_seeds", "clay_basin", "dolium_lid", "fresh_camellia_shoots", "cocoa_powder", "neroli", "spice_blend",
		"powdered_tea", "steamed_camellia_shoots" };
	public static final String[] bottles=new String[] {"bowl","mug","jug","cup"};
	public static final String[] base_drinks = new String[] { "berry_juice", "berry_must", "drupe_juice", "drupe_must", "pome_juice", "pome_must", "tea", "hot_chocolate", "milk", "water" };
	public static final DeferredItem<BeveragePotionFluid> POTION = ITEMS.registerItem("potion_dummy", t -> new BeveragePotionFluid(t));
	public static final DeferredItem<CVMaterialItem> GLASS_BOWL = item("glass_bowl");
	public static final DeferredItem<CVMaterialItem> GLASS_CUP = item("glass_cup");
	public static final DeferredItem<CVMaterialItem> GLASS_JUG = item("glass_jug");
	public static final DeferredItem<CVMaterialItem> GLASS_MUG = item("glass_mug");
	
	
	//public static final DeferredItem<SorbetItem> FLAT_BREAD=ITEMS.register("flat_bread", () -> new SorbetItem(CVBlocks.FLAT_BREAD.get(), createProps(), true));
	public static List<DeferredItem<BeverageItem>> beverages=new ArrayList<>();
	public static List<DeferredItem<SorbetItem>> sorbets=new ArrayList<>();
	static {
		for (String s : base_material) {
			item(s);
		}
		for (String s : base_drinks) {
			Supplier<Fluid> drink_fluids;
			Consumable.Builder csm=Consumables.defaultDrink();
			if("water".equals(s)) {
				drink_fluids=()->Fluids.WATER;
			}else if("milk".equals(s)) {
				drink_fluids=NeoForgeMod.MILK;
				csm.onConsume(ClearAllStatusEffectsConsumeEffect.INSTANCE);
			}else {
				drink_fluids=Lazy.of(()->BuiltInRegistries.FLUID.getValue(CVMain.rl(s)));
			}
			beverages.add(ITEMS.registerItem(s, t -> new BeverageItem(CVBlocks.BEVERAGE.get(),drink_fluids, t.craftRemainder(Items.GLASS_BOTTLE)
				.component(DataComponents.CONSUMABLE, csm.build()).usingConvertsTo(Items.GLASS_BOTTLE), true,false)));
		}
		for (String s : CVFluids.intern.keySet()) {
			Consumable.Builder csm=Consumables.defaultDrink();
			beverages.add(ITEMS.registerItem(s, t -> new BeverageItem(CVBlocks.BEVERAGE.get(),Lazy.of(()->BuiltInRegistries.FLUID.getValue(CVMain.rl(s))), t
				.craftRemainder(Items.GLASS_BOTTLE).usingConvertsTo(Items.GLASS_BOTTLE)
				.component(DataComponents.CONSUMABLE, csm.build()), false,false)));
		}
	/*	for (String s : CVFluids.sorbets) {
			sorbets.add(ITEMS.register(s, () -> ));
		}*/
		
	}

	public static DeferredItem<CVMaterialItem> item(String name) {
		return ITEMS.registerItem(name, CVMaterialItem::new);
	}

}