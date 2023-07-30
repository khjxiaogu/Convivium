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
import com.khjxiaogu.convivium.util.BeveragePendingContext;
import com.teammoeg.caupona.data.TranslationProvider;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;

public class HasFluidCondition implements RelishCondition {
	Fluid f;
	@Override
	public JsonElement serialize() {
		JsonObject jo=new JsonObject();
		jo.addProperty("relish", Utils.getRegistryName(f).toString());
		jo.addProperty("type", getType());
		return jo;
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeRegistryIdUnsafe(ForgeRegistries.FLUIDS, f);
	}
	

	public HasFluidCondition(FriendlyByteBuf buffer) {
		this(buffer.readRegistryIdUnsafe(ForgeRegistries.FLUIDS));
	}

	public HasFluidCondition(JsonObject json) {
		this(ForgeRegistries.FLUIDS.getValue(new ResourceLocation(GsonHelper.getAsString(json,"relish"))));
	}

	public HasFluidCondition(Fluid relish) {
		this.f=relish;
	}

	@Override
	public boolean test(BeveragePendingContext t) {
		return t.relishFluids.contains(f);
	}

	@Override
	public String getTranslation(TranslationProvider p) {
		return p.getTranslation("recipe.convivium.relish_cond.contains_fluid",f.getFluidType().getDescription());
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return "contains_fluid";
	}

}
