/*
 * Copyright (c) 2024 IEEM Trivium Society/khjxiaogu
 *
 * This file is part of Convivium.
 *
 * Convivium is free software: you can redistribute it and/or modify
 * it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 * the Free Software Foundation, version 3.
 *
 * Convivium is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 * You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 * along with Convivium. If not, see <https://www.gnu.org/licenses/>.
 */

package com.khjxiaogu.convivium.data.recipes.numbers;

import java.util.Objects;

import com.khjxiaogu.convivium.util.evaluator.Evaluator;
import com.khjxiaogu.convivium.util.evaluator.IEnvironment;
import com.khjxiaogu.convivium.util.evaluator.Node;
import com.khjxiaogu.convivium.util.evaluator.NullEnvironment;
import com.mojang.serialization.DataResult;

public class Expression implements INumber{
	public static class Constant implements INumber{
		private final double num;
		private Constant(double num) {
			super();
			this.num = num;
		}
		public double num() {
			return num;
		}
		@Override
		public double applyAsDouble(IEnvironment t) {
			return num;
		}
		@Override
		public int hashCode() {
			return Objects.hash(num);
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			Constant other = (Constant) obj;
			return Double.doubleToLongBits(num) == Double.doubleToLongBits(other.num);
		}
		@Override
		public String toString() {
			return ""+ num ;
		}
		@Override
		public DataResult<Double> asConstant() {
			return DataResult.success(num);
		}
		@Override
		public DataResult<String> asExpression() {
			return DataResult.success(""+ num);
		}
		
	}
	public static final INumber ZERO=new Constant(0);
	public static final INumber ONE=new Constant(1);
	private final Node node;

	private final String expr;
	public String expr() {
		return expr;
	}
	public Expression(String expr,Node node) {
		super();
		this.expr = expr;
		this.node = node;
	}
	public Expression(String expr) {
		super();
		this.expr = expr;
		this.node = Evaluator.eval(expr);
	}

	public static Constant of(double expr) {
		return new Constant(expr);
	}
	public static INumber of(String expr) {
		Node node = Evaluator.eval(expr);
		if(node.isPrimary())
			return new Constant((float) node.eval(NullEnvironment.INSTANCE));
		return new Expression(expr,node);
	}
	public static DataResult<INumber> parse(String expr) {
		try {
			return DataResult.success(of(expr));
		}catch(Exception ex) {
			return DataResult.error(ex::getMessage);
		}
	}
	public DataResult<Double> asConstant(){
		return DataResult.error(()->"Not a constant");
	}
	public double applyAsDouble(IEnvironment t) {
		return node.eval(t);
	}
	@Override
	public String toString() {
		return node.toString();
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
	@Override
	public DataResult<String> asExpression() {
		return DataResult.success(expr);
	}
	
}
