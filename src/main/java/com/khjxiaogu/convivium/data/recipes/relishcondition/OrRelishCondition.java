package com.khjxiaogu.convivium.data.recipes.relishcondition;

import com.google.gson.JsonObject;
import com.khjxiaogu.convivium.data.recipes.RelishRecipe;
import com.khjxiaogu.convivium.util.BeveragePendingContext;
import com.teammoeg.caupona.data.TranslationProvider;

import net.minecraft.network.FriendlyByteBuf;

public class OrRelishCondition extends LogicalRelishCondition {

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return "or";
	}

	@Override
	public boolean test(BeveragePendingContext t) {
		// TODO Auto-generated method stub
		return r1.test(t)||r2.test(t);
	}

	public OrRelishCondition(FriendlyByteBuf pb) {
		super(pb);
		// TODO Auto-generated constructor stub
	}

	public OrRelishCondition(JsonObject jo) {
		super(jo);
		// TODO Auto-generated constructor stub
	}

	public OrRelishCondition(RelishCondition r1, RelishCondition r2) {
		super(r1, r2);
		// TODO Auto-generated constructor stub
	}



}
