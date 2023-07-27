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
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;


import com.google.gson.JsonObject;
import com.khjxiaogu.convivium.CVBlocks;
import com.khjxiaogu.convivium.CVFluids;
import com.khjxiaogu.convivium.CVItems;
import com.khjxiaogu.convivium.CVMain;
import com.khjxiaogu.convivium.data.recipes.BeverageTypeRecipe;
import com.khjxiaogu.convivium.data.recipes.ContainingRecipe;
import com.khjxiaogu.convivium.data.recipes.ConvertionRecipe;
import com.khjxiaogu.convivium.data.recipes.RelishFluidRecipe;
import com.khjxiaogu.convivium.data.recipes.RelishRecipe;
import com.khjxiaogu.convivium.data.recipes.compare.GT;
import com.khjxiaogu.convivium.data.recipes.compare.LT;
import com.khjxiaogu.convivium.data.recipes.relishcondition.OnlyMajorRelishCondition;
import com.khjxiaogu.convivium.util.Constants;
import com.mojang.datafixers.util.Pair;
import com.teammoeg.caupona.CPMain;
import com.teammoeg.caupona.data.IDataRecipe;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.ForgeRegistries;

public class CVRecipeProvider extends RecipeProvider {
	private final HashMap<String, Integer> PATH_COUNT = new HashMap<>();

	static final Fluid water = fluid(mrl("nail_soup")), milk = fluid(mrl("scalded_milk")), stock = fluid(mrl("stock"));
	public static List<IDataRecipe> recipes = new ArrayList<>();

	public CVRecipeProvider(DataGenerator generatorIn) {
		super(generatorIn.getPackOutput());
	}

	@Override
	protected void buildRecipes(Consumer<FinishedRecipe> outx) {
		Consumer<IDataRecipe> out = r -> {
			outx.accept(new FinishedRecipe() {

				@Override
				public void serializeRecipeData(JsonObject jo) {
					r.serializeRecipeData(jo);
				}

				@Override
				public ResourceLocation getId() {
					return r.getId();
				}

				@Override
				public JsonObject serializeAdvancement() {
					return null;
				}

				@Override
				public ResourceLocation getAdvancementId() {
					return null;
				}

				@Override
				public RecipeSerializer<?> getType() {
					return r.getSerializer();
				}

			});
		};
		//out.accept(new GrindingRecipe(rl("grinding/magma"),List.of(Pair.of(Ingredient.of(Items.MAGMA_CREAM),2),Pair.of(Ingredient.of(cvitem("dolium_lid")),1)), null, 0f, new FluidStack(Fluids.WATER,500),new FluidStack(Fluids.LAVA,500),List.of(new ItemStack(Items.SLIME_BALL,1),new ItemStack(cpitem("soot"),5)), 200, false));
		relish(out,"bath","#7eb3c2",CVFluids.teaf.get());
		relish(out,"cereal","#dac381",ForgeMod.MILK.get());
		relish(out,"fire","#ea9359",CVFluids.cocoaf.get());
		relish(out,"hearth","#886a51",Fluids.WATER);
		relish(out,"seasons","#aac35d",CVFluids.bjuicef.get(),CVFluids.djuicef.get(),CVFluids.pjuicef.get());
		relish(out,"wine","#ce6c71",CVFluids.bwinef.get(),CVFluids.dwinef.get(),CVFluids.pwinef.get());
		relish(out,"none","#ffffff");
		createME("night_vision").major("bath")
		.local(Constants.SWEETNESS_DELTA,0).local(Constants.ASTRINGENCY_DELTA,"2-"+Constants.ASTRINGENCY).local(Constants.DISPLAY,Constants.ASTRINGENCY+"+0.1").local(Constants.THICKNESS_DELTA, 0).local(Constants.PUNGENCY_DELTA,0).local(Constants.SOOTHINGNESS_DELTA, 0)
			.effect(MobEffects.NIGHT_VISION).amp("1").time("100").compare(Constants.ASTRINGENCY,GT.C,"1.99").next()
		.end(out);
		createME("strength").major("bath")
		.local(Constants.SWEETNESS_DELTA,0).local(Constants.ASTRINGENCY_DELTA,"-("+Constants.ASTRINGENCY+"+2)" ).local(Constants.DISPLAY, "-"+Constants.ASTRINGENCY+"+0.1").local(Constants.THICKNESS_DELTA, 0).local(Constants.PUNGENCY_DELTA,0).local(Constants.SOOTHINGNESS_DELTA, 0)
			.effect(MobEffects.NIGHT_VISION).amp("1").time("100").compare(Constants.ASTRINGENCY,LT.C,"-1.99").next()
		.end(out);
		createME("saturation").major("hearth")
		.local(Constants.SWEETNESS_DELTA,3).local(Constants.ASTRINGENCY_DELTA, 3).local(Constants.DISPLAY, 5).local(Constants.THICKNESS_DELTA, 0).local(Constants.PUNGENCY_DELTA,0).local(Constants.SOOTHINGNESS_DELTA, 0)
			.effect(MobEffects.SATURATION).amp("1").time("100").compare("1",GT.C,"3").next()
		.end(out);
		/*createME("saturation").major("cereal")
		.local(Constants.SWEETNESS_DELTA,1).local(Constants.ASTRINGENCY_DELTA, 3).local(Constants.DISPLAY, 5).local(Constants.THICKNESS_DELTA, 2).local(Constants.PUNGENCY_DELTA,-2).local(Constants.SOOTHINGNESS_DELTA, 0)
			.effect(MobEffects.SATURATION).amp("1").time("100").compare("3",GT.C,"1").next()
		.end(out);*/
		out.accept(new ContainingRecipe(rl("bottle/beverage"),CVBlocks.BEVERAGE.get().asItem(),CVFluids.mixedf.get()));
		for(String s:CVItems.base_drinks) {
			if(s.equals("milk")) {
				out.accept(new ContainingRecipe(rl("bottle/"+s),cvitem(s),ForgeMod.MILK.get()));
			}else if(s.equals("water")) {
				out.accept(new ContainingRecipe(rl("bottle/"+s),cvitem(s),Fluids.WATER));
			}else
				out.accept(new ContainingRecipe(rl("bottle/"+s),cvitem(s),cvfluid(s)));
		}
		taste(Items.APPLE).vars().astringency(2).end().end(out);
		out.accept(new ConvertionRecipe(rl("convertion/tea"),List.of(Pair.of(Ingredient.of(cvitem("powdered_tea")),1f)),Fluids.WATER, cvfluid("tea"), 60, 200, false));
		out.accept(new ConvertionRecipe(rl("convertion/cocoa_from_water"),List.of(Pair.of(Ingredient.of(cvitem("cocoa_powder")),1f)),Fluids.WATER, cvfluid("hot_chocolate"), 40,200, false));
		out.accept(new ConvertionRecipe(rl("convertion/cocoa_from_milk"),List.of(Pair.of(Ingredient.of(cvitem("cocoa_powder")),1f)),ForgeMod.MILK.get(), cvfluid("hot_chocolate"), 40,200, false));
		out.accept(new BeverageTypeRecipe(rl("beverage/tea"),List.of(),List.of(Ingredient.of(cvitem("powdered_tea"))), List.of(new OnlyMajorRelishCondition("bath")), List.of(), 0, 400, 0,CVFluids.teaf.get(), false));
		for(String s:List.of("pome","drupe","berry"))
			out.accept(new ConvertionRecipe(rl("convertion/"+s+"_juice_from_must"),List.of(),cvfluid(s+"_must"), cvfluid(s+"_juice"), 40,200, false));
		for(String s:CVFluids.intern.keySet()) {
			out.accept(new ContainingRecipe(rl("bottle/"+s),cvitem(s),cvfluid(s)));
		}
	}
	private TasteRecipeBuilder taste(Item it) {
		return new TasteRecipeBuilder(rl("taste/"+Utils.getRegistryName(it).getNamespace()+"/"+Utils.getRegistryName(it).getPath())).item(Ingredient.of(it));
	}
	private TasteRecipeBuilder taste(TagKey<Item> it) {
		return new TasteRecipeBuilder(rl("taste/"+it.location().getNamespace()+"/"+it.location().getPath())).item(Ingredient.of(it));
	}
	private SwayRecipeBuilder create(String name,String icon) {
		return new SwayRecipeBuilder(rl("sway_effect/"+name),new ResourceLocation(icon));
	}
	private SwayRecipeBuilder createME(String name) {
		return new SwayRecipeBuilder(rl("sway_effect/"+name),new ResourceLocation("textures/mob_effect/"+name+".png"));
	}
	private Fluid cvfluid(String name) {
		return ForgeRegistries.FLUIDS.getValue(new ResourceLocation(CVMain.MODID, name));
	}
	private void relish(Consumer<IDataRecipe> out,String name,String clr,Fluid... fs) {
		out.accept(new RelishRecipe(rl("relish/"+name), name,new ResourceLocation(CVMain.MODID,"relish/"+name),clr));
		for(Fluid f:fs)
			out.accept(new RelishFluidRecipe(rl("relish_fluid/"+Utils.getRegistryName(f).getPath()), f, name));
	}
	private Item cvitem(String name) {
		return ForgeRegistries.ITEMS.getValue(new ResourceLocation(CVMain.MODID, name));
	}
	private Item cpitem(String name) {
		return ForgeRegistries.ITEMS.getValue(new ResourceLocation(CPMain.MODID, name));
	}

	private Item mitem(String name) {
		return ForgeRegistries.ITEMS.getValue(new ResourceLocation(name));
	}


	private Item item(ResourceLocation rl) {
		return ForgeRegistries.ITEMS.getValue(rl);
	}

	private static Fluid fluid(ResourceLocation rl) {
		return ForgeRegistries.FLUIDS.getValue(rl);
	}

	private static ResourceLocation mrl(String s) {
		return new ResourceLocation(CVMain.MODID, s);
	}

	private ResourceLocation ftag(String s) {
		return new ResourceLocation("forge", s);
	}

	private ResourceLocation mcrl(String s) {
		return new ResourceLocation(s);
	}

	private ResourceLocation rl(String s) {
		if (!s.contains("/"))
			s = "crafting/" + s;
		if (PATH_COUNT.containsKey(s)) {
			int count = PATH_COUNT.get(s) + 1;
			PATH_COUNT.put(s, count);
			return new ResourceLocation(CVMain.MODID, s + count);
		}
		PATH_COUNT.put(s, 1);
		return new ResourceLocation(CVMain.MODID, s);
	}


}
