package com.khjxiaogu.convivium.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.mojang.datafixers.util.Pair;
import com.teammoeg.caupona.util.FloatemStack;
import com.teammoeg.caupona.util.IFoodInfo;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;

public class BeverageInfo implements IFoodInfo {
	public List<FloatemStack> stacks;
	public List<MobEffectInstance> effects;
	public List<Pair<MobEffectInstance, Float>> foodeffect = new ArrayList<>();
	public int healing;
	public float saturation;
	@Override
	public List<FloatemStack> getStacks() {
		return stacks;
	}

	@Override
	public int getHealing() {
		return healing;
	}

	@Override
	public float getSaturation() {
		return saturation;
	}

	@SuppressWarnings("deprecation")
	public FoodProperties getFood() {

		FoodProperties.Builder b = new FoodProperties.Builder();
		for (MobEffectInstance eff : effects) {
			if (eff != null) {
				b.effect(eff, 1);
			}
		}
		for (Pair<MobEffectInstance, Float> ef : foodeffect) {
			b.effect(()->new MobEffectInstance(ef.getFirst()), ef.getSecond());
		}
		b.nutrition(healing);
		if(Float.isNaN(saturation))
			b.saturationMod(0);
		else
			b.saturationMod(saturation);
		b.alwaysEat();
		return b.build();
	}

	@Override
	public List<Pair<Supplier<MobEffectInstance>, Float>> getEffects() {
		List<Pair<Supplier<MobEffectInstance>, Float>> li=new ArrayList<>();
		for (MobEffectInstance eff : effects) {
			if (eff != null) {
				li.add(Pair.of(()->new MobEffectInstance(eff), 1f));
			}
		}
		for (Pair<MobEffectInstance, Float> ef : foodeffect) {
			li.add(Pair.of(()->new MobEffectInstance(ef.getFirst()), ef.getSecond()));
		}
		return null;
	}
}
