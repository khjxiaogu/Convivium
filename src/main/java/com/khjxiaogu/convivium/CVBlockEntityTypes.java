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

package com.khjxiaogu.convivium;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;
import com.khjxiaogu.convivium.blocks.aqueduct.AqueductBlockEntity;
import com.khjxiaogu.convivium.blocks.aqueduct.AqueductControllerBlockEntity;
import com.khjxiaogu.convivium.blocks.basin.BasinBlockEntity;
import com.khjxiaogu.convivium.blocks.foods.BeverageBlockEntity;
import com.khjxiaogu.convivium.blocks.kinetics.AeolipileBlockEntity;
import com.khjxiaogu.convivium.blocks.kinetics.CogeCageBlockEntity;
import com.khjxiaogu.convivium.blocks.pestle_and_mortar.PamBlockEntity;
import com.khjxiaogu.convivium.blocks.platter.PlatterBlockEntity;
import com.khjxiaogu.convivium.blocks.vending.BeverageVendingBlockEntity;
import com.khjxiaogu.convivium.blocks.whisk.WhiskBlockEntity;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CVBlockEntityTypes {
	public static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister
			.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CVMain.MODID);
	public static final RegistryObject<BlockEntityType<CogeCageBlockEntity>> COG_CAGE=
			REGISTER.register("cog_cage",makeTypes2(CogeCageBlockEntity::new,()->List.of(CVBlocks.cog,CVBlocks.cage)));
	public static final RegistryObject<BlockEntityType<AeolipileBlockEntity>> AOELIPILE=
			REGISTER.register("aoelipile",makeType(AeolipileBlockEntity::new,()->CVBlocks.aeolipile));
	public static final RegistryObject<BlockEntityType<PlatterBlockEntity>> PLATTER=
			REGISTER.register("platter",makeType(PlatterBlockEntity::new,()->CVBlocks.platter));
	public static final RegistryObject<BlockEntityType<WhiskBlockEntity>> WHISK=
			REGISTER.register("whisk",makeType(WhiskBlockEntity::new,()->CVBlocks.whisk));
	public static final RegistryObject<BlockEntityType<PamBlockEntity>> PAM=
			REGISTER.register("pestle_and_mortar",makeType(PamBlockEntity::new,()->CVBlocks.pam));
	public static final RegistryObject<BlockEntityType<BasinBlockEntity>> BASIN=
			REGISTER.register("basin",makeTypes2(BasinBlockEntity::new,()->List.of(CVBlocks.basin,CVBlocks.lead_basin)));
	public static final RegistryObject<BlockEntityType<AqueductBlockEntity>> AQUEDUCT=
			REGISTER.register("aqueduct",makeTypes2(AqueductBlockEntity::new,()->CVBlocks.aqueducts));
	public static final RegistryObject<BlockEntityType<AqueductControllerBlockEntity>> AQUEDUCT_MAIN=
			REGISTER.register("aqueduct_controller",makeTypes2(AqueductControllerBlockEntity::new,()->CVBlocks.aqueduct_mains));
	public static final RegistryObject<BlockEntityType<BeverageBlockEntity>> BEVERAGE=
			REGISTER.register("beverage",makeType(BeverageBlockEntity::new,()->CVBlocks.BEVERAGE));
	public static final RegistryObject<BlockEntityType<BeverageVendingBlockEntity>> BEVERAGE_VENDING_MACHINE=
			REGISTER.register("beverage_vending_machine",makeType(BeverageVendingBlockEntity::new,()->CVBlocks.BEVERAGE_VENDING_MACHINE));
	private static <T extends BlockEntity> Supplier<BlockEntityType<T>> makeType(BlockEntitySupplier<T> create,
			Supplier<RegistryObject<? extends Block>> valid) {
		return () -> new BlockEntityType<>(create, ImmutableSet.of(valid.get().get()), null);
	}
	private static <T extends BlockEntity,E extends Block> Supplier<BlockEntityType<T>> makeTypes2(BlockEntitySupplier<T> create,
			Supplier<List<RegistryObject<E>>> valid) {
		return () -> new BlockEntityType<>(create, valid.get().stream().map(RegistryObject<E>::get).collect(Collectors.toSet()), null);
	}

}