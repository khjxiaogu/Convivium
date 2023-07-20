package com.khjxiaogu.convivium.util.evaluator;

public interface IEnvironment {
	double get(String key);
	Double getOptional(String key);
	void set(String key,double v);
}
