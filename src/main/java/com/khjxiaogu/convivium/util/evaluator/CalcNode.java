package com.khjxiaogu.convivium.util.evaluator;

import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;

class CalcNode implements Node{
	DoubleUnaryOperator calc;
	Node nested;
	String name;
	public CalcNode(Node nested,String name,DoubleUnaryOperator calc) {
		this.calc = calc;
		this.nested = nested;
		this.name=name;
	}

	@Override
	public double eval(IEnvironment env) {
		return calc.applyAsDouble(nested.eval(env));
	}
	
	@Override
	public boolean isPrimary() {
		return false;
	}
	
	@Override
	public Node simplify() {
		nested=nested.simplify();
		if(nested.isPrimary())
			return new ConstNode(eval(NullEnvironment.INSTANCE));
		return this;
	}

	@Override
	public String toString() {
		return name + "(" + nested + ")";
	}
}