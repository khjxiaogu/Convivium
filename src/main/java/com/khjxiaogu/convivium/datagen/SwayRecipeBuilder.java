package com.khjxiaogu.convivium.datagen;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import com.khjxiaogu.convivium.data.recipes.SwayEffect;
import com.khjxiaogu.convivium.data.recipes.SwayRecipe;
import com.khjxiaogu.convivium.data.recipes.compare.Compare;
import com.khjxiaogu.convivium.data.recipes.compare.CompareCondition;
import com.khjxiaogu.convivium.data.recipes.numbers.Expression;
import com.khjxiaogu.convivium.data.recipes.numbers.INumber;
import com.khjxiaogu.convivium.data.recipes.relishcondition.MajorRelishCondition;
import com.khjxiaogu.convivium.data.recipes.relishcondition.OnlyMajorRelishCondition;
import com.khjxiaogu.convivium.data.recipes.relishcondition.RelishCondition;
import com.teammoeg.caupona.data.IDataRecipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;

public class SwayRecipeBuilder {
	public static class SwayEffectBuilder<T>{
		MobEffect effect;
		INumber amplifier;
		INumber duration;
		Function<SwayEffect,T> fin;
		List<CompareCondition> compare=new ArrayList<>();
		public SwayEffectBuilder(Function<SwayEffect,T> p,MobEffect effect) {
			super();
			this.fin=p;
			this.effect = effect;
		}
		public SwayEffectBuilder<T> compare(String ex1,Compare c,String ex2) {
			compare.add(new CompareCondition(c,Expression.of(ex1),Expression.of(ex2)));
			return this;
		}
		public SwayEffectBuilder<T> amp(String s) {
			amplifier=Expression.of(s);
			return this;
		}
		public SwayEffectBuilder<T> time(String s){
			duration=Expression.of(s);
			return this;
		}
		public T next() {
			return fin.apply(new SwayEffect(effect,amplifier,duration,compare));
		}
	}

	private List<RelishCondition> relish=new ArrayList<>();
	private int priority;
	private Map<String,INumber> locals=new LinkedHashMap<>();
	private List<SwayEffect> effects=new ArrayList<>();
	private ResourceLocation id;
	private ResourceLocation icon;
	
	public static List<SwayRecipe> recipes=new ArrayList<>();

	public SwayRecipeBuilder(ResourceLocation id, ResourceLocation icon) {
		super();
		this.id = id;
		this.icon = icon;
	}

	public SwayRecipeBuilder local(String k,String exp) {
		locals.put(k, Expression.of(exp));
		return this;
	}
	public SwayRecipeBuilder local(String k,double d) {
		locals.put(k, Expression.of((float)d));
		return this;
	}
	public SwayRecipeBuilder major(String relish) {
		this.relish.add(new MajorRelishCondition(relish));
		return this;
	}
	public SwayRecipeBuilder only(String relish) {
		this.relish.add(new OnlyMajorRelishCondition(relish));
		return this;
	}
	public SwayRecipeBuilder cond(RelishCondition relish) {
		this.relish.add(relish);
		return this;
	}
	public SwayRecipeBuilder p(int p) {
		this.priority=p;
		return this;
	}
	public SwayEffectBuilder<SwayRecipeBuilder> effect(MobEffect me){
		return new SwayEffectBuilder<>(k->{
			effects.add(k);
			return this;
		},me);
	}
	public void end(Consumer<IDataRecipe> out) {
		out.accept(new SwayRecipe(id,relish, priority, locals, effects, icon));
	}
}
