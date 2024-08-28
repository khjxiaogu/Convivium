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

public class OrRelishCondition extends LogicalRelishCondition {
	public static final MapCodec<OrRelishCondition> CODEC=RecordCodecBuilder.mapCodec(t->codecStart(t).apply(t, OrRelishCondition::new));
	@Override
	public String getType() {
		return "or";
	}

	@Override
	public boolean test(BeveragePendingContext t) {
		return r1.test(t)||r2.test(t);
	}


	public OrRelishCondition(RelishCondition r1, RelishCondition r2) {
		super(r1, r2);
	}



}
