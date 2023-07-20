package com.khjxiaogu.convivium.data.recipes.compare;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;

public class LT extends Compare {

	@Override
	public boolean test(float t1, float num) {
		return Float.compare(t1, num)<0;
	}

	@Override
	public String getType() {
		return "less";
	}

	public LT(float num) {
		super(num);
	}

	public LT(FriendlyByteBuf num) {
		super(num);
	}

	public LT(JsonObject num) {
		super(num);
	}

}
