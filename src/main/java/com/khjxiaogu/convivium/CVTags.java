/*
 * Copyright (c) 2023 IEEM
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

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public class CVTags {
	public static class Blocks{
		private static TagKey<Block> create(String s){
			return  BlockTags.create(new ResourceLocation(CVMain.MODID, s));
		}
		
	}
	public static class Items{

		private static TagKey<Item> create(String s){
			return ItemTags.create(new ResourceLocation(CVMain.MODID, s));
		}
	}
	
	public static class Fluids{
		private static TagKey<Fluid> create(String s){
			return FluidTags.create(new ResourceLocation(CVMain.MODID, s));
		}

		
	}
	
}
