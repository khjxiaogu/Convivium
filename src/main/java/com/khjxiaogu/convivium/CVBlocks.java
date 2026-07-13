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

package com.khjxiaogu.convivium;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

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
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.component.UseRemainder;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CVBlocks {
	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CVMain.MODID);
	public static final DeferredBlock<BeverageBlock> BEVERAGE = baseblock("beverage", b -> new BeverageBlock(getBProps(b),BeverageBlock.BOTTLE_SHAPE), (b,r) -> new BeverageItem(r,()->CVFluids.mixedf.get(), b.craftRemainder(Items.GLASS_BOTTLE).usingConvertsTo(Items.GLASS_BOTTLE).component(DataComponents.CONSUMABLE,Consumables.DEFAULT_DRINK), false,true));
	public static final DeferredBlock<BeverageBlock> BOWL = baseblock("beverage_bowl", b -> new BeverageBlock(getBProps(b),BeverageBlock.BOWL_SHAPE), (b,r) -> new BeverageItem(r,()->CVFluids.mixedf.get(), b.craftRemainder(new ItemStackTemplate(CVItems.GLASS_BOWL)).component(DataComponents.USE_REMAINDER, new UseRemainder(new ItemStackTemplate(CVItems.GLASS_BOWL))).component(DataComponents.CONSUMABLE,Consumables.DEFAULT_DRINK), false,true));
	public static final DeferredBlock<BeverageBlock> CUP = baseblock("beverage_cup", b -> new BeverageBlock(getBProps(b),BeverageBlock.CUP_SHAPE), (b,r) -> new BeverageItem(r,()->CVFluids.mixedf.get(), b.craftRemainder(new ItemStackTemplate(CVItems.GLASS_CUP)).component(DataComponents.USE_REMAINDER, new UseRemainder(new ItemStackTemplate(CVItems.GLASS_CUP))).component(DataComponents.CONSUMABLE,Consumables.DEFAULT_DRINK), false,true));
	public static final DeferredBlock<BeverageBlock> JUG = baseblock("beverage_jug", b -> new BeverageBlock(getBProps(b),BeverageBlock.JUG_SHAPE), (b,r) -> new BeverageItem(r,()->CVFluids.mixedf.get(), b.craftRemainder(new ItemStackTemplate(CVItems.GLASS_JUG)).component(DataComponents.USE_REMAINDER, new UseRemainder(new ItemStackTemplate(CVItems.GLASS_JUG))).component(DataComponents.CONSUMABLE,Consumables.DEFAULT_DRINK), false,true));
	public static final DeferredBlock<BeverageBlock> MUG = baseblock("beverage_mug", b -> new BeverageBlock(getBProps(b),BeverageBlock.MUG_SHAPE), (b,r) -> new BeverageItem(r,()->CVFluids.mixedf.get(), b.craftRemainder(new ItemStackTemplate(CVItems.GLASS_MUG)).component(DataComponents.USE_REMAINDER, new UseRemainder(new ItemStackTemplate(CVItems.GLASS_MUG))).component(DataComponents.CONSUMABLE,Consumables.DEFAULT_DRINK), false,true));
	public static final List<DeferredBlock<BeverageBlock>> BEVERAGE_BLOCKS=List.of(BEVERAGE,BOWL,CUP,JUG,MUG); 
	
	public static final DeferredBlock<CogCageBlock> cage = baseblock("cage_wheel", b -> new CogCageBlock(getKineticProps(b)));
	public static final DeferredBlock<CogCageBlock> cog = baseblock("cog", b -> new CogCageBlock(getKineticProps(b)));
	public static final DeferredBlock<AeolipileBlock> aeolipile = baseblock("aeolipile", b -> new AeolipileBlock(getKineticProps(b)));
	public static final DeferredBlock<PlatterBlock> platter = baseblock("fruit_platter", b -> new PlatterBlock(getKineticProps(b)));
	public static final DeferredBlock<WhiskBlock> whisk = baseblock("whisk", b -> new WhiskBlock(getKineticProps(b)));
	public static final DeferredBlock<PamBlock> pam = baseblock("pestle_and_mortar", b -> new PamBlock(getKineticProps(b)));
	public static final DeferredBlock<BasinBlock> basin = baseblock("basin", b -> new BasinBlock(getKineticProps(b)));
	public static final DeferredBlock<BasinBlock> lead_basin = baseblock("lead_basin", b -> new BasinBlock(b.sound(SoundType.METAL)
		.strength(3.5f, 10).noOcclusion()));
	public static final DeferredBlock<WolfFountainBlock> wolf_fountain = baseblock("wolf_fountain", b -> new WolfFountainBlock(getKineticProps(b)));

	public static final List<DeferredBlock<Block>> aqueducts = new ArrayList<>();
	public static final List<DeferredBlock<Block>> aqueduct_mains = new ArrayList<>();
	public static final DeferredBlock<CamelliaFlowerBlock> CAMELLIA_FLOWER = baseblock("camellia_product",
		b -> new CamelliaFlowerBlock(b.mapColor(MapColor.PLANT).replaceable().noCollision()
			.instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).ignitedByLava()
			.pushReaction(PushReaction.DESTROY)));
	public static final DeferredBlock<CamelliaBlock> CAMELLIA = baseblock("camellia_plant", b -> new CamelliaBlock(b.mapColor(MapColor.WOOD)
		.strength(2.0F).noOcclusion().sound(SoundType.WOOD)));
	public static final DeferredBlock<BeverageVendingBlock> BEVERAGE_VENDING_MACHINE = baseblock("beverage_vending_machine",
		b -> new BeverageVendingBlock(b.mapColor(MapColor.COLOR_ORANGE)
			.strength(2.0F).noOcclusion().sound(SoundType.STONE)));
	public static final DeferredBlock<SorbetBlock> FLAT_BREAD = baseblock("flatbread",
		b -> new SorbetBlock(getSProps(b)),(o,t)->new SorbetItem(t, null, o.food(new FoodProperties.Builder().nutrition(3).saturationModifier(0.6f).build()), true));
	public static final List<Block> beverage = new ArrayList<>();
	public static final List<Block> sorbets = new ArrayList<>();
	static {
		for (String s : new String[] { "felsic_tuff", "stone", "sandstone" }) {
			aqueducts.add(baseblock(s + "_aqueduct", b -> new AqueductBlock(getKineticProps(b))));
			aqueduct_mains.add(baseblock(s + "_aqueduct_wavemaker", b -> new AqueductControllerBlock(getKineticProps(b))));
		}
		for(String s:CVFluids.sorbets) {
			baseblock(s+"_sorbet",b -> new SorbetBlock(getSProps(b)),(b,t)->new SorbetItem(t,Lazy.of(()->BuiltInRegistries.FLUID.getValue(CVMain.rl(s+"_sorbet"))), b.craftRemainder(CVBlocks.FLAT_BREAD.get().asItem()).food(new FoodProperties.Builder().nutrition(3).saturationModifier(0.6f).build()), false));
		}
	}

	// register any block to registry
	static <T extends Block> DeferredBlock<T> baseblock(String name, Function<Properties,T> bl) {
		DeferredBlock<T> blx = BLOCKS.registerBlock(name, bl);
		CVItems.ITEMS.registerItem(name, t -> new CPBlockItem(blx.get(), t, CVMain.MAIN_TAB));
		return blx;
	}

	// register any block to registry with custom item factory
	static <T extends Block> DeferredBlock<T> baseblock(String name, Function<Properties,T> bl, BiFunction<Item.Properties, T, Item> toitem) {
		DeferredBlock<T> blx = BLOCKS.registerBlock(name, bl);
		CVItems.ITEMS.registerItem(name, t -> toitem.apply(t,blx.get()));
		return blx;
	}

	// register basic block to registry
	static DeferredBlock<Block> block(String name) {
		DeferredBlock<Block> blx = BLOCKS.registerSimpleBlock(name);
		CVItems.ITEMS.registerItem(name, t -> new CPBlockItem(blx.get(), t, CVMain.MAIN_TAB));
		return blx;
	}

	private static Properties getKineticProps(Properties p) {
		return p.sound(SoundType.STONE)
			.strength(3.5f, 10).noOcclusion();
	}

	private static Properties getBProps(Properties p) {
		return p.sound(SoundType.GLASS)
			.strength(3.5f, 10).noOcclusion().instabreak().isViewBlocking(CVBlocks::isntSolid);
	}
	private static Properties getSProps(Properties p) {
		return p.sound(SoundType.WOOL)
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