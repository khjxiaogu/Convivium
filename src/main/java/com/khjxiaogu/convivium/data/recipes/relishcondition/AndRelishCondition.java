package com.khjxiaogu.convivium.data.recipes.relishcondition;

import com.google.gson.JsonObject;
import com.khjxiaogu.convivium.util.BeveragePendingContext;

import net.minecraft.network.FriendlyByteBuf;

public class AndRelishCondition extends LogicalRelishCondition {

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return "and";
	}

	@Override
	public boolean test(BeveragePendingContext t) {
		// TODO Auto-generated method stub
		return r1.test(t)&&r2.test(t);
	}

	public AndRelishCondition(FriendlyByteBuf pb) {
		super(pb);
		// TODO Auto-generated constructor stub
	}

	public AndRelishCondition(JsonObject jo) {
		super(jo);
		// TODO Auto-generated constructor stub
	}

	public AndRelishCondition(RelishCondition r1, RelishCondition r2) {
		super(r1, r2);
		// TODO Auto-generated constructor stub
	}



}
