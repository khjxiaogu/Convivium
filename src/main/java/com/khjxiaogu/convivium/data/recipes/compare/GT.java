package com.khjxiaogu.convivium.data.recipes.compare;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;

public class GT extends Compare {

	@Override
	public boolean test(Float t1, Float num) {
		return Float.compare(t1!=null?t1:0, num!=0?num:0)>0;
	}

	@Override
	public String getType() {
		return "greater";
	}

	public GT() {
		super();
	}

	public GT(FriendlyByteBuf num) {
		super(num);
	}

	public GT(JsonObject num) {
		super(num);
	}

}
