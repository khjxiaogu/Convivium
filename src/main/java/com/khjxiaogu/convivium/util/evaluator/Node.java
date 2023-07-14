package com.khjxiaogu.convivium.util.evaluator;

import java.util.Map;

public interface Node{
	double eval(Map<String,Double> env);
	boolean isPrimary();
	Node simplify();
}