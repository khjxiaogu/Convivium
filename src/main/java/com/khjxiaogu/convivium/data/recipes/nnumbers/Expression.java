package com.khjxiaogu.convivium.data.recipes.nnumbers;

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
		public Double apply(IEnvironment t) {
			// TODO Auto-generated method stub
			return (double) num;
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
	public Double apply(IEnvironment t) {
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
