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

import com.khjxiaogu.convivium.blocks.pestle_and_mortar.PamContainer;
import com.khjxiaogu.convivium.blocks.platter.PlatterContainer;
import com.khjxiaogu.convivium.blocks.vending.BeverageVendingContainer;
import com.khjxiaogu.convivium.blocks.whisk.WhiskContainer;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CVGui {
	public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.MENU_TYPES,
			CVMain.MODID);
	public static final RegistryObject<MenuType<PlatterContainer>> PLATTER = CONTAINERS.register("platter",
			() -> IForgeMenuType.create(PlatterContainer::new));
	public static final RegistryObject<MenuType<PamContainer>> PAM = CONTAINERS.register("pestle_and_mortar",
			() -> IForgeMenuType.create(PamContainer::new));
	public static final RegistryObject<MenuType<WhiskContainer>> WHISK = CONTAINERS.register("whisk",
			() -> IForgeMenuType.create(WhiskContainer::new));
	public static final RegistryObject<MenuType<BeverageVendingContainer>> VENDING = CONTAINERS.register("beverage_vending_machine",
			() -> IForgeMenuType.create(BeverageVendingContainer::new));
}