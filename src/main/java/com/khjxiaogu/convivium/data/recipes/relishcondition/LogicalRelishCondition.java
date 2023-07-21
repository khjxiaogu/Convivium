package com.khjxiaogu.convivium.data.recipes.relishcondition;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.teammoeg.caupona.data.TranslationProvider;

import net.minecraft.network.FriendlyByteBuf;

public abstract class LogicalRelishCondition implements RelishCondition {
	protected RelishCondition r1;
	protected RelishCondition r2;

	public LogicalRelishCondition(RelishCondition r1, RelishCondition r2) {
		super();
		this.r1 = r1;
		this.r2 = r2;
	}
	public LogicalRelishCondition(JsonObject jo) {
		super();
		this.r1 = RelishConditions.of(jo.get("cond1").getAsJsonObject());
		this.r2 = RelishConditions.of(jo.get("cond2").getAsJsonObject());
	}
	public LogicalRelishCondition(FriendlyByteBuf pb) {
		super();
		this.r1 = RelishConditions.of(pb);
		this.r2 = RelishConditions.of(pb);
	}
	@Override
	public JsonElement serialize() {
		// TODO Auto-generated method stub
		JsonObject jo=new JsonObject();
		jo.addProperty("type", getType());
		jo.add("cond1", r1.serialize());
		jo.add("cond2", r2.serialize());
		return jo;
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		// TODO Auto-generated method stub
		RelishConditions.write(r1,buffer);
		RelishConditions.write(r2,buffer);
	}
	@Override
	public String getTranslation(TranslationProvider p) {
		// TODO Auto-generated method stub
		return p.getTranslation("recipe.convivium.relish_cond."+getType(),r1.getTranslation(p),r2.getTranslation(p));
	}

}
