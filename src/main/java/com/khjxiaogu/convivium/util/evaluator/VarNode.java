package com.khjxiaogu.convivium.util.evaluator;

import java.util.Map;

class VarNode implements Node{
	String token;

	public VarNode(String token) {
		this.token = token;
	}

	@Override
	public double eval(Map<String, Double> env) {
		return env.getOrDefault(token,0.0);
	}
	@Override
	public String toString() {
		return  token ;
	}

	@Override
	public boolean isPrimary() {
		return false;
	}

	@Override
	public Node simplify() {
		return this;
	}
}