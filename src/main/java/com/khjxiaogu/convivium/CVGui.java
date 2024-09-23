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

import com.khjxiaogu.convivium.blocks.basin.BasinContainer;
import com.khjxiaogu.convivium.blocks.pestle_and_mortar.PamContainer;
import com.khjxiaogu.convivium.blocks.platter.PlatterContainer;
import com.khjxiaogu.convivium.blocks.vending.BeverageVendingContainer;
import com.khjxiaogu.convivium.blocks.whisk.WhiskContainer;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CVGui {
	public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(BuiltInRegistries.MENU,
		CVMain.MODID);
	public static final DeferredHolder<MenuType<?>, MenuType<PlatterContainer>> PLATTER = CONTAINERS.register("platter",
		() -> IMenuTypeExtension.create(PlatterContainer::new));
	public static final DeferredHolder<MenuType<?>, MenuType<PamContainer>> PAM = CONTAINERS.register("pestle_and_mortar",
		() -> IMenuTypeExtension.create(PamContainer::new));
	public static final DeferredHolder<MenuType<?>, MenuType<WhiskContainer>> WHISK = CONTAINERS.register("whisk",
		() -> IMenuTypeExtension.create(WhiskContainer::new));
	public static final DeferredHolder<MenuType<?>, MenuType<BeverageVendingContainer>> VENDING = CONTAINERS.register("beverage_vending_machine",
		() -> IMenuTypeExtension.create(BeverageVendingContainer::new));
	public static final DeferredHolder<MenuType<?>, MenuType<BasinContainer>> BASIN = CONTAINERS.register("basin",
		() -> IMenuTypeExtension.create(BasinContainer::new));
}