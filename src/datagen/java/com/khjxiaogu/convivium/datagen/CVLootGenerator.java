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

import com.khjxiaogu.convivium.CVBlocks;
import com.khjxiaogu.convivium.CVMain;
import com.khjxiaogu.convivium.blocks.camellia.CamelliaFlowerBlock;

import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.packs.VanillaBlockLoot;
import net.minecraft.data.loot.packs.VanillaLootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootDataId;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CVLootGenerator extends LootTableProvider {

	public CVLootGenerator(DataGenerator dataGeneratorIn) {
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
			dropSelf(CVBlocks.aeolipile.get());
			dropSelf(CVBlocks.cage.get());
			dropSelf(CVBlocks.cog.get());
			dropSelf(CVBlocks.pam.get());
			dropSelf(CVBlocks.platter.get());
			dropSelf(CVBlocks.whisk.get());
			dropSelf(CVBlocks.basin.get());
			dropSelf(CVBlocks.lead_basin.get());
			add(CVBlocks.CAMELLIA_FLOWER.get(),LootTable.lootTable()
				.withPool(LootPool.lootPool()
					.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(CVBlocks.CAMELLIA_FLOWER.get())
						.setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(CamelliaFlowerBlock.AGE, 3)))
					.setRolls(ConstantValue.exactly(1))
					.add(LootItem.lootTableItem(cpi("camellia_seeds")).apply(SetItemCountFunction.setCount(UniformGenerator.between(1,2)))))
				.withPool(LootPool.lootPool()
					.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(CVBlocks.CAMELLIA_FLOWER.get())
						.setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(CamelliaFlowerBlock.AGE, 0)))
					.setRolls(ConstantValue.exactly(1.0F))
					.add(LootItem.lootTableItem(cpi("fresh_camellia_shoots")).apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))))
				.withPool(LootPool.lootPool()
						.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(CVBlocks.CAMELLIA_FLOWER.get())
							.setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(CamelliaFlowerBlock.AGE, 1)))
						.setRolls(ConstantValue.exactly(1.0F))
						.add(LootItem.lootTableItem(cpi("camellia_flower")).apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))))
				.withPool(LootPool.lootPool()
						.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(CVBlocks.CAMELLIA_FLOWER.get())
							.setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(CamelliaFlowerBlock.AGE, 2)))
						.setRolls(ConstantValue.exactly(1.0F))
						.add(LootItem.lootTableItem(cpi("camellia_flower")).apply(SetItemCountFunction.setCount(ConstantValue.exactly(2)))))
			);
			dropSelf(CVBlocks.BEVERAGE_VENDING_MACHINE.get());
			
			for(RegistryObject<Block> b:CVBlocks.aqueducts) {
				dropSelf(b.get());
			}
			for(RegistryObject<Block> b:CVBlocks.aqueduct_mains) {
				dropSelf(b.get());
			}
		}

		private Block cp(String name) {
			return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(CVMain.MODID, name));
		}
		private Item cpi(String name) {
			return ForgeRegistries.ITEMS.getValue(new ResourceLocation(CVMain.MODID, name));
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
