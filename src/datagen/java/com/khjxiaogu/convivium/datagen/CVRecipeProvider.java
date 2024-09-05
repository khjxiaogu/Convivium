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

import static com.khjxiaogu.convivium.CVTags.Items.*;
import static com.khjxiaogu.convivium.util.Constants.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.khjxiaogu.convivium.CVBlocks;
import com.khjxiaogu.convivium.CVFluids;
import com.khjxiaogu.convivium.CVItems;
import com.khjxiaogu.convivium.CVMain;
import com.khjxiaogu.convivium.data.recipes.BasinRecipe;
import com.khjxiaogu.convivium.data.recipes.ContainingRecipe;
import com.khjxiaogu.convivium.data.recipes.ConvertionRecipe;
import com.khjxiaogu.convivium.data.recipes.RelishFluidRecipe;
import com.khjxiaogu.convivium.data.recipes.RelishItemRecipe;
import com.khjxiaogu.convivium.data.recipes.RelishRecipe;
import com.khjxiaogu.convivium.util.Constants;
import com.khjxiaogu.convivium.util.FloatSizedOrCatalystIngredient;
import com.teammoeg.caupona.CPMain;
import com.teammoeg.caupona.data.IDataRecipe;
import com.teammoeg.caupona.util.SizedOrCatalystFluidIngredient;
import com.teammoeg.caupona.util.SizedOrCatalystIngredient;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidStack;

public class CVRecipeProvider extends RecipeProvider {
	private final HashMap<String, Integer> PATH_COUNT = new HashMap<>();

	static final Fluid water = fluid(mrl("nail_soup")), milk = fluid(mrl("scalded_milk")), stock = fluid(mrl("stock"));
	public static List<IDataRecipe> recipes = new ArrayList<>();

	public CVRecipeProvider(DataGenerator generatorIn, CompletableFuture<HolderLookup.Provider> future) {
		super(generatorIn.getPackOutput(), future);
	}

	@Override
	protected void buildRecipes(RecipeOutput out) {
		// out.accept(new
		// GrindingRecipe(rl("grinding/pome"),List.of(Pair.of(Ingredient.of(Items.APPLE),2)),
		// null, 0f, new FluidStack(Fluids.WATER,250),new
		// FluidStack(CVFluids.pjuicef.get(),250),List.of(new
		// ItemStack(cpitem("scraps"))), 200, false));
		relish(out, Constants.TEA, "#7eb3c2", CVFluids.teaf.get());
		relish(out, Constants.MILK, "#dac381", NeoForgeMod.MILK.get());
		relish(out, Constants.COCOA, "#ea9359", CVFluids.cocoaf.get());
		relish(out, Constants.WATER, "#886a51", Fluids.WATER);
		relish(out, Constants.JUICE, "#aac35d", CVFluids.bjuicef.get(), CVFluids.djuicef.get(), CVFluids.pjuicef.get());
		relish(out, Constants.WINE, "#ce6c71", CVFluids.bwinef.get(), CVFluids.dwinef.get(), CVFluids.pwinef.get());
		relish(out, Constants.NONE, "#ffffff");
		/*
		 * createME("night_vision") .major(Constants.TEA)
		 * .local(Constants.SWEETNESS_DELTA,0)
		 * .local(Constants.ASTRINGENCY_DELTA,"3-"+Constants.ASTRINGENCY)
		 * .local(Constants.DISPLAY,Constants.ASTRINGENCY+"+0.1")
		 * .local(Constants.THICKNESS_DELTA, 0) .local(Constants.PUNGENCY_DELTA,0)
		 * .local(Constants.SOOTHINGNESS_DELTA, 0)
		 * .effect(MobEffects.NIGHT_VISION).amp("1").time("100").compare(Constants.
		 * ASTRINGENCY,GT.C,"1.99").next() .end(out);
		 */
		/*
		 * createME("strength").major(Constants.TEA)
		 * .local(Constants.SWEETNESS_DELTA,0).local(Constants.ASTRINGENCY_DELTA,"-("+
		 * Constants.ASTRINGENCY+"+3)" ).local(Constants.DISPLAY,
		 * "-"+Constants.ASTRINGENCY+"+0.01").local(Constants.THICKNESS_DELTA,
		 * 0).local(Constants.PUNGENCY_DELTA,0).local(Constants.SOOTHINGNESS_DELTA, 0)
		 * .effect(MobEffects.DAMAGE_BOOST).amp("1").time("100").compare(Constants.
		 * ASTRINGENCY,LT.C,"-1.99").next() .end(out);
		 */
		/*
		 * createME("saturation").major(Constants.WATER)
		 * .local(Constants.SWEETNESS_DELTA,3) .local(Constants.ASTRINGENCY_DELTA, 3)
		 * .local(Constants.DISPLAY, 5) .local(Constants.THICKNESS_DELTA, 0)
		 * .local(Constants.PUNGENCY_DELTA,0) .local(Constants.SOOTHINGNESS_DELTA, 0)
		 * .effect(MobEffects.SATURATION).amp("1").time("100").compare("1",GT.C,"3").
		 * next() .end(out);
		 */
		/*
		 * createME("saturation").major(Constants.MILK)
		 * .local(Constants.SWEETNESS_DELTA,1).local(Constants.ASTRINGENCY_DELTA,
		 * 3).local(Constants.DISPLAY, 5).local(Constants.THICKNESS_DELTA,
		 * 2).local(Constants.PUNGENCY_DELTA,-2).local(Constants.SOOTHINGNESS_DELTA, 0)
		 * .effect(MobEffects.SATURATION).amp("1").time("100").compare("3",GT.C,"1").
		 * next() .end(out);
		 */
		out.accept(rl("bottle/beverage"), new ContainingRecipe(CVBlocks.BEVERAGE.get().asItem(), CVFluids.mixedf.get()), null);
		for (String s : CVItems.base_drinks) {
			if (s.equals("milk")) {
				out.accept(rl("bottle/" + s), new ContainingRecipe(cvitem(s), NeoForgeMod.MILK.get()), null);
			} else if (s.equals("water")) {
				out.accept(rl("bottle/" + s), new ContainingRecipe(cvitem(s), Fluids.WATER), null);
			} else
				out.accept(rl("bottle/" + s), new ContainingRecipe(cvitem(s), cvfluid(s)), null);
		}
		// taste(Items.APPLE).vars().astringency(2).end().end(out);
		out.accept(rl("convertion/tea"),
			new ConvertionRecipe(List.of(FloatSizedOrCatalystIngredient.of(cvitem("powdered_tea"), 1f)), new FluidStack(Fluids.WATER, 250), cvfluid("tea", 250), 60, 200, false), null);
		out.accept(rl("convertion/cocoa_from_water"),
			new ConvertionRecipe(List.of(FloatSizedOrCatalystIngredient.of(cvitem("cocoa_powder"), 1f)), new FluidStack(Fluids.WATER, 250), cvfluid("hot_chocolate", 250), 40, 200, false), null);
		out.accept(rl("convertion/cocoa_from_milk"),
			new ConvertionRecipe(List.of(FloatSizedOrCatalystIngredient.of(cvitem("cocoa_powder"), 1f)), new FluidStack(NeoForgeMod.MILK.get(), 250), cvfluid("hot_chocolate", 250), 40, 200, false),
			null);
		//out.accept(rl("relish_item/cocoa"), new RelishItemRecipe(Ingredient.of(cvitem("cocoa_powder")), Constants.COCOA), null);
		//out.accept(rl("relish_item/tea"), new RelishItemRecipe(Ingredient.of(cvitem("powdered_tea")), Constants.TEA), null);
		for (String s : List.of("pome", "drupe", "berry"))
			out.accept(rl("basin/" + s + "_juice_to_sapa"), new BasinRecipe(SizedOrCatalystFluidIngredient.of(cvfluid(s + "_juice"), 250), SizedOrCatalystIngredient.of(Items.FLOWER_POT, 1),
				List.of(new ItemStack(cpitem("sapa_spice_jar"))), 1, 200, true), null);
		for (String s : List.of("pome", "drupe", "berry"))
			out.accept(rl("convertion/" + s + "_juice_from_must"), new ConvertionRecipe(List.of(), cvfluid(s + "_must", 250), cvfluid(s + "_juice", 250), 40, 200, false), null);
		for (String s : CVFluids.intern.keySet()) {
			out.accept(rl("bottle/" + s), new ContainingRecipe(cvitem(s), cvfluid(s)), null);
		}
		type("hot_chocolate").has(Constants.COCOA).canContains(SPICE).canContains(SWEET).time(200).end(out);

		type("mulled_wine").has(WINE).allow(WATER).mustContains(SPICE).canContains(FRUIT).canContains(SWEET).priority(100).time(200).end(out);
		type("jaegertee").has(WINE).and().has(TEA).allow(WATER).mustContains(SPICE).canContains(SWEET).canContains(FRUIT).priority(100).time(200).end(out);
		type("posca").has(WINE).and().has(JUICE).allow(WATER).canContains(FRUIT).priority(100).time(200).end(out);
		type("leicha").has(TEA).allow(WATER).mustContains(NUTS).priority(100).time(200).end(out);
		type("te_mocha").has(TEA).and().has(MILK).and().has(COCOA).allow(WATER).canContains(SWEET).priority(100).time(200).end(out);
		type("kahwa_tea").has(TEA).allow(WATER).mustContains(NUTS).mustContains(SPICE).priority(100).time(200).end(out);
		type("saidi_tea").has(TEA).mustContains(SWEET).priority(100).time(200).end(out);
		type("milk_tea").has(MILK).and().has(TEA).allow(WATER).canContains(SWEET).canContains(NUTS).canContains(SPICE).priority(100).time(200).end(out);
		type("sweet_tea").has(TEA).allow(WATER).mustContains(SWEET).canContains(FRUIT).priority(100).time(200).end(out);
		type("fruit_tongsui").has(WATER).mustContains(FRUIT).canContains(SWEET).priority(100).time(200).end(out);
		type("ade").has(JUICE).and().has(WATER).canContains(FRUIT).canContains(SWEET).priority(100).time(200).end(out);
		type("punch").has(WATER).and().has(JUICE).allow(WINE).mustContains(SWEET).canContains(SPICE).priority(100).time(200).end(out);
		type("syllabub").has(MILK).and().has(JUICE).allow(WATER).canContains(SPICE).canContains(SWEET).priority(100).time(200).end(out);
		type("posset").has(WINE).and().has(MILK).allow(WATER).canContains(SPICE).canContains(SWEET).priority(100).time(200).end(out);
		type("chocolate_tea").has(COCOA).and().has(TEA).allow(WATER).canContains(SPICE).canContains(SWEET).priority(100).time(200).end(out);
		type("cocoa_wine").has(COCOA).and().has(WINE).allow(WATER).canContains(SPICE).canContains(SWEET).priority(100).time(200).end(out);
		type("chocolate_milk").has(COCOA).and().has(MILK).allow(WATER).canContains(SPICE).canContains(SWEET).canContains(NUTS).priority(100).time(200).end(out);
	}

	private TasteRecipeBuilder taste(Item it) {
		return new TasteRecipeBuilder(rl("taste/" + Utils.getRegistryName(it).getNamespace() + "/" + Utils.getRegistryName(it).getPath())).item(Ingredient.of(it));
	}

	private TasteRecipeBuilder taste(TagKey<Item> it) {
		return new TasteRecipeBuilder(rl("taste/" + it.location().getNamespace() + "/" + it.location().getPath())).item(Ingredient.of(it));
	}

	private SwayRecipeBuilder create(String name, String icon) {
		return new SwayRecipeBuilder(rl("sway_effect/" + name), ResourceLocation.parse(icon));
	}

	private TypeRecipeBuilder type(String name) {
		return new TypeRecipeBuilder(rl("beverage_type/" + name), cvfluid(name));
	}

	private TypeRecipeBuilder type(ResourceLocation rl) {
		return new TypeRecipeBuilder(rl("beverage_type/" + rl.getPath()), fluid(rl));
	}

	private SwayRecipeBuilder createME(String name) {
		return new SwayRecipeBuilder(rl("sway_effect/" + name), ResourceLocation.withDefaultNamespace("mob_effect/" + name));
	}

	private Fluid cvfluid(String name) {
		return BuiltInRegistries.FLUID.get(ResourceLocation.fromNamespaceAndPath(CVMain.MODID, name));
	}

	private FluidStack cvfluid(String name, int amt) {
		return new FluidStack(BuiltInRegistries.FLUID.get(ResourceLocation.fromNamespaceAndPath(CVMain.MODID, name)), amt);
	}

	private void relish(RecipeOutput out, String name, String clr, Fluid... fs) {
		out.accept(rl("relish/" + name), new RelishRecipe(name, ResourceLocation.fromNamespaceAndPath(CVMain.MODID, "relish/" + name), clr), null);
		for (Fluid f : fs) {
			out.accept(rl("relish_fluid/" + Utils.getRegistryName(f).getPath()), new RelishFluidRecipe(f, name), null);
			type(Utils.getRegistryName(f)).has(f).canContains(SPICE).canContains(SWEET).time(200).end(out);

		}
	}

	private Item cvitem(String name) {
		return BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(CVMain.MODID, name));
	}

	private Item cpitem(String name) {
		return BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(CPMain.MODID, name));
	}

	private Item mitem(String name) {
		return BuiltInRegistries.ITEM.get(ResourceLocation.withDefaultNamespace(name));
	}

	private Item item(ResourceLocation rl) {
		return BuiltInRegistries.ITEM.get(rl);
	}

	private static Fluid fluid(ResourceLocation rl) {
		return BuiltInRegistries.FLUID.get(rl);
	}

	private static ResourceLocation mrl(String s) {
		return ResourceLocation.fromNamespaceAndPath(CVMain.MODID, s);
	}

	private ResourceLocation ftag(String s) {
		return ResourceLocation.fromNamespaceAndPath("c", s);
	}

	private ResourceLocation mcrl(String s) {
		return ResourceLocation.withDefaultNamespace(s);
	}

	private ResourceLocation rl(String s) {
		if (!s.contains("/"))
			s = "crafting/" + s;
		if (PATH_COUNT.containsKey(s)) {
			int count = PATH_COUNT.get(s) + 1;
			PATH_COUNT.put(s, count);
			return ResourceLocation.fromNamespaceAndPath(CVMain.MODID, s + count);
		}
		PATH_COUNT.put(s, 1);
		return ResourceLocation.fromNamespaceAndPath(CVMain.MODID, s);
	}

}
