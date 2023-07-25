/*
 * Copyright (c) 2023 IEEM Trivium Society/khjxiaogu
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

package com.khjxiaogu.convivium.util.evaluator;

import java.util.function.DoubleBinaryOperator;

class BiCalcNode extends BiNode{
	DoubleBinaryOperator calc;
	public static DoubleBinaryOperator add=(v1,v2)->v1+v2;
	public static DoubleBinaryOperator min=(v1,v2)->v1-v2;
	public static DoubleBinaryOperator mul=(v1,v2)->v1*v2;
	public static DoubleBinaryOperator div=(v1,v2)->v1/v2;
	public static DoubleBinaryOperator pow=(v1,v2)->Math.pow(v1,v2);
	public static DoubleBinaryOperator mod=(v1,v2)->v1%v2;
	public BiCalcNode(Node left, Node right,DoubleBinaryOperator calc) {
		super(left, right);
		this.calc=calc;
	}

	@Override
	public double eval(IEnvironment env) {
		return calc.applyAsDouble(left.eval(env),right.eval(env));
	}

	@Override
	public boolean isPrimary() {
		return false;
	}

	@Override
	public String toString() {
		String cn=calc==add?"+":(calc==min?"-":(calc==mul?"*":(calc==div?"/":(calc==pow?"^":(calc==mod?"%":calc+"")))));
		return left+cn+right;
	}

	@Override
	public Node simplify() {
		left=left.simplify();
		right=right.simplify();
		if(left.isPrimary()&&right.isPrimary())
			return new ConstNode(eval(NullEnvironment.INSTANCE));
		else if(calc==add||calc==min) {
			return new ExprNode(calc==add,left,right).simplify();
		}else if(calc==mul||calc==div) {
			return new TermNode(calc==mul,left,right).simplify();
		}
		return this;
	}
	
}