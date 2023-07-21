package com.khjxiaogu.convivium.data.recipes.compare;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;

public class EQ extends Compare {

	@Override
	public boolean test(Float t1, Float num) {
		return Float.compare(t1!=null?t1:0, num!=0?num:0)==0;
	}

	public EQ() {

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
