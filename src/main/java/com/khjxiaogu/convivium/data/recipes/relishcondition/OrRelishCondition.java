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
import com.khjxiaogu.convivium.util.BeveragePendingContext;

import net.minecraft.network.FriendlyByteBuf;

public class OrRelishCondition extends LogicalRelishCondition {

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return "or";
	}

	@Override
	public boolean test(BeveragePendingContext t) {
		// TODO Auto-generated method stub
		return r1.test(t)||r2.test(t);
	}

	public OrRelishCondition(FriendlyByteBuf pb) {
		super(pb);
		// TODO Auto-generated constructor stub
	}

	public OrRelishCondition(JsonObject jo) {
		super(jo);
		// TODO Auto-generated constructor stub
	}

	public OrRelishCondition(RelishCondition r1, RelishCondition r2) {
		super(r1, r2);
		// TODO Auto-generated constructor stub
	}



}
