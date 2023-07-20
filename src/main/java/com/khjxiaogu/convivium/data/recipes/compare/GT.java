package com.khjxiaogu.convivium.data.recipes.compare;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;

public class GT extends Compare {

	@Override
	public boolean test(float t1, float num) {
		return Float.compare(t1, num)>0;
	}

	@Override
	public String getType() {
		return "greater";
	}

	public GT(float num) {
		super(num);
	}

	public GT(FriendlyByteBuf num) {
		super(num);
	}

	public GT(JsonObject num) {
		super(num);
	}

}
