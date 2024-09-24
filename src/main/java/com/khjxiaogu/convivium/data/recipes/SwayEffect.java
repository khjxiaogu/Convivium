/*
 * Copyright (c) 2024 IEEM Trivium Society/khjxiaogu
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

import com.khjxiaogu.convivium.data.recipes.compare.CompareCondition;
import com.khjxiaogu.convivium.data.recipes.numbers.Expression;
import com.khjxiaogu.convivium.data.recipes.numbers.INumber;
import com.khjxiaogu.convivium.util.evaluator.IEnvironment;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

public class SwayEffect{
	Holder<MobEffect> effect;
	INumber amplifier;
	INumber duration;
	List<CompareCondition> compare;
	public static final Codec<SwayEffect> CODEC=RecordCodecBuilder.create(t->t.group(
			BuiltInRegistries.MOB_EFFECT.holderByNameCodec().fieldOf("effect").forGetter(o->o.effect),
			INumber.CODEC.optionalFieldOf("level", Expression.ZERO).forGetter(o->o.amplifier),
			INumber.CODEC.optionalFieldOf("time", Expression.ONE).forGetter(o->o.duration),
			Codec.list(CompareCondition.CODEC).fieldOf("condition").forGetter(o->o.compare)
		).apply(t, SwayEffect::new));
	public SwayEffect(Holder<MobEffect> effect, INumber amplifier, INumber duration, List<CompareCondition> compare) {
		super();
		this.effect = effect;
		this.amplifier = amplifier;
		this.duration = duration;
		this.compare = compare;
	}
/*
	public SwayEffect(FriendlyByteBuf buffer) {
		amplifier=Expression.of(buffer);
		duration=Expression.of(buffer);
		effect=buffer.readRegistryIdUnsafe(ForgeRegistries.MOB_EFFECTS);
		compare=SerializeUtil.readList(buffer, CompareCondition::new);
	}*/

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
	}/*
	@Override
	public void write(FriendlyByteBuf buffer) {
		// TODO Auto-generated method stub
		amplifier.write(buffer);
		duration.write(buffer);
		buffer.writeRegistryIdUnsafe(ForgeRegistries.MOB_EFFECTS, effect);
		SerializeUtil.writeList(buffer, compare, CompareCondition::write);
	}*/

}
