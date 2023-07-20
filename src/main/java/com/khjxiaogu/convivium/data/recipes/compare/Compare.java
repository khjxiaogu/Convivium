package com.khjxiaogu.convivium.data.recipes.compare;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;

public abstract class Compare implements ICompare {
	float num;
	@Override
	public boolean test(Float t) {
		if(t==null)t=0f;
		return test(t, num);
	}
	public abstract boolean test(float t1,float num) ;
	public abstract String getType();
	public Compare(float num) {
		super();
		this.num = num;
	}
	public Compare(JsonObject num) {
		super();
		this.num = GsonHelper.getAsFloat(num, "to");
	}
	public Compare(FriendlyByteBuf num) {
		super();
		this.num = num.readFloat();
	}
	@Override
	public JsonElement serialize() {
		JsonObject jo=new JsonObject();
		jo.addProperty("to",num);
		return jo;
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeFloat(num);
	}

}
