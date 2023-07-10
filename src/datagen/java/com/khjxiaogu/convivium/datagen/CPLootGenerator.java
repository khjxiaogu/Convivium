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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.khjxiaogu.convivium.CVMain;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.packs.VanillaBlockLoot;
import net.minecraft.data.loot.packs.VanillaLootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootDataId;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.registries.ForgeRegistries;

public class CPLootGenerator extends LootTableProvider {

	public CPLootGenerator(DataGenerator dataGeneratorIn) {
		super(dataGeneratorIn.getPackOutput(), Set.of(), VanillaLootTableProvider.create(dataGeneratorIn.getPackOutput()).getTables());
	}

	@Override
	public List<SubProviderEntry> getTables() {
		return Arrays.asList(new SubProviderEntry(() -> new LTBuilder(), LootContextParamSets.BLOCK));
	}

	@Override
	protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationcontext) {
		map.forEach((p_278897_, p_278898_) -> {
			p_278898_.validate(validationcontext.setParams(p_278898_.getParamSet()).enterElement("{" + p_278897_ + "}", new LootDataId<>(LootDataType.TABLE, p_278897_)));
		});
		//map.forEach((name, table) -> LootTables.validate(validationtracker, name, table));
	}

	private static class LTBuilder extends VanillaBlockLoot {
		protected LTBuilder() {
			super();
		}

		@Override
		protected void generate() {
	
		}

		private Block cp(String name) {
			return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(CVMain.MODID, name));
		}

		ArrayList<Block> added = new ArrayList<>();

		@Override
		protected Iterable<Block> getKnownBlocks() {
			return added;
		}

		@Override
		public void dropOther(Block blockIn, ItemLike drop) {
			added.add(blockIn);
			super.dropOther(blockIn, drop);
		}

		protected void add(Block pBlock, LootTable.Builder pLootTableBuilder) {
			added.add(pBlock);
			super.add(pBlock, pLootTableBuilder);
		}


	}
}
