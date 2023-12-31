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

package com.khjxiaogu.convivium.data.recipes.numbers;

import java.util.Objects;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.khjxiaogu.convivium.util.evaluator.Evaluator;
import com.khjxiaogu.convivium.util.evaluator.IEnvironment;
import com.khjxiaogu.convivium.util.evaluator.Node;
import com.khjxiaogu.convivium.util.evaluator.NullEnvironment;

import net.minecraft.network.FriendlyByteBuf;

public class Expression implements INumber{
	public static class Constant implements INumber{
		float num=0;
		private Constant(float num) {
			super();
			this.num = num;
		}
		private Constant(FriendlyByteBuf num) {
			super();
			this.num = num.readFloat();
		}

		@Override
		public JsonElement serialize() {
			// TODO Auto-generated method stub
			return new JsonPrimitive(num);
		}

		@Override
		public void write(FriendlyByteBuf buffer) {
			// TODO Auto-generated method stub
			buffer.writeVarInt(2);
			buffer.writeFloat(num);
		}

		@Override
		public double applyAsDouble(IEnvironment t) {
			// TODO Auto-generated method stub
			return num;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Float.floatToIntBits(num);
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Constant other = (Constant) obj;
			if (Float.floatToIntBits(num) != Float.floatToIntBits(other.num))
				return false;
			return true;
		}
		@Override
		public String toString() {
			return ""+ num ;
		}
		
	}
	public static final INumber ZERO=new Constant(0);
	public static final INumber ONE=new Constant(1);
	Node node;
	String expr;
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
	private Expression(FriendlyByteBuf expr) {
		super();
		this.expr = expr.readUtf();
		this.node = Evaluator.eval(this.expr);
	}
	public static INumber of(FriendlyByteBuf expr) {
		switch(expr.readVarInt()) {
		case 1:return new Expression(expr);
		case 2:return new Constant(expr);
		}
		throw new IllegalArgumentException("Expression must be number or string");
	}
	public static INumber of(JsonElement expr) {
		if(expr.isJsonPrimitive()) {
			JsonPrimitive jp=expr.getAsJsonPrimitive();
			if(jp.isNumber())
				return new Constant(jp.getAsFloat());
			return of(jp.getAsString());
		}
		throw new IllegalArgumentException("Expression must be number or string");
	}
	public static INumber of(float expr) {
		return new Constant(expr);
	}
	public static INumber of(String expr) {
		Node node = Evaluator.eval(expr);
		if(node.isPrimary())
			return new Constant((float) node.eval(NullEnvironment.INSTANCE));
		return new Expression(expr,node);
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
	public JsonElement serialize() {
		// TODO Auto-generated method stub
		return new JsonPrimitive(expr);
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		// TODO Auto-generated method stub
		buffer.writeVarInt(1);
		buffer.writeUtf(expr);
	}
	
}
