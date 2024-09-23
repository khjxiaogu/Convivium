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

package com.khjxiaogu.convivium.util.evaluator;

import java.util.HashMap;
import java.util.Map;

import com.khjxiaogu.convivium.data.recipes.numbers.INumber;

public class VariantEnvironment implements IEnvironment{
	Map<String,Double> map;
	Map<String,INumber> exp;
	IEnvironment parent;
	public VariantEnvironment() {
		super();
		map=new HashMap<>();
	}




	public VariantEnvironment(IEnvironment parent, Map<String, INumber> exp) {
		super();
		this.parent = parent;
		this.map = new HashMap<>();
		this.exp = exp;
	}




	public VariantEnvironment(IEnvironment parent) {
		this();
		this.parent = parent;
	}

	@Override
	public Double getOptional(String key) {
		Double d=map.get(key);
		if(d==null) {
			INumber num=exp.get(key);
			if(num!=null) {
				map.put(key, 0D);
				double res=num.applyAsDouble(this);
				map.put(key,res);
				return res;
			}
			if(parent!=null)
				d=parent.getOptional(key);
		}
		return d;
	}

	@Override
	public void set(String key, double v) {
		// TODO Auto-generated method stub
		map.put(key, v);
	}

	@Override
	public double get(String key) {
		Double d=getOptional(key);
		if(d==null)
			return 0;
		return d;
	}
}
