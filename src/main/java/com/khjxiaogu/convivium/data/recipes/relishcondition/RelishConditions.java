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

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.teammoeg.caupona.data.DataDeserializerRegistry;
import com.teammoeg.caupona.util.SerializeUtil;

public class RelishConditions {
	private static DataDeserializerRegistry<RelishCondition> relishes=new DataDeserializerRegistry<>();
	public static final Codec<RelishCondition> CODEC=relishes.createCodec();
	static {
		register("major", MajorRelishCondition.class, MajorRelishCondition.CODEC);
		register("only_major", OnlyMajorRelishCondition.class, OnlyMajorRelishCondition.CODEC);
		register("compare", RelishCompareCondition.class, RelishCompareCondition.CODEC);
		register("contains",HasRelishCondition.class, HasRelishCondition.CODEC);
		register("contains_fluid",HasFluidCondition.class, HasFluidCondition.CODEC);
		
		
		register("and", AndRelishCondition.class, AndRelishCondition.CODEC);
		register("or", OrRelishCondition.class, OrRelishCondition.CODEC);
	}
	
	public static <T extends RelishCondition> void register(String name,Class<T> cls,MapCodec<T> codec) {
		relishes.register(name, cls, codec,SerializeUtil.toStreamCodec(codec));
	}

}
