package com.khjxiaogu.convivium.data.recipes.compare;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;

public class LT extends Compare {

	@Override
	public boolean test(Float t1, Float num) {
		return Float.compare(t1!=null?t1:0, num!=0?num:0)<0;
	}

	@Override
	public String getType() {
		return "lesser";
	}

	public LT() {
		super();
	}

	public LT(FriendlyByteBuf num) {
		super(num);
	}

	public LT(JsonObject num) {
		super(num);
	}

}
