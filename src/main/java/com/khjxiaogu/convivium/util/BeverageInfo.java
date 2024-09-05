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

package com.khjxiaogu.convivium.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.joml.Vector3f;

import com.khjxiaogu.convivium.CVFluids;
import com.khjxiaogu.convivium.CVMain;
import com.khjxiaogu.convivium.data.recipes.BeverageTypeRecipe;
import com.khjxiaogu.convivium.data.recipes.RelishRecipe;
import com.khjxiaogu.convivium.data.recipes.SwayRecipe;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teammoeg.caupona.components.IFoodInfo;
import com.teammoeg.caupona.data.recipes.FoodValueRecipe;
import com.teammoeg.caupona.util.ChancedEffect;
import com.teammoeg.caupona.util.FloatemStack;
import com.teammoeg.caupona.util.SerializeUtil;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.FoodProperties.PossibleEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;

public class BeverageInfo implements IFoodInfo {
	public List<FloatemStack> stacks;
	public List<MobEffectInstance> effects;
	public List<MobEffectInstance> swayeffects;
	public List<ChancedEffect> foodeffect;
	public Fluid[] relishes = new Fluid[5];
	public String activeRelish1 = "";
	public String activeRelish2 = "";
	public int healing;
	public float saturation;
	private int heat;

	public BeverageInfo() {
		effects = new ArrayList<>();
		swayeffects = new ArrayList<>();
		stacks = new ArrayList<>();
		foodeffect = new ArrayList<>();
	}

	public static Codec<BeverageInfo> CODEC = RecordCodecBuilder.create(t -> t.group(
		Codec.list(FloatemStack.CODEC).fieldOf("items").forGetter(o -> o.stacks),
		Codec.list(SerializeUtil.fromRFBBStreamCodec(MobEffectInstance.STREAM_CODEC, MobEffectInstance.CODEC)).fieldOf("effects").forGetter(o -> o.effects),
		Codec.list(SerializeUtil.fromRFBBStreamCodec(MobEffectInstance.STREAM_CODEC, MobEffectInstance.CODEC)).fieldOf("sway").forGetter(o -> o.swayeffects),
		Codec.list(ChancedEffect.CODEC).fieldOf("feffects").forGetter(o -> o.foodeffect),
		Codec.list(SerializeUtil.idOrKey(BuiltInRegistries.FLUID)).fieldOf("relish").forGetter(o -> Arrays.stream(o.relishes).map(n->n==null?Fluids.EMPTY:n).collect(Collectors.toList())),
		Codec.STRING.fieldOf("activeRelish1").forGetter(o -> o.activeRelish1),
		Codec.STRING.fieldOf("activeRelish2").forGetter(o -> o.activeRelish2),
		Codec.INT.fieldOf("heal").forGetter(o -> o.healing),
		Codec.FLOAT.fieldOf("sat").forGetter(o -> o.saturation),
		Codec.INT.fieldOf("heat").forGetter(o -> o.heat)).apply(t, BeverageInfo::new));

	public BeverageInfo(List<FloatemStack> stacks, List<MobEffectInstance> effects, List<MobEffectInstance> swayeffects, List<ChancedEffect> foodeffect, List<Fluid> relishes, String activeRelish1,
		String activeRelish2, int healing, float saturation, int heat) {
		super();
		
		this.stacks = stacks;
		this.effects = effects;
		this.swayeffects = swayeffects;
		this.foodeffect = foodeffect;
		this.relishes = relishes.stream().map(t->t==Fluids.EMPTY?null:t).toArray(Fluid[]::new);
		this.activeRelish1 = activeRelish1;
		this.activeRelish2 = activeRelish2;
		this.healing = healing;
		this.saturation = saturation;
		this.heat = heat;
		
	}

	public BeverageInfo(List<FloatemStack> stacks, List<MobEffectInstance> effects, List<MobEffectInstance> swayeffects, List<ChancedEffect> foodeffect, Fluid[] relishes, String activeRelish1,
		String activeRelish2, int healing, float saturation, int heat) {
		super();
		this.stacks = stacks;
		this.effects = effects;
		this.swayeffects = swayeffects;
		this.foodeffect = foodeffect;
		this.relishes = relishes;
		this.activeRelish1 = activeRelish1;
		this.activeRelish2 = activeRelish2;
		this.healing = healing;
		this.saturation = saturation;
		this.heat = heat;
	}

	public BeverageInfo copy() {
		return new BeverageInfo(stacks.stream().map(t->t.copy()).collect(Collectors.toList()),effects.stream().map(t->new MobEffectInstance(t)).collect(Collectors.toList()),new ArrayList<>(swayeffects),foodeffect.stream().map(t->t.copy()).collect(Collectors.toList()),relishes,activeRelish1,activeRelish2,healing,saturation,heat);
	}
	public void appendTooltip(List<Component> tt) {
		RecipeHolder<RelishRecipe> r1 = RelishRecipe.recipes.get(activeRelish1);
		RecipeHolder<RelishRecipe> r2 = RelishRecipe.recipes.get(activeRelish2);
		if (!effects.isEmpty())
			PotionContents.addPotionTooltip(effects, tt::add, 1, 20);

		if (r1 != null) {
			if (r2 != null) {
				tt.add(Utils.translate("tooltip." + CVMain.MODID + ".major_relish_2", r1.value().getText(), r2.value().getText()));
			} else
				tt.add(Utils.translate("tooltip." + CVMain.MODID + ".major_relish_1", r1.value().getText()));
		}
	}

	public Vector3f getColor() {
		return getColor(relishes);
	}

	public int getIColor() {
		return getIColor(relishes);
	}

	public static Vector3f getColor(Fluid[] relishes) {
		Vector3f clr = new Vector3f();
		int cnt = 0;
		for (int i = 0; i < 5; i++) {
			Fluid f = relishes[i];
			if (f != null) {
				clr.add(tclr(IClientFluidTypeExtensions.of(f).getTintColor()));
				cnt++;
			}
		}
		if (cnt == 0) cnt = 1;
		clr = clr.div(cnt);
		return clr;
	}

	private static Vector3f tclr(int col) {
		return new Vector3f((col >> 16 & 255) / 255.0f, (col >> 8 & 255) / 255.0f, (col & 255) / 255.0f);
	}

	public static int getIColor(Fluid[] relishes) {
		Vector3f clr = getColor(relishes);

		return 0xff << 24 | ((int) (clr.x * 0xff)) << 16 | ((int) (clr.y * 0xff)) << 8 | ((int) (clr.z * 0xff));
	}

	public float getDensity() {
		return stacks.stream().map(FloatemStack::getCount).reduce(0f, Float::sum);
	}

	public int getRelishCount() {
		for (int i = 0; i < 5; i++) {
			if (relishes[i] == null)
				return i;
		}
		return 5;
	}

	public Pair<List<CurrentSwayInfo>, Fluid> adjustParts(float oparts, float parts) {
		for (FloatemStack fs : stacks) {
			fs.setCount(fs.getCount() * oparts / parts);
		}

		for (MobEffectInstance es : effects) {
			es.duration = (int) (es.duration * oparts / parts);
		}
		for (ChancedEffect es : foodeffect) {
			es.effect.duration = (int) (es.effect.duration * oparts / parts);
		}
		heat = (int) (heat * oparts / parts);

		completeData();
		recalculateHAS();
		return this.handleSway();
	}

	public static boolean isEffectEquals(MobEffectInstance t1, MobEffectInstance t2) {
		return t1.getEffect() == t2.getEffect() && t1.getAmplifier() == t2.getAmplifier();
	}

	public void addEffect(MobEffectInstance eff, float parts) {
		for (MobEffectInstance oes : effects) {
			if (isEffectEquals(oes, eff)) {
				oes.duration = Math.max(oes.duration,
					(int) Math.min(oes.duration + eff.duration / parts, eff.duration * 2f));
				return;
			}
		}
		if (effects.size() < 3) {
			MobEffectInstance copy = new MobEffectInstance(eff);
			copy.duration /= parts;
			effects.add(copy);
		}
	}

	public Pair<List<CurrentSwayInfo>, Fluid> handleSway() {
		BeveragePendingContext ctx = new BeveragePendingContext(this);
		swayeffects.clear();
		List<CurrentSwayInfo> swi = SwayRecipe.recipes.stream().map(t -> t.value()).map(ctx::handleSwayRecipe).flatMap(Optional::stream)
			.map(t -> {
				swayeffects.addAll(t.getFirst());
				return t.getSecond();
			})
			.flatMap(Optional::stream)
			.sorted((t2, t1) -> Mth.ceil(t1.display - t2.display))
			.collect(Collectors.toList());
		swayeffects.sort(Comparator.<MobEffectInstance, String>comparing(e -> e.getEffect().getRegisteredName())
			.thenComparingInt(e -> e.getAmplifier()).thenComparingInt(e -> e.getDuration()));
		recalculateHAS();
		return Pair.of(swi,
			BeverageTypeRecipe.sorted.stream().map(t -> t.value()).filter(t -> t.matches(ctx)).map(t -> t.output).findFirst()
				.orElse(CVFluids.mixedf.get()));
	}

	public Fluid checkFluidType() {
		BeveragePendingContext ctx = new BeveragePendingContext(this);
		return BeverageTypeRecipe.sorted.stream().map(t -> t.value()).filter(t -> t.matches(ctx)).map(t -> t.output).findFirst()
			.orElse(CVFluids.mixedf.get());
	}

	public void merge(BeverageInfo f, float cparts, float oparts) {

		for (MobEffectInstance es : f.effects) {
			boolean added = false;
			for (MobEffectInstance oes : effects) {
				if (isEffectEquals(oes, es)) {
					oes.duration += es.duration * oparts / cparts;
					added = true;
					break;
				}
			}
			if (!added) {
				if (effects.size() < 3) {
					MobEffectInstance copy = new MobEffectInstance(es);
					copy.duration = (int) (copy.duration * oparts / cparts);
					effects.add(copy);
				}
			}
		}
		for (FloatemStack fs : f.stacks) {
			this.addItem(new FloatemStack(fs.getStack(), fs.getCount() * oparts / cparts));
		}
		heat += f.heat * oparts / cparts;

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
		for (ChancedEffect ef : foodeffect) {
			b.effect(ef.effectSupplier(), ef.chance);
		}
		for (MobEffectInstance ef : effects) {
			b.effect(() -> new MobEffectInstance(ef), 1);
		}
		for (MobEffectInstance ef : swayeffects) {
			b.effect(() -> new MobEffectInstance(ef), 1);
		}
		b.nutrition(healing);
		if (Float.isNaN(saturation))
			b.saturationModifier(0);
		else
			b.saturationModifier(saturation);
		b.alwaysEdible();
		return b.build();
	}

	@Override
	public List<PossibleEffect> getEffects() {
		List<PossibleEffect> li = new ArrayList<>();
		for (MobEffectInstance eff : effects) {
			if (eff != null) {
				li.add(new PossibleEffect(() -> new MobEffectInstance(eff), 1f));
			}
		}
		for (ChancedEffect ef : foodeffect) {
			li.add(new PossibleEffect(ef.effectSupplier(), ef.chance));
		}
		for (MobEffectInstance eff : swayeffects) {
			if (eff != null) {
				li.add(new PossibleEffect(() -> new MobEffectInstance(eff), 1f));
			}
		}
		return li;
	}

	public void completeData() {
		stacks.removeIf(t->t.isEmpty());
		stacks.sort(Comparator.<FloatemStack>comparingDouble(e -> e.getCount()).thenComparingInt(t -> Item.getId(t.getItem())));
		foodeffect.sort(
			Comparator.<ChancedEffect, String>comparing(e -> e.effect.getEffect().getRegisteredName())
				.thenComparing(e -> e.chance));
		effects.sort(
			Comparator.<MobEffectInstance, String>comparing(e -> e.getEffect().getRegisteredName())
				.thenComparingInt(e -> e.getAmplifier()).thenComparingInt(e -> e.getDuration()));
	}

	public void recalculateHAS() {
		foodeffect.clear();
		float nh = 0;
		float ns = 0;
		for (FloatemStack fs : stacks) {
			FoodValueRecipe fvr = FoodValueRecipe.recipes.get(fs.getItem());
			if (fvr != null) {
				nh += fvr.heal * fs.getCount();
				ns += fvr.sat * fs.getCount() * fvr.heal;
				if (fvr.effects != null)
					fvr.effects.stream().map(ChancedEffect::new).forEach(foodeffect::add);
				continue;
			}
			FoodProperties f = fs.getStack().getFoodProperties(null);
			if (f != null) {
				nh += fs.getCount() * f.nutrition();
				ns += fs.getCount() * f.saturation();
				f.effects().stream().map(ChancedEffect::new).forEach(foodeffect::add);
			}
		}
		int conv = (int) (0.075 * nh);
		this.healing = (int) Math.ceil(nh - conv);
		ns += conv / 2f;
		if (this.healing > 0)
			this.saturation = Math.max(0.6f, ns / this.healing);
		else
			this.saturation = 0;
	}

	public boolean addItem(ItemStack is, float parts) {
		for (FloatemStack i : stacks) {
			if (i.equals(is)) {
				i.setCount(i.getCount() + is.getCount() / parts);
				return true;
			}
		}
		if(stacks.size()>=20)
			return false;
		stacks.add(new FloatemStack(is.copy(), is.getCount() / parts));
		return true;
	}

	public boolean addItem(FloatemStack is) {
		for (FloatemStack i : stacks) {
			if (i.equals(is.getStack())) {
				i.setCount(i.getCount() + is.getCount());
				return true;
			}
		}
		if(stacks.size()>=20)
			return false;
		stacks.add(is);
		return true;
	}

	public BeverageInfo(List<FloatemStack> stacks, List<MobEffectInstance> effects, List<MobEffectInstance> swayeffects, List<ChancedEffect> foodeffect, Fluid[] relishes, int healing,
		float saturation, int heat) {
		super();
		this.stacks = stacks;
		this.effects = effects;
		this.swayeffects = swayeffects;
		this.foodeffect = foodeffect;
		this.relishes = relishes;
		this.healing = healing;
		this.saturation = saturation;
		this.heat = heat;
	}

	@Override
	public Fluid getBase() {
		
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(relishes);
		result = prime * result + Objects.hash(effects, foodeffect, healing, heat, saturation, stacks, swayeffects);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		BeverageInfo other = (BeverageInfo) obj;
		return Objects.equals(effects, other.effects) && Objects.equals(foodeffect, other.foodeffect) && healing == other.healing && heat == other.heat && Arrays.equals(relishes, other.relishes)
			&& Float.floatToIntBits(saturation) == Float.floatToIntBits(other.saturation) && Objects.equals(stacks, other.stacks) && Objects.equals(swayeffects, other.swayeffects);
	}


}
