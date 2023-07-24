/*
 * Copyright (c) 2023 IEEM Trivium Society/khjxiaogu
 *
 * This file is part of Convivium.
 *
 * Convivium is free software: you can redistribute it and/or modify
 * it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 * the Free Software Foundation, version 3.
 *
 * Convivium is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 * You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 * along with Convivium. If not, see <https://www.gnu.org/licenses/>.
 */

package com.khjxiaogu.convivium.data.recipes;

import java.util.List;
import java.util.Optional;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.khjxiaogu.convivium.data.recipes.compare.CompareCondition;
import com.khjxiaogu.convivium.data.recipes.numbers.Expression;
import com.khjxiaogu.convivium.data.recipes.numbers.INumber;
import com.khjxiaogu.convivium.util.evaluator.IEnvironment;
import com.teammoeg.caupona.data.SerializeUtil;
import com.teammoeg.caupona.data.Writeable;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.registries.ForgeRegistries;

public class SwayEffect implements Writeable{
	MobEffect effect;
	INumber amplifier;
	INumber duration;
	List<CompareCondition> compare;
	public SwayEffect(MobEffect effect, INumber amplifier, INumber duration, List<CompareCondition> compare) {
		super();
		this.effect = effect;
		this.amplifier = amplifier;
		this.duration = duration;
		this.compare = compare;
	}
	public SwayEffect(JsonObject jo) {
		
		if (jo.has("level"))
			amplifier = Expression.of(jo.get("level"));
		else
			amplifier = Expression.ZERO;
		if (jo.has("time"))
			duration = Expression.of(jo.get("time"));
		effect = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(jo.get("effect").getAsString()));
		compare = SerializeUtil.parseJsonList(jo.get("condition"), CompareCondition::new);
		
		
	}
	public SwayEffect(FriendlyByteBuf buffer) {
		amplifier=Expression.of(buffer);
		duration=Expression.of(buffer);
		effect=buffer.readRegistryIdUnsafe(ForgeRegistries.MOB_EFFECTS);
		compare=SerializeUtil.readList(buffer, CompareCondition::new);
	}
	@Override
	public JsonElement serialize() {
		// TODO Auto-generated method stub
		JsonObject jo=new JsonObject();
		jo.add("level", amplifier.serialize());
		jo.add("time",duration.serialize());
		jo.addProperty("effect",Utils.getRegistryName(effect).toString());
		jo.add("condition",SerializeUtil.toJsonList(compare, CompareCondition::serialize));
		return jo;
	}
	public Optional<MobEffectInstance> getEffectNoChecck(IEnvironment env) {
		if(effect!=null) 
			return Optional.of(new MobEffectInstance(effect,(int)duration.applyAsDouble(env),(int)amplifier.applyAsDouble(env)));
		return Optional.empty();
	}
	public Optional<MobEffectInstance> getEffect(IEnvironment env) {
		if(effect!=null&&compare.stream().allMatch(t->t.test(env))) {
			return Optional.of(new MobEffectInstance(effect,(int)duration.applyAsDouble(env),(int)amplifier.applyAsDouble(env)));
		}
		return Optional.empty();
	}
	public boolean hasEffect(IEnvironment env) {
		if(compare.stream().allMatch(t->t.test(env))) {
			return true;
		}
		return false;
	}
	@Override
	public void write(FriendlyByteBuf buffer) {
		// TODO Auto-generated method stub
		amplifier.write(buffer);
		duration.write(buffer);
		buffer.writeRegistryIdUnsafe(ForgeRegistries.MOB_EFFECTS, effect);
		SerializeUtil.writeList(buffer, compare, CompareCondition::write);
	}

}
