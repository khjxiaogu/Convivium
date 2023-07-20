package com.khjxiaogu.convivium.util.evaluator;

public interface Node{
	double eval(IEnvironment env);
	boolean isPrimary();
	Node simplify();
}