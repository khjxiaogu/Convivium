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

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;

public class EQ extends Compare {
	public static final Compare C=new EQ();
	@Override
	public boolean test(Float t1, Float num) {
		return Float.compare(t1!=null?t1:0, num!=0?num:0)==0;
	}

	public EQ() {

	}

	public EQ(FriendlyByteBuf num) {
		super(num);
	}

	public EQ(JsonObject num) {
		super(num);
	}

	@Override
	public String getType() {
		return "equals";
	}

}
