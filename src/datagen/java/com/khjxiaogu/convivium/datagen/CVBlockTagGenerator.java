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

package com.khjxiaogu.convivium.datagen;


import java.util.concurrent.CompletableFuture;

import com.khjxiaogu.convivium.CVMain;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CVBlockTagGenerator extends TagsProvider<Block> {

	public CVBlockTagGenerator(DataGenerator dataGenerator, String modId, ExistingFileHelper existingFileHelper,CompletableFuture<HolderLookup.Provider> provider) {
		super(dataGenerator.getPackOutput(), Registries.BLOCK,provider,modId, existingFileHelper);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void addTags(Provider pProvider) {

	}
	@SafeVarargs
	private void adds(TagAppender<Block> ta,ResourceKey<? extends Block>... keys) {
		ResourceKey[] rk=keys;
		ta.add(rk);
	}
	private TagAppender<Block> tag(String s) {
		return this.tag(BlockTags.create(mrl(s)));
	}

	private ResourceKey<Block> cp(String s) {
		return ResourceKey.create(Registries.BLOCK,mrl(s));
	}
	private ResourceKey<Block> rk(Block  b) {
		return ForgeRegistries.BLOCKS.getResourceKey(b).orElseGet(()->b.builtInRegistryHolder().key());
	}
	private TagAppender<Block> tag(ResourceLocation s) {
		return this.tag(BlockTags.create(s));
	}
	private ResourceLocation rl(RegistryObject<Item> it) {
		return it.getId();
	}

	private ResourceLocation rl(String r) {
		return new ResourceLocation(r);
	}

	private TagKey<Block> otag(String s) {
		return BlockTags.create(mrl(s));
	}

	private TagKey<Item> atag(ResourceLocation s) {
		return ItemTags.create(s);
	}

	private ResourceLocation mrl(String s) {
		return new ResourceLocation(CVMain.MODID, s);
	}

	private ResourceLocation frl(String s) {
		return new ResourceLocation("forge", s);
	}

	private ResourceLocation mcrl(String s) {
		return new ResourceLocation(s);
	}

	@Override
	public String getName() {
		return CVMain.MODID + " block tags";
	}


	/*@Override
	protected Path getPath(ResourceLocation id) {
		return super.pathProvider.json("data/" + id.getNamespace() + "/tags/blocks/" + id.getPath() + ".json");
	}*/
}
