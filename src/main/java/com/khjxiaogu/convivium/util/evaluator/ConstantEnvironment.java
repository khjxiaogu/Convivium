package com.khjxiaogu.convivium.util.evaluator;

import java.util.Map;

public class ConstantEnvironment implements IEnvironment {
	Map<String,Double> map;
	public ConstantEnvironment(Map<String, Double> map) {
		super();
		this.map = map;
	}
	@Override
	public double get(String key) {
		Double d=getOptional(key);
		return d==null?0:d;
	}

	@Override
	public Double getOptional(String key) {
		return map.get(key);
	}



	@Override
	public void set(String key, double v) {
		throw new IllegalStateException("Connot set variant on constant enironment.");
	}

}
