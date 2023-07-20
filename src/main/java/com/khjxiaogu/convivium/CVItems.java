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

import com.teammoeg.caupona.item.CPItem;
import com.teammoeg.caupona.util.TabType;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CVItems {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CVMain.MODID);
	public static final String[] base_material = new String[] { "camellia_flower","camellia_seeds","clay_basin","dolium_lid","fresh_camellia_shoots","powdered_tea","steamed_camellia_shoots"};

	static {
		for (String s : base_material) {
			item(s, createProps(),CVMain.MAIN_TAB);
		}
	}
	public static RegistryObject<Item> item(String name,Properties props,TabType tab){
		return ITEMS.register(name,()->new CPItem(props,tab));
	}
	static Properties createProps() {
		return new Item.Properties();
	}
}