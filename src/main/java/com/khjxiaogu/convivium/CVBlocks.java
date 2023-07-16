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

import java.util.function.Function;
import java.util.function.Supplier;

import com.khjxiaogu.convivium.blocks.kinetics.AeolipileBlock;
import com.khjxiaogu.convivium.blocks.kinetics.CogCageBlock;
import com.teammoeg.caupona.item.CPBlockItem;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CVBlocks {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CVMain.MODID);

	public static final RegistryObject<CogCageBlock> cage=baseblock("cage_wheel",()->new CogCageBlock(getKineticProps()));
	public static final RegistryObject<CogCageBlock> cog=baseblock("cog",()->new CogCageBlock(getKineticProps()));
	public static final RegistryObject<AeolipileBlock> aeolipile=baseblock("aeolipile",()->new AeolipileBlock(getKineticProps()));
	
	//register any block to registry
	static <T extends Block> RegistryObject<T> baseblock(String name, Supplier<T> bl) {
		RegistryObject<T> blx = BLOCKS.register(name, bl);
		CVItems.ITEMS.register(name, () -> new CPBlockItem(blx.get(), CVItems.createProps(), CVMain.MAIN_TAB));
		return blx;
	}
	//register any block to registry with custom item factory
	static <T extends Block> RegistryObject<T> baseblock(String name, Supplier<T> bl, Function<T, Item> toitem) {
		RegistryObject<T> blx = BLOCKS.register(name, bl);
		CVItems.ITEMS.register(name, () -> toitem.apply(blx.get()));
		return blx;
	}
	//register basic block to registry
	static RegistryObject<Block> block(String name, Properties props) {
		RegistryObject<Block> blx = BLOCKS.register(name, () -> new Block(props));
		CVItems.ITEMS.register(name, () -> new CPBlockItem(blx.get(), CVItems.createProps(), CVMain.MAIN_TAB));
		return blx;
	}
	//Property functions
	private static Properties getStoneProps() {
		return Block.Properties.of().mapColor(MapColor.STONE).sound(SoundType.STONE).requiresCorrectToolForDrops().strength(2.0f,
				6);
	}
	private static Properties getKineticProps() {
		return Block.Properties.of().sound(SoundType.STONE)
				.strength(3.5f, 10).noOcclusion();
	}

	private static Properties getTransparentProps() {
		return Block.Properties.of().sound(SoundType.STONE).requiresCorrectToolForDrops()
				.strength(3.5f, 10).noOcclusion();
	}

	@SuppressWarnings("unused")
	private static boolean isntSolid(BlockState state, BlockGetter reader, BlockPos pos) {
		return false;
	}

	@SuppressWarnings("unused")
	private static Boolean never(BlockState p_50779_, BlockGetter p_50780_, BlockPos p_50781_, EntityType<?> p_50782_) {
		return (boolean) false;
	}

	@SuppressWarnings("unused")
	private static Boolean ocelotOrParrot(BlockState p_50822_, BlockGetter p_50823_, BlockPos p_50824_,
			EntityType<?> p_50825_) {
		return p_50825_ == EntityType.OCELOT || p_50825_ == EntityType.PARROT;
	}
}