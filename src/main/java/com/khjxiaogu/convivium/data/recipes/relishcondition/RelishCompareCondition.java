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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.khjxiaogu.convivium.data.recipes.RelishRecipe;
import com.khjxiaogu.convivium.data.recipes.compare.Compare;
import com.khjxiaogu.convivium.data.recipes.compare.Compares;
import com.khjxiaogu.convivium.util.BeveragePendingContext;
import com.teammoeg.caupona.data.TranslationProvider;

import net.minecraft.network.FriendlyByteBuf;

public class RelishCompareCondition extends AbstractRelishCondition {
	Compare comp;
	float num;
	public RelishCompareCondition(FriendlyByteBuf buffer) {
		super(buffer);
		comp=Compares.of(buffer);
		num=buffer.readFloat();
	}

	public RelishCompareCondition(JsonObject json) {
		super(json);
		comp=Compares.of(json.get("compare").getAsJsonObject());
		num=json.get("num").getAsFloat();
	}

	@Override
	public JsonElement serialize() {
		// TODO Auto-generated method stub
		JsonObject jo=super.serialize().getAsJsonObject();
		jo.add("compare", comp.serialize());
		jo.addProperty("num", num);
		return jo;
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		super.write(buffer);
		Compares.write(comp, buffer);
		buffer.writeFloat(num);
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
		return p.getTranslation("recipe.convivium.relish_cond.number",RelishRecipe.recipes.get(relish).getText());
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return "compare";
	}

}
