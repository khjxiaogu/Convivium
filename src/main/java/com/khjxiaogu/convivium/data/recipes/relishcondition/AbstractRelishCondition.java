package com.khjxiaogu.convivium.data.recipes.relishcondition;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;

public abstract class AbstractRelishCondition implements RelishCondition{
	protected String relish;
	@Override
	public JsonElement serialize() {
		JsonObject jo=new JsonObject();
		jo.addProperty("relish", relish);
		
		return jo;
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeUtf(relish);
	}
	

	public AbstractRelishCondition(FriendlyByteBuf buffer) {
		this(buffer.readUtf());
	}
	public AbstractRelishCondition(JsonObject json) {
		this(GsonHelper.getAsString(json,"relish"));
	}
	public AbstractRelishCondition(String relish) {
		super();
		this.relish = relish;
	}
}
