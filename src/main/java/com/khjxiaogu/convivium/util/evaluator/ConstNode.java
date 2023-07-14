package com.khjxiaogu.convivium.util.evaluator;

import java.util.Map;

class ConstNode implements Node{
	double val;

	@Override
	public String toString() {
		return ""+val;
	}

	public ConstNode(double val) {
		this.val = val;
	}

	@Override
	public double eval(Map<String, Double> env) {
		return val;
	}
	@Override
	public boolean isPrimary() {
		return true;
	}

	@Override
	public Node simplify() {
		return this;
	}
}