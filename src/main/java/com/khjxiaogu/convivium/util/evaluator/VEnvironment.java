package com.khjxiaogu.convivium.util.evaluator;

import java.util.HashMap;
import java.util.Map;

public class VEnvironment implements IEnvironment{
	Map<String,Double> map;
	IEnvironment parent;
	public VEnvironment() {
		super();
		map=new HashMap<>();
	}

	public VEnvironment(Map<String, Double> map) {
		super();
		this.map = map;
	}

	public VEnvironment(IEnvironment parent,Map<String, Double> map) {
		super();
		this.map = map;
		this.parent = parent;
	}

	public VEnvironment(IEnvironment parent) {
		this();
		this.parent = parent;
	}

	@Override
	public Double getOptional(String key) {
		Double d=null;
		if(parent!=null)
			d=parent.getOptional(key);
		if(d!=null)return d;
		return map.get(key);
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
