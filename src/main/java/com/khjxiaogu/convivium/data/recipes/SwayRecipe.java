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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;

public class SwayRecipe  extends IDataRecipe{


	public SwayRecipe(List<RelishCondition> relish, int priority, Map<String, INumber> locals,
			List<SwayEffect> effects, ResourceLocation icon) {
		this.relish = relish;
		this.priority = priority;
		this.locals = locals;
		this.effects = effects;
		this.icon = icon;
	}

	public List<RelishCondition> relish;
	public int priority;
	public Map<String,INumber> locals;
	public List<SwayEffect> effects;
	public ResourceLocation icon;
	public static DeferredHolder<RecipeSerializer<?>,RecipeSerializer<?>> SERIALIZER;
	public static DeferredHolder<RecipeType<?>,RecipeType<Recipe<?>>> TYPE;
	public static List<RecipeHolder<SwayRecipe>> recipes;
	public static MapCodec<SwayRecipe> CODEC=RecordCodecBuilder.mapCodec(t->t.group(
		Codec.list(RelishConditions.CODEC).optionalFieldOf("relish",List.of()).forGetter(o->o.relish),
		Codec.INT.fieldOf("priority").forGetter(o->o.priority),
		Codec.compoundList(Codec.STRING, INumber.CODEC).optionalFieldOf("locals",List.of()).forGetter(o->o.locals.entrySet().stream().map(e->Pair.of(e.getKey(),e.getValue())).collect(Collectors.toList())),
		Codec.list(SwayEffect.CODEC).fieldOf("effects").forGetter(o->o.effects),
		ResourceLocation.CODEC.fieldOf("icon").forGetter(o->o.icon)
		).apply(t, SwayRecipe::new));
	
	public SwayRecipe(List<RelishCondition> relish, int priority, List<Pair<String, INumber>> locals, List<SwayEffect> effects, ResourceLocation icon) {
		super();
		this.relish = relish;
		this.priority = priority;
		this.locals=new HashMap<>();
		locals.stream().forEach(p->this.locals.put(p.getFirst(),p.getSecond()));
		this.effects = effects;
		this.icon = icon;
	}
	@Override
	public RecipeSerializer<?> getSerializer() {
		// TODO Auto-generated method stub
		return SERIALIZER.get();
	}
	@Override
	public RecipeType<?> getType() {
		// TODO Auto-generated method stub
		return TYPE.get();
	}

	/*
	public SwayRecipe(ResourceLocation id,FriendlyByteBuf jo) {
		super(id);
		relish=SerializeUtil.readList(jo, RelishConditions::of);
		priority=jo.readVarInt();
		locals=new LinkedHashMap<>();
		SerializeUtil.readList(jo,b->Pair.of(b.readUtf(),Expression.of(b))).forEach(d->locals.put(d.getFirst(), d.getSecond()));
		effects=SerializeUtil.readList(jo,SwayEffect::new);
		icon=jo.readResourceLocation();
		
	}*/
/*
	public void write(FriendlyByteBuf pb) {
		SerializeUtil.writeList(pb, relish, RelishConditions::write);
		pb.writeVarInt(priority);
		SerializeUtil.writeList(pb, locals.entrySet(),(t,b)->{b.writeUtf(t.getKey());t.getValue().write(b);});
		SerializeUtil.writeList(pb,effects,Writeable::write);
		pb.writeResourceLocation(icon);
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
