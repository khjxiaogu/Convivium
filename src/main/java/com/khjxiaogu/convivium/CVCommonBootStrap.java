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

import com.mojang.datafixers.util.Pair;
import com.teammoeg.caupona.util.CreativeTabItemHelper;
import com.teammoeg.caupona.util.ICreativeModeTabItem;

import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = CVMain.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CVCommonBootStrap {
	public static final List<Pair<Supplier<? extends ItemLike>,Float>> compositables = new ArrayList<>();
	@SubscribeEvent
	public static void onCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
		CreativeTabItemHelper helper=new CreativeTabItemHelper(event.getTabKey(),event.getTab());
		CVItems.ITEMS.getEntries().forEach(e->{
			if(e.get() instanceof ICreativeModeTabItem item) {
				item.fillItemCategory(helper);
			}
		});
		helper.register(event);
		
	}
	public static <T extends ItemLike> RegistryObject<T> asCompositable(RegistryObject<T> obj,float val){
		compositables.add(Pair.of(obj, val));
		return obj;
	} 
	@SubscribeEvent
	public static void onCommonSetup(@SuppressWarnings("unused") FMLCommonSetupEvent event) {
		registerDispensers();
		compositables.forEach(p->ComposterBlock.COMPOSTABLES.put(p.getFirst().get(),(float)p.getSecond()));
	}


	public static void registerDispensers() {
		
	}


	
	
}
