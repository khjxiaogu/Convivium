package com.khjxiaogu.convivium.data.recipes.compare;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;

public abstract class Compare implements ICompare {
	@Override
	public abstract boolean test(Float t1,Float num) ;
	public abstract String getType();
	public Compare() {
		super();
	}
	public Compare(JsonObject num) {
		super();
	}
	public Compare(FriendlyByteBuf num) {
		super();
	}
	@Override
	public JsonElement serialize() {
		JsonObject jo=new JsonObject();
		jo.addProperty("type", getType());
		return jo;
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
	}

}
