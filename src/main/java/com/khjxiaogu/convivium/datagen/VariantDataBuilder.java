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

package com.khjxiaogu.convivium.datagen;

import java.util.HashMap;
import java.util.Map;

import com.khjxiaogu.convivium.util.Constants;

public class VariantDataBuilder<T> {
	protected Map<String,Float> variantData=new HashMap<>();
	private T par;
	public VariantDataBuilder(T par) {
		super();
		this.par = par;
	}
	public VariantDataBuilder<T> val(String k,float v){
		variantData.put(k, v);
		return this;
	}
	public VariantDataBuilder<T> sweetness(float v){
		return val(Constants.SWEETNESS,v);
	}
	public VariantDataBuilder<T> astringency(float v){
		return val(Constants.ASTRINGENCY,v);
	}
	public VariantDataBuilder<T> pungency(float v){
		return val(Constants.PUNGENCY,v);
	}
	public VariantDataBuilder<T> soothingness(float v){
		return val(Constants.SOOTHINGNESS,v);
	}
	public VariantDataBuilder<T> thickness(float v){
		return val(Constants.THICKNESS,v);
	}
	public T end() {
		return par;
	};
}
