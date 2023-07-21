package com.khjxiaogu.convivium.data.recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.khjxiaogu.convivium.data.recipes.compare.Compare;
import com.khjxiaogu.convivium.data.recipes.compare.Compares;
import com.khjxiaogu.convivium.data.recipes.nnumbers.Expression;
import com.khjxiaogu.convivium.data.recipes.nnumbers.INumber;
import com.teammoeg.caupona.data.Writeable;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.ForgeRegistries;

public class SwayEffect implements Writeable{
	MobEffect effect;
	INumber amplifier;
	INumber duration;
	Compare compare;
	INumber expr1;
	INumber expr2;
	public SwayEffect(JsonObject jo) {
		
		if (jo.has("level"))
			amplifier = Expression.of(jo.get("level"));
		else
			amplifier = Expression.ZERO;
		if (jo.has("time"))
			duration = Expression.of(jo.get("time"));
		effect = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(jo.get("effect").getAsString()));
		compare = Compares.of(jo.get("compare").getAsJsonObject());
		expr1 = Expression.of(jo.get("left"));
		expr2 = Expression.of(jo.get("right"));
		
	}
	@Override
	public JsonElement serialize() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		// TODO Auto-generated method stub
		
	}

}
