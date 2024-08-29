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

import java.util.HashMap;
import java.util.Map;

import com.mojang.serialization.Codec;

import net.minecraft.util.StringRepresentable;
import net.minecraft.util.StringRepresentable.StringRepresentableCodec;



public class Compares {
	public final static Map<Compare,Comparators> map=new HashMap<>();
	public enum Comparators implements StringRepresentable{
		greater(GT.C),
		equals(EQ.C),
		lesser(LT.C),
		greater_equals(GE.C),
		not_equals(NE.C),
		lesser_equals(LE.C)
		;
		Compare comp;
		
		private Comparators(Compare comp) {
			this.comp = comp;
			map.put(comp, this);
		}

		@Override
		public String getSerializedName() {
			return name();
		}
		
	}
	public static final Codec<Compare> CODEC=new StringRepresentableCodec<>(Comparators.values(), Comparators::valueOf,Comparators::ordinal).xmap(v->v.comp, map::get);


}
