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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.khjxiaogu.convivium.data.recipes.compare.CompareCondition;
import com.khjxiaogu.convivium.data.recipes.numbers.Expression;
import com.khjxiaogu.convivium.data.recipes.numbers.INumber;
import com.khjxiaogu.convivium.data.recipes.relishcondition.RelishCondition;
import com.khjxiaogu.convivium.data.recipes.relishcondition.RelishConditions;
import com.khjxiaogu.convivium.util.BeveragePendingContext;
import com.khjxiaogu.convivium.util.evaluator.IEnvironment;
import com.khjxiaogu.convivium.util.evaluator.VariantEnvironment;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teammoeg.caupona.data.IDataRecipe;
import com.teammoeg.caupona.util.SerializeUtil;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;

public class SwayRecipe  extends IDataRecipe{
	public static class SwayEffect{
		public static final Codec<SwayEffect> CODEC=RecordCodecBuilder.create(t->t.group(
			BuiltInRegistries.MOB_EFFECT.holderByNameCodec().fieldOf("effect").forGetter(o->o.effect),
			INumber.CODEC.optionalFieldOf("level", Expression.ZERO).forGetter(o->o.amplifier),
			INumber.CODEC.optionalFieldOf("time", Expression.ONE).forGetter(o->o.duration),
			Codec.list(CompareCondition.CODEC).fieldOf("condition").forGetter(o->o.compare)
		).apply(t, SwayEffect::new));
		public static final StreamCodec<RegistryFriendlyByteBuf,SwayEffect> STREAM_CODEC=StreamCodec.composite(
			ByteBufCodecs.holderRegistry(Registries.MOB_EFFECT),o->o.effect,
			INumber.STREAM_CODEC,o->o.amplifier,
			INumber.STREAM_CODEC,o->o.duration,
			CompareCondition.STREAM_CODEC.apply(ByteBufCodecs.list()),o->o.compare,
		SwayEffect::new);
		Holder<MobEffect> effect;
		INumber amplifier;
		INumber duration;
		List<CompareCondition> compare;
		
		public SwayEffect(Holder<MobEffect> effect, INumber amplifier, INumber duration, List<CompareCondition> compare) {
			super();
			this.effect = effect;
			this.amplifier = amplifier;
			this.duration = duration;
			this.compare = compare;
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

	}



	public List<RelishCondition> relish;
	public int priority;
	public Map<String,INumber> locals;
	public List<SwayEffect> effects;
	public Identifier icon;
	public static DeferredHolder<RecipeSerializer<?>,RecipeSerializer<SwayRecipe>> SERIALIZER;
	public static DeferredHolder<RecipeType<?>,RecipeType<SwayRecipe>> TYPE;
	public static List<RecipeHolder<SwayRecipe>> recipes;
	public static final MapCodec<SwayRecipe> CODEC=RecordCodecBuilder.mapCodec(t->t.group(
		Codec.list(RelishConditions.CODEC).optionalFieldOf("relish",List.of()).forGetter(o->o.relish),
		Codec.INT.fieldOf("priority").forGetter(o->o.priority),
		Codec.compoundList(Codec.STRING, INumber.CODEC).optionalFieldOf("locals",List.of())
		.forGetter(o->o.locals.entrySet().stream().map(e->Pair.of(e.getKey(),e.getValue())).collect(Collectors.toList())),
		Codec.list(SwayEffect.CODEC).fieldOf("effects").forGetter(o->o.effects),
		Identifier.CODEC.fieldOf("icon").forGetter(o->o.icon)
		).apply(t, SwayRecipe::new));
	public static final StreamCodec<RegistryFriendlyByteBuf,SwayRecipe> STREAM_CODEC=StreamCodec.composite(
		RelishConditions.STREAM_CODEC.apply(ByteBufCodecs.list()),o->o.relish,
		ByteBufCodecs.VAR_INT,o->o.priority,
		SerializeUtil.pair(ByteBufCodecs.STRING_UTF8, INumber.STREAM_CODEC).apply(ByteBufCodecs.list()),
		o->o.locals.entrySet().stream().map(e->Pair.of(e.getKey(),e.getValue())).collect(Collectors.toList()),
		SwayEffect.STREAM_CODEC.apply(ByteBufCodecs.list()),o->o.effects,
		Identifier.STREAM_CODEC,o->o.icon,
		SwayRecipe::new
		);
	public SwayRecipe(List<RelishCondition> relish, int priority, Map<String, INumber> locals,
			List<SwayEffect> effects2, Identifier icon) {
		this.relish = relish;
		this.priority = priority;
		this.locals = locals;
		this.effects = effects2;
		this.icon = icon;
	}
	public SwayRecipe(List<RelishCondition> relish, int priority, List<Pair<String, INumber>> locals, List<SwayEffect> effects, Identifier icon) {
		super();
		this.relish = relish;
		this.priority = priority;
		this.locals=new HashMap<>();
		locals.stream().forEach(p->this.locals.put(p.getFirst(),p.getSecond()));
		this.effects = effects;
		this.icon = icon;
	}
	@Override
	public RecipeSerializer<SwayRecipe> getSerializer() {
		// TODO Auto-generated method stub
		return SERIALIZER.get();
	}
	@Override
	public RecipeType<SwayRecipe> getType() {
		// TODO Auto-generated method stub
		return TYPE.get();
	}

	/*
	public SwayRecipe(Identifier id,FriendlyByteBuf jo) {
		super(id);
		relish=SerializeUtil.readList(jo, RelishConditions::of);
		priority=jo.readVarInt();
		locals=new LinkedHashMap<>();
		SerializeUtil.readList(jo,b->Pair.of(b.readUtf(),Expression.of(b))).forEach(d->locals.put(d.getFirst(), d.getSecond()));
		effects=SerializeUtil.readList(jo,SwayEffect::new);
		icon=jo.readIdentifier();
		
	}*/
/*
	public void write(FriendlyByteBuf pb) {
		SerializeUtil.writeList(pb, relish, RelishConditions::write);
		pb.writeVarInt(priority);
		SerializeUtil.writeList(pb, locals.entrySet(),(t,b)->{b.writeUtf(t.getKey());t.getValue().write(b);});
		SerializeUtil.writeList(pb,effects,Writeable::write);
		pb.writeIdentifier(icon);
	}*/
	public IEnvironment createChildEnv(IEnvironment par) {
		return new VariantEnvironment(par,locals);
	}
	public boolean canApply(BeveragePendingContext ctx) {
		return relish.stream().anyMatch(t->t.test(ctx));
	}
	public boolean hasEffects(IEnvironment env){
		for(SwayEffect sw:effects) {
			if(sw.hasEffect(env))return true;
		}
		return false;
	}
	public Pair<Boolean,List<MobEffectInstance>> getEffects(IEnvironment env){
		List<MobEffectInstance> result=new ArrayList<>();
		boolean res=false;
		for(SwayEffect sw:effects) {
			if(sw.hasEffect(env)) {
				sw.getEffect(env).ifPresent(result::add);
				res=true;
			}
		}
		return Pair.of(res, result);
	}
}
