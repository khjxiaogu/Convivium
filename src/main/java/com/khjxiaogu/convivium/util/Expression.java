package com.khjxiaogu.convivium.util;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import com.khjxiaogu.convivium.util.evaluator.Evaluator;
import com.khjxiaogu.convivium.util.evaluator.Node;

public class Expression implements Function<Map<String,Double>,Double>{
	Node node;
	String expr;
	public Expression(String expr) {
		super();
		this.expr = expr;
		node=Evaluator.eval(expr);
	}
	@Override
	public Double apply(Map<String, Double> t) {
		return node.eval(t);
	}
	@Override
	public String toString() {
		return expr;
	}
	@Override
	public int hashCode() {
		return Objects.hash(expr);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Expression other = (Expression) obj;
		return Objects.equals(expr, other.expr);
	}
	
}
