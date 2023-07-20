package com.khjxiaogu.convivium.data.recipes.compare;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;

public class EQ extends Compare {

	@Override
	public boolean test(float t1, float num) {
		return Float.compare(t1, num)==0;
	}

	public EQ(float num) {
		super(num);
	}

	public EQ(FriendlyByteBuf num) {
		super(num);
	}

	public EQ(JsonObject num) {
		super(num);
	}

	@Override
	public String getType() {
		return "equals";
	}

}
