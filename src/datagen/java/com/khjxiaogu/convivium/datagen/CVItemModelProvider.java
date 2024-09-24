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

package com.khjxiaogu.convivium.datagen;

import com.khjxiaogu.convivium.CVFluids;
import com.khjxiaogu.convivium.CVItems;
import com.khjxiaogu.convivium.CVMain;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class CVItemModelProvider extends ItemModelProvider {

	public CVItemModelProvider(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper) {
		super(generator.getPackOutput(), modid, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		for (String mt : CVItems.base_material) {
			texture(mt);
		}
		for (String mt : CVItems.base_drinks) {
			simpleTexture(mt, "beverages/");
		}
		for (String mt : CVFluids.intern.keySet()) {
			simpleTexture(mt, "beverages/");
		}
		for(String mt:CVFluids.sorbets) {
			super.singleTexture(mt+"_sorbet", ResourceLocation.fromNamespaceAndPath("minecraft", "item/generated"), "layer0",
				ResourceLocation.fromNamespaceAndPath(CVMain.MODID, "item/sorbets/" + mt));

		}
		texture("flatbread");
		potTexture("beverage", "alcohol_bottle", "beverages/");
		texture("jug");
	}

	public void itemModel(Item item, String name) {
		super.withExistingParent(Utils.getRegistryName(item).getPath(), ResourceLocation.fromNamespaceAndPath(CVMain.MODID, "block/" + name));
	}

	public ItemModelBuilder potTexture(String n, String name, String par) {
		return withExistingParent(n, ResourceLocation.fromNamespaceAndPath("minecraft", "item/generated"))
			.texture("layer0", ResourceLocation.fromNamespaceAndPath(CVMain.MODID, "item/" + par + name))
			.texture("layer1", ResourceLocation.fromNamespaceAndPath(CVMain.MODID, "item/" + par + name + "_overlay"));
	}

	public ItemModelBuilder simpleTexture(String name, String par) {
		return super.singleTexture(name, ResourceLocation.fromNamespaceAndPath("minecraft", "item/generated"), "layer0",
			ResourceLocation.fromNamespaceAndPath(CVMain.MODID, "item/" + par + name));
	}

	public ItemModelBuilder texture(String name) {
		return texture(name, name);
	}

	public ItemModelBuilder texture(String name, String par) {
		return super.singleTexture(name, ResourceLocation.fromNamespaceAndPath("minecraft", "item/generated"), "layer0",
			ResourceLocation.fromNamespaceAndPath(CVMain.MODID, "item/" + par));
	}
}
