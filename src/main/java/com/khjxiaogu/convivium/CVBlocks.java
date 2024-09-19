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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import com.khjxiaogu.convivium.blocks.aqueduct.AqueductBlock;
import com.khjxiaogu.convivium.blocks.aqueduct.AqueductControllerBlock;
import com.khjxiaogu.convivium.blocks.basin.BasinBlock;
import com.khjxiaogu.convivium.blocks.camellia.CamelliaBlock;
import com.khjxiaogu.convivium.blocks.camellia.CamelliaFlowerBlock;
import com.khjxiaogu.convivium.blocks.foods.BeverageBlock;
import com.khjxiaogu.convivium.blocks.foods.BeverageItem;
import com.khjxiaogu.convivium.blocks.foods.SorbetBlock;
import com.khjxiaogu.convivium.blocks.foods.SorbetItem;
import com.khjxiaogu.convivium.blocks.kinetics.AeolipileBlock;
import com.khjxiaogu.convivium.blocks.kinetics.CogCageBlock;
import com.khjxiaogu.convivium.blocks.pestle_and_mortar.PamBlock;
import com.khjxiaogu.convivium.blocks.platter.PlatterBlock;
import com.khjxiaogu.convivium.blocks.vending.BeverageVendingBlock;
import com.khjxiaogu.convivium.blocks.whisk.WhiskBlock;
import com.khjxiaogu.convivium.blocks.wolf_fountain.WolfFountainBlock;
import com.teammoeg.caupona.item.CPBlockItem;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CVBlocks {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, CVMain.MODID);
	public static final DeferredHolder<Block, BeverageBlock> BEVERAGE = baseblock("beverage", () -> new BeverageBlock(getBProps()), r -> new BeverageItem(r,()->CVFluids.mixedf.get(), CVItems.createProps(), false));
	public static final DeferredHolder<Block, CogCageBlock> cage = baseblock("cage_wheel", () -> new CogCageBlock(getKineticProps()));
	public static final DeferredHolder<Block, CogCageBlock> cog = baseblock("cog", () -> new CogCageBlock(getKineticProps()));
	public static final DeferredHolder<Block, AeolipileBlock> aeolipile = baseblock("aeolipile", () -> new AeolipileBlock(getKineticProps()));
	public static final DeferredHolder<Block, PlatterBlock> platter = baseblock("fruit_platter", () -> new PlatterBlock(getKineticProps()));
	public static final DeferredHolder<Block, WhiskBlock> whisk = baseblock("whisk", () -> new WhiskBlock(getKineticProps()));
	public static final DeferredHolder<Block, PamBlock> pam = baseblock("pestle_and_mortar", () -> new PamBlock(getKineticProps()));
	public static final DeferredHolder<Block, BasinBlock> basin = baseblock("basin", () -> new BasinBlock(getKineticProps()));
	public static final DeferredHolder<Block, BasinBlock> lead_basin = baseblock("lead_basin", () -> new BasinBlock(Block.Properties.of().sound(SoundType.METAL)
		.strength(3.5f, 10).noOcclusion()));
	public static final DeferredHolder<Block, WolfFountainBlock> wolf_fountain = baseblock("wolf_fountain", () -> new WolfFountainBlock(getKineticProps()));

	public static final List<DeferredHolder<Block, Block>> aqueducts = new ArrayList<>();
	public static final List<DeferredHolder<Block, Block>> aqueduct_mains = new ArrayList<>();
	public static final DeferredHolder<Block, CamelliaFlowerBlock> CAMELLIA_FLOWER = baseblock("camellia_product",
		() -> new CamelliaFlowerBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).replaceable().noCollission()
			.instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).ignitedByLava()
			.pushReaction(PushReaction.DESTROY)));
	public static final DeferredHolder<Block, CamelliaBlock> CAMELLIA = baseblock("camellia_plant", () -> new CamelliaBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD)
		.strength(2.0F).noOcclusion().sound(SoundType.WOOD)));
	public static final DeferredHolder<Block, BeverageVendingBlock> BEVERAGE_VENDING_MACHINE = baseblock("beverage_vending_machine",
		() -> new BeverageVendingBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE)
			.strength(2.0F).noOcclusion().sound(SoundType.STONE)));
	public static final DeferredHolder<Block, SorbetBlock> FLAT_BREAD = baseblock("flatbread",
		() -> new SorbetBlock(getSProps()),t->new SorbetItem(t, null, CVItems.createProps(), true));
	public static final List<Block> beverage = new ArrayList<>();
	public static final List<Block> sorbets = new ArrayList<>();
	static {
		for (String s : new String[] { "felsic_tuff", "stone", "sandstone" }) {
			aqueducts.add(baseblock(s + "_aqueduct", () -> new AqueductBlock(getKineticProps())));
			aqueduct_mains.add(baseblock(s + "_aqueduct_wavemaker", () -> new AqueductControllerBlock(getKineticProps())));
		}
		for(String s:CVFluids.sorbets) {
			baseblock(s+"_sorbet",() -> new SorbetBlock(getSProps()),t->new SorbetItem(t,Lazy.of(()->BuiltInRegistries.FLUID.get(CVMain.rl(s+"_sorbet"))), CVItems.createProps(), false));
		}
	}

	// register any block to registry
	static <T extends Block> DeferredHolder<Block, T> baseblock(String name, Supplier<T> bl) {
		DeferredHolder<Block, T> blx = BLOCKS.register(name, bl);
		CVItems.ITEMS.register(name, () -> new CPBlockItem(blx.get(), CVItems.createProps(), CVMain.MAIN_TAB));
		return blx;
	}

	// register any block to registry with custom item factory
	static <T extends Block> DeferredHolder<Block, T> baseblock(String name, Supplier<T> bl, Function<T, Item> toitem) {
		DeferredHolder<Block, T> blx = BLOCKS.register(name, bl);
		CVItems.ITEMS.register(name, () -> toitem.apply(blx.get()));
		return blx;
	}

	// register basic block to registry
	static DeferredHolder<Block, Block> block(String name, Properties props) {
		DeferredHolder<Block, Block> blx = BLOCKS.register(name, () -> new Block(props));
		CVItems.ITEMS.register(name, () -> new CPBlockItem(blx.get(), CVItems.createProps(), CVMain.MAIN_TAB));
		return blx;
	}

	private static Properties getKineticProps() {
		return Block.Properties.of().sound(SoundType.STONE)
			.strength(3.5f, 10).noOcclusion();
	}

	private static Properties getBProps() {
		return Block.Properties.of().sound(SoundType.GLASS)
			.strength(3.5f, 10).noOcclusion().instabreak().isViewBlocking(CVBlocks::isntSolid);
	}
	private static Properties getSProps() {
		return Block.Properties.of().sound(SoundType.WOOL)
			.strength(3.5f, 10).noOcclusion().instabreak().isViewBlocking(CVBlocks::isntSolid);
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