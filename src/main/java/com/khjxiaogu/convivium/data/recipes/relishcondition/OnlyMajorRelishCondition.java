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

import com.google.gson.JsonObject;
import com.khjxiaogu.convivium.data.recipes.RelishRecipe;
import com.khjxiaogu.convivium.util.BeveragePendingContext;
import com.teammoeg.caupona.data.TranslationProvider;

import net.minecraft.network.FriendlyByteBuf;

public class OnlyMajorRelishCondition extends AbstractRelishCondition {

	public OnlyMajorRelishCondition(FriendlyByteBuf buffer) {
		super(buffer);
	}

	public OnlyMajorRelishCondition(JsonObject json) {
		super(json);
	}

	public OnlyMajorRelishCondition(String relish) {
		super(relish);
	}

	@Override
	public boolean test(BeveragePendingContext t) {
		return t.activerelish.size()==1&&t.activerelish.get(0).equals(relish);
	}

	@Override
	public String getTranslation(TranslationProvider p) {
		return p.getTranslation("recipe.convivium.relish_cond.only_major",RelishRecipe.recipes.get(relish).getText());
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return "only_major";
	}

}
