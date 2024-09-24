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
import com.khjxiaogu.convivium.util.PotionItemInfo;
import com.teammoeg.caupona.CPMain;
import com.teammoeg.caupona.util.SerializeUtil;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CVComponents {
	public static final DeferredRegister<DataComponentType<?>> REGISTRY = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, CPMain.MODID);

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<PotionItemInfo>> POTION_ITEM = REGISTRY.register("potion_item",
		() -> DataComponentType.<PotionItemInfo>builder().cacheEncoding().persistent(PotionItemInfo.CODEC).build());

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<BeverageInfo>> BEVERAGE_INFO = REGISTRY.register("beverage_info",
		() -> DataComponentType.<BeverageInfo>builder().cacheEncoding().persistent(BeverageInfo.CODEC).networkSynchronized(SerializeUtil.toStreamCodec(BeverageInfo.CODEC)).build());

	public CVComponents() {
	}

}
