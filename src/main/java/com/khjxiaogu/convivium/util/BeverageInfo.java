package com.khjxiaogu.convivium.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.mojang.datafixers.util.Pair;
import com.teammoeg.caupona.data.SerializeUtil;
import com.teammoeg.caupona.util.FloatemStack;
import com.teammoeg.caupona.util.IFoodInfo;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;

public class BeverageInfo implements IFoodInfo {
	public List<FloatemStack> stacks;
	public List<MobEffectInstance> effects;
	public List<Pair<MobEffectInstance, Float>> foodeffect = new ArrayList<>();
	public String[] relishes=new String[5];
	public int healing;
	public float saturation;
	public BeverageInfo(CompoundTag nbt) {
		stacks = nbt.getList("items", 10).stream().map(e -> (CompoundTag) e).map(FloatemStack::new)
				.collect(Collectors.toList());
		healing = nbt.getInt("heal");
		saturation = nbt.getFloat("sat");
		foodeffect = nbt.getList("feffects", 10).stream().map(e -> (CompoundTag) e)
				.map(e -> new Pair<>(MobEffectInstance.load(e.getCompound("effect")), e.getFloat("chance")))
				.collect(Collectors.toList());
		effects = nbt.getList("effects", 10).stream().map(e->MobEffectInstance.load((CompoundTag)e))
				.collect(Collectors.toList());
		ListTag list=nbt.getList("relish",8);
		for(int i=0;i<5;i++) {
			relishes[i]=list.getString(i);
		}
	}
	public void write(CompoundTag nbt) {
		nbt.put("items", SerializeUtil.toNBTList(stacks, FloatemStack::serializeNBT));
		nbt.put("feffects", SerializeUtil.toNBTList(foodeffect, e -> {
			CompoundTag cnbt = new CompoundTag();
			cnbt.put("effect", e.getFirst().save(new CompoundTag()));
			cnbt.putFloat("chance", e.getSecond());
			return cnbt;
		}));
		nbt.put("effects", SerializeUtil.toNBTList(effects, e -> e.save(new CompoundTag())));
		ListTag relishes=new ListTag();
		for(String s:this.relishes) {
			relishes.add(StringTag.valueOf(s));
		}
		nbt.put("relish", relishes);
		nbt.putInt("heal", healing);
		nbt.putFloat("sat", saturation);
	}
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
