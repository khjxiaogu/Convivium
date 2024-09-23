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

import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class CVFluidTagGenerator extends TagsProvider<Fluid> {

	public CVFluidTagGenerator(DataGenerator dataGenerator, String modId, ExistingFileHelper existingFileHelper, CompletableFuture<HolderLookup.Provider> provider) {
		super(dataGenerator.getPackOutput(), Registries.FLUID, provider, modId, existingFileHelper);
	}

	@Override
	protected void addTags(Provider p) {
	}

	@Override
	public String getName() {
		return CVMain.MODID + " fluid tags";
	}
	/*
	 * @Override protected Path getPath(ResourceLocation id) { return
	 * this.generator.getOutputFolder() .resolve("data/" + id.getNamespace() +
	 * "/tags/fluids/" + id.getPath() + ".json"); }
	 */

}
