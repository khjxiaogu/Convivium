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

package com.khjxiaogu.convivium.data.recipes.compare;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.minecraft.network.FriendlyByteBuf;

public abstract class Compare implements ICompare {
	@Override
	public abstract boolean test(Float t1,Float num) ;
	public abstract String getType();
	public Compare() {
		super();
	}
	/**
	 * @param num  
	 */
	public Compare(JsonObject num) {
		super();
	}
	/**
	 * @param num  
	 */
	public Compare(FriendlyByteBuf num) {
		super();
	}
	@Override
	public JsonElement serialize() {
		return new JsonPrimitive(getType());
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
	}

}
