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

import com.khjxiaogu.convivium.util.BeverageInfo;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStackTemplate;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CVComponents {
	public static final DeferredRegister<DataComponentType<?>> REGISTRY = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, CVMain.MODID);

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemStackTemplate>> POTION_ITEM = REGISTRY.register("potion_item",
		() -> DataComponentType.<ItemStackTemplate>builder().cacheEncoding().persistent(ItemStackTemplate.CODEC).networkSynchronized(ItemStackTemplate.STREAM_CODEC).build());

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<BeverageInfo>> BEVERAGE_INFO = REGISTRY.register("beverage_info",
		() -> DataComponentType.<BeverageInfo>builder().cacheEncoding().persistent(BeverageInfo.CODEC).networkSynchronized(BeverageInfo.STREAM_CODEC).build());

	public CVComponents() {
	}

}
