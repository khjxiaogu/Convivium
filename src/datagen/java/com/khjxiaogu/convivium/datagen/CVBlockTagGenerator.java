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

import java.util.concurrent.CompletableFuture;

import com.khjxiaogu.convivium.CVBlocks;
import com.khjxiaogu.convivium.CVMain;
import com.khjxiaogu.convivium.CVTags;
import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.CPItems;
import com.teammoeg.caupona.CPMain;
import com.teammoeg.caupona.CPTags;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;

public class CVBlockTagGenerator extends TagsProvider<Block> {


	public CVBlockTagGenerator(DataGenerator dataGenerator, String modId,CompletableFuture<HolderLookup.Provider> provider) {
		super(dataGenerator.getPackOutput(), Registries.BLOCK,provider,modId);
	}

	@Override
	protected void addTags(Provider pProvider) {
		for (String s : new String[] { "felsic_tuff", "stone", "sandstone" }) {
			this.tag(CVTags.Blocks.AQUEDUCT).add(cv(s + "_aqueduct")).add(cv(s + "_aqueduct_wavemaker"));
		}
		adds(this.tag(CPTags.Blocks.CHIMNEY_IGNORES), CVBlocks.AEOLIPILE.getKey());
		adds(this.tag(CVTags.Blocks.AQUEDUCT_MOVE), CPBlocks.BOWL.getKey(), CPBlocks.DISH.getKey(), CVBlocks.PLATTER.getKey(), CPBlocks.WOLF.getKey(),
			CPBlocks.COPPER_PAN.getKey(), CPBlocks.LEAD_PAN.getKey(), CPBlocks.IRON_PAN.getKey(), CPBlocks.GRAVY_BOAT.getKey(), CPBlocks.STONE_PAN.getKey(), CVBlocks.BEVERAGE.getKey());
		for (String s : CPItems.dishes) {
			this.tag(CVTags.Blocks.AQUEDUCT_MOVE).add(cpn(s));
		}
		adds(tag(BlockTags.MINEABLE_WITH_AXE), CVBlocks.CAGE.getKey(), CVBlocks.COG.getKey(), CVBlocks.PLATTER.getKey(), CVBlocks.CAMELLIA.getKey());
		adds(tag(BlockTags.MINEABLE_WITH_PICKAXE), CVBlocks.WHISK.getKey(), CVBlocks.AEOLIPILE.getKey(), CVBlocks.PAM.getKey(), CVBlocks.BASIN.getKey(), CVBlocks.LEAD_BASIN.getKey(),
			CVBlocks.BEVERAGE_VENDING_MACHINE.getKey());
		for (DeferredHolder<Block, Block> i : CVBlocks.AQUEDUCTS) {
			tag(BlockTags.MINEABLE_WITH_PICKAXE).add(i.getKey());
		}
		for (DeferredHolder<Block, Block> i : CVBlocks.AQUEDUCT_CONTROLLERS) {
			tag(BlockTags.MINEABLE_WITH_PICKAXE).add(i.getKey());
		}

	}

	@SuppressWarnings("unchecked")
	@SafeVarargs
	private void adds(TagAppender<ResourceKey<Block>, Block> ta,ResourceKey<? extends Block>... keys) {
		for(ResourceKey<? extends Block> blk:keys)
		ta.add((ResourceKey<Block>) blk);
	}
	@SuppressWarnings("unused")
	private TagAppender<ResourceKey<Block>, Block> tag(String s) {
		return this.tag(BlockTags.create(mrl(s)));
	}
	private TagAppender<ResourceKey<Block>, Block> tag(TagKey<Block> s) {
		return TagAppender.forBuilder(super.getOrCreateRawBuilder(s)) ;
	}
	@SuppressWarnings("unused")
	private TagAppender<ResourceKey<Block>, Block> tag(Identifier s) {
		return tag(BlockTags.create(s)) ;
	}
	private ResourceKey<Block> cv(String s) {
		return ResourceKey.create(Registries.BLOCK,mrl(s));
	}
	private ResourceKey<Block> cpn(String s) {
		return ResourceKey.create(Registries.BLOCK,CPMain.rl(s));
	}
	@SuppressWarnings("unused")
	private ResourceKey<Block> rk(Block  b) {
		return BuiltInRegistries.BLOCK.getResourceKey(b).get();
	}

	@SuppressWarnings("unused")
	private Identifier rl(DeferredHolder<Item,Item> it) {
		return it.getId();
	}

	@SuppressWarnings("unused")
	private Identifier rl(String r) {
		return Identifier.parse(r);
	}

	@SuppressWarnings("unused")
	private TagKey<Block> otag(String s) {
		return BlockTags.create(mrl(s));
	}

	@SuppressWarnings("unused")
	private TagKey<Item> atag(Identifier s) {
		return ItemTags.create(s);
	}

	private Identifier mrl(String s) {
		return Identifier.fromNamespaceAndPath(CVMain.MODID, s);
	}

	@SuppressWarnings("unused")
	private Identifier frl(String s) {
		return Identifier.fromNamespaceAndPath("c", s);
	}

	@SuppressWarnings("unused")
	private Identifier mcrl(String s) {
		return Identifier.withDefaultNamespace(s);
	}

	@Override
	public String getName() {
		return CVMain.MODID + " block tags";
	}
}
