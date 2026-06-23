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

import java.util.function.BiConsumer;

import com.khjxiaogu.convivium.CVFluids;
import com.khjxiaogu.convivium.CVItems;
import com.khjxiaogu.convivium.CVMain;
import com.khjxiaogu.convivium.client.BeverageTint;

import net.minecraft.client.color.item.Constant;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ItemModelOutput;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.item.ItemModel.Unbaked;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

public class CVItemModelProvider extends ItemModelGenerators {

	public CVItemModelProvider(ItemModelOutput itemModelOutput, BiConsumer<Identifier, ModelInstance> modelOutput) {
		super(itemModelOutput, modelOutput);
	}
	public static final ModelTemplate POT_TEMPLATE=ModelTemplates.createItem("generated", TextureSlot.LAYER0, TextureSlot.LAYER1, TextureSlot.PARTICLE);
	
	@Override
	public void run() {
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
			texture(mt+"_sorbet","sorbets/" + mt);

		}
		texture("flatbread");
		potTexture("beverage", "alcohol_bottle", "beverages/");
		for(String bottleType:CVItems.bottles) {
			texture("glass_"+bottleType,"glass_" + bottleType + "_empty");
			potTexture("beverage_"+bottleType, "glass_"+bottleType, "beverages/");
		}
		texture("jug");
	}

	public void potTexture(String n, String name, String par) {

		Item item=BuiltInRegistries.ITEM.getValue(CVMain.rl(n));
    	Identifier rkey=CVMain.rl( par + name);
    	Identifier texture=rkey.withPrefix("item/");
    	Identifier overlay=texture.withSuffix("_overlay");
        Identifier model = POT_TEMPLATE.create(item, new TextureMapping().put(TextureSlot.LAYER0, mat(overlay)).put(TextureSlot.LAYER1, mat(texture)).put(TextureSlot.PARTICLE, mat(texture)), modelOutput);
        this.itemModelOutput.accept(item, ItemModelUtils.tintedModel(model,new BeverageTint(0xff3333aa)));

	}
    public Material mat(Identifier path) {
    	return new Material(path,false);
    }

	public void simpleTexture(String name, String par) {
		this.itemModelOutput.accept(BuiltInRegistries.ITEM.getValue(CVMain.rl(name)),
		ItemModelUtils.plainModel(ModelTemplates.FLAT_ITEM.create(CVMain.rl("item/" + name),new TextureMapping().put(TextureSlot.LAYER0, new Material(CVMain.rl("item/" + par + name),false)), this.modelOutput)));

	}
	public Unbaked plain(String name) {
		return plain(name,"");
	}
	public Unbaked plain(String name, String par) {
		return ItemModelUtils.plainModel(ModelTemplates.FLAT_ITEM.create(CVMain.rl("item/" + name),new TextureMapping().put(TextureSlot.LAYER0, new Material(CVMain.rl("item/" + par + name),false)), this.modelOutput))
		;

	}
	public void texture(String name) {
		texture(name, name);
	}
	public void texture(Item name, String par) {
		this.itemModelOutput.accept(name,
			ItemModelUtils.plainModel(ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(name), TextureMapping.layer0(new Material(Identifier.fromNamespaceAndPath(CVMain.MODID, "item/"+par))), this.modelOutput)
				));
	}
	public void texture(String name, String par) {
		texture(BuiltInRegistries.ITEM.getValue(CVMain.rl(name)),par);
	}
}
