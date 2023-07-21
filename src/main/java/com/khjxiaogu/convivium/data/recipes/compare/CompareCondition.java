package com.khjxiaogu.convivium.data.recipes.compare;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.khjxiaogu.convivium.data.recipes.numbers.Expression;
import com.khjxiaogu.convivium.data.recipes.numbers.INumber;
import com.khjxiaogu.convivium.util.evaluator.IEnvironment;
import com.teammoeg.caupona.data.Writeable;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;

public class CompareCondition implements Writeable {
	Compare compare;
	INumber expr1;
	INumber expr2;
	public CompareCondition(Compare compare, INumber expr1, INumber expr2) {
		super();
		this.compare = compare;
		this.expr1 = expr1;
		this.expr2 = expr2;
	}

	public CompareCondition(FriendlyByteBuf buffer) {
		super();
		this.expr1=Expression.of(buffer);
		this.compare = Compares.of(buffer);
		this.expr2=Expression.of(buffer);
	}
	public CompareCondition(JsonObject jo) {
		super();
		this.expr1=Expression.of(GsonHelper.getAsString(jo, "left"));
		this.compare=Compares.of(jo.get("compare"));
		this.expr2=Expression.of(GsonHelper.getAsString(jo, "right"));
	}
	@Override
	public JsonElement serialize() {
		// TODO Auto-generated method stub
		JsonObject jo=new JsonObject();
		jo.add("left", expr1.serialize());
		jo.add("right", expr2.serialize());
		jo.add("compare", compare.serialize());
		return jo;
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		// TODO Auto-generated method stub
		expr1.write(buffer);
		compare.write(buffer);
		expr2.write(buffer);

	}
	public boolean test(IEnvironment env) {
		return compare.test((float)(double)expr1.apply(env), (float)(double)expr2.apply(env));
	}

}
