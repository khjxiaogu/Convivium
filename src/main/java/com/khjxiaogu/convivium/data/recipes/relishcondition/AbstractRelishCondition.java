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

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;

public abstract class AbstractRelishCondition implements RelishCondition{
	protected String relish;
	@Override
	public JsonElement serialize() {
		JsonObject jo=new JsonObject();
		jo.addProperty("relish", relish);
		jo.addProperty("type", getType());
		return jo;
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeUtf(relish);
	}
	

	public AbstractRelishCondition(FriendlyByteBuf buffer) {
		this(buffer.readUtf());
	}
	public AbstractRelishCondition(JsonObject json) {
		this(GsonHelper.getAsString(json,"relish"));
	}
	public AbstractRelishCondition(String relish) {
		super();
		this.relish = relish;
	}
}
