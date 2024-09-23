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

package com.khjxiaogu.convivium.data.recipes.relishcondition;

import com.khjxiaogu.convivium.data.recipes.RelishRecipe;
import com.khjxiaogu.convivium.data.recipes.compare.Compare;
import com.khjxiaogu.convivium.data.recipes.compare.Compares;
import com.khjxiaogu.convivium.util.BeveragePendingContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teammoeg.caupona.data.TranslationProvider;

public class RelishCompareCondition extends AbstractRelishCondition {
	public static final MapCodec<RelishCompareCondition> CODEC=RecordCodecBuilder.mapCodec(t->codecStart(t).and(t.group(
		Compares.CODEC.fieldOf("compare").forGetter(o->o.comp),
		Codec.FLOAT.fieldOf("num").forGetter(o->o.num))).apply(t, RelishCompareCondition::new));
	Compare comp;
	float num;

	public RelishCompareCondition(String relish, Compare comp, float num) {
		super(relish);
		this.comp = comp;
		this.num = num;
	}

	public RelishCompareCondition(String relish) {
		super(relish);
	}

	@Override
	public boolean test(BeveragePendingContext t) {
		return comp.test((Float)(float)t.relishes.getOrDefault(relish,0), num);
	}

	@Override
	public String getTranslation(TranslationProvider p) {
		return p.getTranslation("recipe.convivium.relish_cond.number",RelishRecipe.recipes.get(relish).value().getText());
	}

}
