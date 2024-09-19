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

package com.khjxiaogu.convivium.data.recipes.relishcondition;

import com.khjxiaogu.convivium.util.BeveragePendingContext;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teammoeg.caupona.data.TranslationProvider;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.Fluid;

public class OnlyFluidCondition implements RelishCondition {
	public static final MapCodec<OnlyFluidCondition> CODEC=RecordCodecBuilder.mapCodec(t->t.group(
		BuiltInRegistries.FLUID.byNameCodec().fieldOf("relish").forGetter(o->o.f))
		.apply(t, OnlyFluidCondition::new));
	Fluid f;
	public OnlyFluidCondition(Fluid relish) {
		f=relish;
	}

	@Override
	public boolean test(BeveragePendingContext t) {
		return t.relishFluids.stream().allMatch(f::isSame);
	}

	@Override
	public String getTranslation(TranslationProvider p) {
		return p.getTranslation("recipe.convivium.relish_cond.only_fluid",f.getFluidType().getDescription());
	}


}
