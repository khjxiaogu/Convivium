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
import com.khjxiaogu.convivium.CVTags;
import com.teammoeg.caupona.CPMain;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CVItemTagGenerator extends TagsProvider<Item> {

	public CVItemTagGenerator(DataGenerator dataGenerator, String modId, ExistingFileHelper existingFileHelper,CompletableFuture<HolderLookup.Provider> provider) {
		super(dataGenerator.getPackOutput(), Registries.ITEM,provider, modId, existingFileHelper);
	}

	static final String fd = "farmersdelight";
	static final String sf = "simplefarming:";

	@SuppressWarnings("unchecked")
	@Override
	protected void addTags(Provider pProvider) {
		for(String s:new String[] {"berries","pomes","drupes"})
			tag(CVTags.Items.FRUIT).addTag(ItemTags.create(mrl("fruits/"+s+"/small"))).addTag(ItemTags.create(mrl("fruits/"+s+"/large")));
		/*tag(CVTags.Items.FRUIT).add(rk(Items.GLOW_BERRIES,Items.SWEET_BERRIES,Items.APPLE,Items.MELON_SLICE,Items.GOLDEN_APPLE,Items.GLISTERING_MELON_SLICE)).add(cp("fig","wolfberries"));*/
		tag(CVTags.Items.SPICE).add(cv("neroli","spice_blend")).add(cp("asafoetida"));
		tag(CVTags.Items.NUTS).add(cp("walnut"));
		tag(CVTags.Items.SWEET).add(rk(Items.SUGAR,Items.HONEYCOMB,Items.HONEY_BOTTLE));
		tag(CVTags.Items.ASSES).add(cp("asses"));
	}

	private TagAppender<Item> tag(String s) {
		return this.tag(ItemTags.create(mrl(s)));
	}

	private TagAppender<Item> tag(ResourceLocation s) {
		return this.tag(ItemTags.create(s));
	}
	private ResourceKey<Item>[] rk(Item... b) {
		ResourceKey[] rks=new ResourceKey[b.length];
		for(int i=0;i<b.length;i++) {
			rks[i]=rki(b[i]);
		}
		return rks;
	}
	private ResourceKey<Item> rki(Item b) {
		
		return ForgeRegistries.ITEMS.getResourceKey(b).orElseGet(()->b.builtInRegistryHolder().key());
	}
	private ResourceLocation rl(RegistryObject<Item> it) {
		return it.getId();
	}

	private ResourceLocation rl(String r) {
		return new ResourceLocation(r);
	}

	private TagKey<Item> otag(String s) {
		return ItemTags.create(mrl(s));
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

	private TagKey<Item> ftag(String s) {
		TagKey<Item> tag = ItemTags.create(new ResourceLocation("forge", s));
		this.tag(tag);
		return tag;
	}

	private ResourceLocation mcrl(String s) {
		return new ResourceLocation(s);
	}

	@Override
	public String getName() {
		return CVMain.MODID + " item tags";
	}
	private ResourceKey<Item>[] cv(String... b) {
		ResourceKey[] rks=new ResourceKey[b.length];
		for(int i=0;i<b.length;i++) {
			rks[i]=cvi(b[i]);
		}
		return rks;
	}
	private ResourceKey<Item> cvi(String s) {
		return ResourceKey.create(Registries.ITEM,mrl(s));
	}
	private ResourceKey<Item>[] cp(String... b) {
		ResourceKey[] rks=new ResourceKey[b.length];
		for(int i=0;i<b.length;i++) {
			rks[i]=cpi(b[i]);
		}
		return rks;
	}
	private ResourceKey<Item> cpi(String s) {
		return ResourceKey.create(Registries.ITEM,new ResourceLocation(CPMain.MODID, s));
	}
/*
	@Override
	protected Path getPath(ResourceLocation id) {
		return this.generator.getOutputFolder()
				.resolve("data/" + id.getNamespace() + "/tags/items/" + id.getPath() + ".json");
	}*/

}
