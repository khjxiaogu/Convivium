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

package com.khjxiaogu.convivium.datagen;

import com.khjxiaogu.convivium.CVMain;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class CPItemModelProvider extends ItemModelProvider {

	public CPItemModelProvider(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper) {
		super(generator.getPackOutput(), modid, existingFileHelper);
	}

	@Override
	protected void registerModels() {
	}

	public void itemModel(Item item, String name) {
		super.withExistingParent(Utils.getRegistryName(item).getPath(), new ResourceLocation(CVMain.MODID, "block/" + name));
	}

	public ItemModelBuilder simpleTexture(String name, String par) {
		return super.singleTexture(name, new ResourceLocation("minecraft", "item/generated"), "layer0",
				new ResourceLocation(CVMain.MODID, "item/" + par + name));
	}

	public ItemModelBuilder texture(String name) {
		return texture(name, name);
	}

	public ItemModelBuilder texture(String name, String par) {
		return super.singleTexture(name, new ResourceLocation("minecraft", "item/generated"), "layer0",
				new ResourceLocation(CVMain.MODID, "item/" + par));
	}
}
