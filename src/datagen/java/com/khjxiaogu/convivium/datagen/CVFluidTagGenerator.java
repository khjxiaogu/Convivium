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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CVFluidTagGenerator extends TagsProvider<Fluid> {

	public CVFluidTagGenerator(DataGenerator dataGenerator, String modId, ExistingFileHelper existingFileHelper,CompletableFuture<HolderLookup.Provider> provider) {
		super(dataGenerator.getPackOutput(), Registries.FLUID, provider,modId,existingFileHelper);
	}

	@Override
	protected void addTags(Provider p) {
	}
	private Fluid cp(String s) {
		Fluid i = ForgeRegistries.FLUIDS.getValue(mrl(s));
		return i;// just going to cause trouble if not exists
	}
	private TagAppender<Fluid> tag(String s) {
		return this.tag(FluidTags.create(mrl(s)));
	}

	private TagAppender<Fluid> tag(ResourceLocation s) {
		return this.tag(FluidTags.create(s));
	}

	private ResourceLocation rl(RegistryObject<Fluid> it) {
		return it.getId();
	}

	private ResourceLocation rl(String r) {
		return new ResourceLocation(r);
	}

	private TagKey<Fluid> otag(String s) {
		return FluidTags.create(mrl(s));
	}

	private TagKey<Fluid> atag(ResourceLocation s) {
		return FluidTags.create(s);
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
		return CVMain.MODID + " fluid tags";
	}
/*
	@Override
	protected Path getPath(ResourceLocation id) {
		return this.generator.getOutputFolder()
				.resolve("data/" + id.getNamespace() + "/tags/fluids/" + id.getPath() + ".json");
	}*/

}
