package com.khjxiaogu.convivium.util.evaluator;

import java.util.HashMap;
import java.util.Map;

import com.khjxiaogu.convivium.data.recipes.numbers.INumber;

public class VEnvironment implements IEnvironment{
	Map<String,Double> map;
	Map<String,INumber> exp;
	IEnvironment parent;
	public VEnvironment() {
		super();
		map=new HashMap<>();
	}




	public VEnvironment(IEnvironment parent, Map<String, INumber> exp) {
		super();
		this.parent = parent;
		this.map = new HashMap<>();
		this.exp = exp;
	}




	public VEnvironment(IEnvironment parent) {
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
				double res=num.apply(this);
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
