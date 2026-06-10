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

import com.khjxiaogu.convivium.CVMain;
import com.khjxiaogu.convivium.CVTags;
import com.teammoeg.caupona.CPMain;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DeferredHolder;

public class CVItemTagGenerator extends TagsProvider<Item> {

	public CVItemTagGenerator(DataGenerator dataGenerator, String modId, CompletableFuture<HolderLookup.Provider> provider) {
		super(dataGenerator.getPackOutput(), Registries.ITEM, provider, modId);
	}

	static final String fd = "farmersdelight";
	static final String sf = "simplefarming:";

	@Override
	protected void addTags(Provider pProvider) {
		for (String s : new String[] { "berries", "pomes", "drupes" })
			tag(CVTags.Items.FRUIT).addOptionalTag(ItemTags.create(mrl("fruits/" + s + "/small"))).addOptionalTag(ItemTags.create(mrl("fruits/" + s + "/large")));
		/*
		 * tag(CVTags.Items.FRUIT).add(rk(Items.GLOW_BERRIES,Items.SWEET_BERRIES,Items.
		 * APPLE,Items.MELON_SLICE,Items.GOLDEN_APPLE,Items.GLISTERING_MELON_SLICE)).add
		 * (cp("fig","wolfberries"));
		 */
		tag(CVTags.Items.SPICE).add(cv("neroli")).add(cv("spice_blend")).add(cp("asafoetida"));
		tag(CVTags.Items.NUTS).add(cp("walnut"));
		adds(tag(CVTags.Items.SWEET),Items.SUGAR, Items.HONEYCOMB, Items.HONEY_BOTTLE);
		tag(CVTags.Items.ASSES).add(cp("asses"));
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@SafeVarargs
	private void adds(TagAppender<ResourceKey<Item>, Item> ta,Item... keys) {
		
		ResourceKey[] rks=new ResourceKey[keys.length];
		for(int i=0;i<rks.length;i++)
			rks[i]=rk(keys[i]);
		ta.add(rks);
	}
	@SuppressWarnings("unused")
	private TagAppender<ResourceKey<Item>, Item> tag(String s) {
		return this.tag(ItemTags.create(mrl(s)));
	}
	private TagAppender<ResourceKey<Item>, Item> tag(TagKey<Item> s) {
		return TagAppender.forBuilder(super.getOrCreateRawBuilder(s)) ;
	}
	@SuppressWarnings("unused")
	private TagAppender<ResourceKey<Item>, Item> tag(Identifier s) {
		return tag(ItemTags.create(s)) ;
	}
	private ResourceKey<Item> rk(Item b) {
		
		return BuiltInRegistries.ITEM.getResourceKey(b).orElseThrow();
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
	private TagKey<Item> otag(String s) {
		return ItemTags.create(mrl(s));
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
	private TagKey<Item> ftag(String s) {
		TagKey<Item> tag = ItemTags.create(Identifier.fromNamespaceAndPath("c", s));
		this.tag(tag);
		return tag;
	}

	@SuppressWarnings("unused")
	private Identifier mcrl(String s) {
		return Identifier.withDefaultNamespace(s);
	}

	@Override
	public String getName() {
		return CVMain.MODID + " item tags";
	}

	private ResourceKey<Item> cv(String s) {
		return ResourceKey.create(Registries.ITEM,mrl(s));
	}

	private ResourceKey<Item> cp(String s) {
		return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(CPMain.MODID, s));
	}
	/*
	 * @Override protected Path getPath(ResourceLocation id) { return
	 * this.generator.getOutputFolder() .resolve("data/" + id.getNamespace() +
	 * "/tags/items/" + id.getPath() + ".json"); }
	 */

}
