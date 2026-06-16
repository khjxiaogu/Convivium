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

package com.khjxiaogu.convivium.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import com.khjxiaogu.convivium.data.recipes.RelishFluidRecipe;
import com.khjxiaogu.convivium.data.recipes.RelishRecipe;
import com.khjxiaogu.convivium.data.recipes.SwayRecipe;
import com.khjxiaogu.convivium.data.recipes.TasteRecipe;
import com.khjxiaogu.convivium.data.recipes.relishcondition.RelishCondition;
import com.khjxiaogu.convivium.util.evaluator.ConstantEnvironment;
import com.khjxiaogu.convivium.util.evaluator.VariantEnvironment;
import com.mojang.datafixers.util.Pair;
import com.teammoeg.caupona.data.recipes.IPendingContext;
import com.teammoeg.caupona.util.ChancedEffect;
import com.teammoeg.caupona.util.FloatemStack;
import com.teammoeg.caupona.util.FloatemTagStack;
import com.teammoeg.caupona.util.ResultCachingMap;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.material.Fluid;

public class BeveragePendingContext extends IPendingContext {
	private ResultCachingMap<RelishCondition, Boolean> relish = new ResultCachingMap<>(e -> e.test(this));
	public Map<String, Integer> relishes = new HashMap<>();
	public List<Fluid> relishFluids = new ArrayList<>(5);
	public List<String> activerelish = new ArrayList<>();
	public ConstantEnvironment taste;

	public BeveragePendingContext(BeverageInfo info) {
		items = new ArrayList<>(info.stacks.size());
		Object2DoubleOpenHashMap<String> variant = new Object2DoubleOpenHashMap<String>();

		int cnt = info.getRelishCount();

		for (Fluid s : info.relishes) {
			if (s == null) continue;
			relishFluids.add(s);
			RecipeHolder<RelishFluidRecipe> rfr = RelishFluidRecipe.recipes.get(s);
			if (rfr != null) {
				relishes.merge(rfr.value().relish, 1,SUtils.INT_SUM);
				rfr.value().variantData.forEach((e, d) -> {
					variant.mergeDouble(e,d*1d / cnt,SUtils.DBL_SUM);
				});
			}
		}
		checkActiveRelish();
		info.activeRelish1 = "";
		info.activeRelish2 = "";
		if (activerelish.size() > 0) {
			if (activerelish.size() > 1)
				info.activeRelish2 = activerelish.get(1);
			info.activeRelish1 = activerelish.get(0);
		}
		for (FloatemStack fs : info.stacks) {
			Object2DoubleOpenHashMap<String> lvar=new Object2DoubleOpenHashMap<String>();
			items.add(new FloatemTagStack(fs));
			for (RecipeHolder<TasteRecipe> recipe : TasteRecipe.recipes) {
				if (recipe.value().item.test(fs.getStack())) {
					recipe.value().variantData.forEach((e, f) -> {
						lvar.mergeDouble(e,(double)f,SUtils.DBL_SUM);
					});
					break;
				}
			}
			totalItems += fs.getCount();
			for(Object2DoubleMap.Entry<String> p:lvar.object2DoubleEntrySet()) {
				variant.mergeDouble(p.getKey(),30d*p.getDoubleValue()*Mth.sin(Math.min(fs.getCount(), 30)*Mth.PI/60)/Mth.PI,SUtils.DBL_SUM);
			}
			
		}
		for (String rel : activerelish) {

			RecipeHolder<RelishRecipe> rr1 = RelishRecipe.recipes.get(rel);
			if (rr1 != null) {
				rr1.value().variantData.forEach((e, d) -> {
					variant.mergeDouble(e,d.doubleValue(),SUtils.DBL_SUM);
				});
			}
		}
		taste = new ConstantEnvironment(variant);
	}

	public void checkActiveRelish() {
		int max = relishes.values().stream().mapToInt(t -> t).max().orElse(0);
		if (max == 1 && relishes.size() > 1) {
			return;
		}
		for (Entry<String, Integer> i : relishes.entrySet()) {
			if (i.getValue() == max) {
				activerelish.add(i.getKey());
			}
		}
	}

	public List<CurrentSwayInfo> handleSway(BeverageInfo info) {
		info.swayeffects.clear();
		List<CurrentSwayInfo> swi = SwayRecipe.recipes.stream().map(t -> t.value()).map(this::handleSwayRecipe).flatMap(Optional::stream)
			.map(t -> {
				for(MobEffectInstance me:t.getFirst())
					info.swayeffects.add(new ChancedEffect(me,1f));
				return t.getSecond();
			})
			.flatMap(Optional::stream)
			.sorted((t2, t1) -> Mth.ceil(t1.display - t2.display))
			.collect(Collectors.toList());
		info.swayeffects.sort(
			Comparator.<ChancedEffect, String>comparing(e -> e.effect.getEffect().getRegisteredName())
			.thenComparing(e -> e.chance));
		info.recalculateHAS();
		return swi;
	}

	public List<CurrentSwayInfo> getSwayHint() {
		return SwayRecipe.recipes.stream().map(RecipeHolder::value).map(this::handleSwayHint).flatMap(Optional::stream)
			.sorted((t2, t1) -> Mth.ceil(t1.display - t2.display))
			.collect(Collectors.toList());
	}

	public Optional<CurrentSwayInfo> handleSwayHint(SwayRecipe sway) {
		if (sway.canApply(this)) {
			VariantEnvironment env = new VariantEnvironment(taste, sway.locals);
			CurrentSwayInfo csi = new CurrentSwayInfo(sway.icon, env);
			if (sway.hasEffects(env))
				csi.active = 1;
			return csi.toOptional();
		}
		return Optional.empty();
	}

	public Optional<Pair<List<MobEffectInstance>, Optional<CurrentSwayInfo>>> handleSwayRecipe(SwayRecipe sway) {
		if (sway.canApply(this)) {
			VariantEnvironment env = new VariantEnvironment(taste, sway.locals);

			CurrentSwayInfo csi = new CurrentSwayInfo(sway.icon, env);
			Pair<Boolean, List<MobEffectInstance>> effs = sway.getEffects(env);
			if (effs.getFirst()) {
				csi.active = 1;
			}
			return Optional.of(Pair.of(effs.getSecond(), csi.toOptional()));
		}
		return Optional.empty();
	}

	public boolean compute(RelishCondition cond) {
		return relish.compute(cond);
	}

}
