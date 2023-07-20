package com.khjxiaogu.convivium.util.evaluator;

class VarNode implements Node{
	String token;

	public VarNode(String token) {
		this.token = token;
	}

	@Override
	public double eval(IEnvironment env) {
		return env.get(token);
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