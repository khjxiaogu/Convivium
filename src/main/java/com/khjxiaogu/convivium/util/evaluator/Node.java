package com.khjxiaogu.convivium.util.evaluator;

import java.util.Map;

public interface Node{
	double eval(IEnvironment env);
	boolean isPrimary();
	Node simplify();
}