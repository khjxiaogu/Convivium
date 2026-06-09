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

package com.khjxiaogu.convivium.util;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.joml.Vector3f;

import com.khjxiaogu.convivium.CVFluids;
import com.khjxiaogu.convivium.CVMain;
import com.khjxiaogu.convivium.data.recipes.BeverageTypeRecipe;
import com.khjxiaogu.convivium.data.recipes.RelishRecipe;
import com.khjxiaogu.convivium.data.recipes.SwayRecipe;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teammoeg.caupona.client.util.FluidRenderHelper;
import com.teammoeg.caupona.components.IFoodInfo;
import com.teammoeg.caupona.data.recipes.FoodValueRecipe;
import com.teammoeg.caupona.util.ChancedEffect;
import com.teammoeg.caupona.util.FloatemStack;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.FoodProperties.Builder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.fluids.FluidStack;

public class BeverageInfo implements IFoodInfo,TooltipProvider {
	public List<FloatemStack> stacks;
	public List<ChancedEffect> effects;
	public List<ChancedEffect> swayeffects;
	public List<ChancedEffect> foodeffect;
	public Fluid[] relishes = new Fluid[5];
	public String activeRelish1 = "";
	public String activeRelish2 = "";
	public int healing;
	public float saturation;

	public BeverageInfo() {
		effects = new ArrayList<>();
		swayeffects = new ArrayList<>();
		stacks = new ArrayList<>();
		foodeffect = new ArrayList<>();
	}

	public static final Codec<BeverageInfo> CODEC = RecordCodecBuilder.create(t -> t.group(
		Codec.list(FloatemStack.CODEC).fieldOf("items").forGetter(o -> o.stacks),
		Codec.list(ChancedEffect.CODEC).fieldOf("effects").forGetter(o -> o.effects),
		Codec.list(ChancedEffect.CODEC).fieldOf("sway").forGetter(o -> o.swayeffects),
		Codec.list(ChancedEffect.CODEC).fieldOf("feffects").forGetter(o -> o.foodeffect),
		Codec.list(BuiltInRegistries.FLUID.byNameCodec().<Optional<Fluid>>xmap(o->o==Fluids.EMPTY?Optional.empty():Optional.of(o), o->o.orElse(Fluids.EMPTY))).fieldOf("relish").forGetter(o->o.getRelishList()),
		Codec.STRING.fieldOf("activeRelish1").forGetter(o -> o.activeRelish1),
		Codec.STRING.fieldOf("activeRelish2").forGetter(o -> o.activeRelish2),
		Codec.INT.fieldOf("heal").forGetter(o -> o.healing),
		Codec.FLOAT.fieldOf("sat").forGetter(o -> o.saturation)).apply(t, BeverageInfo::new));
	public static final StreamCodec<RegistryFriendlyByteBuf,BeverageInfo> STREAM_CODEC = StreamCodec.composite(
		FloatemStack.STREAM_CODEC.apply(ByteBufCodecs.list()),o -> o.stacks,
		ChancedEffect.STREAM_CODEC.apply(ByteBufCodecs.list()),o -> o.effects,
		ChancedEffect.STREAM_CODEC.apply(ByteBufCodecs.list()),o -> o.swayeffects,
		ChancedEffect.STREAM_CODEC.apply(ByteBufCodecs.list()),o -> o.foodeffect,
		ByteBufCodecs.optional(ByteBufCodecs.registry(Registries.FLUID)).apply(ByteBufCodecs.list()),o->o.getRelishList(),
		ByteBufCodecs.STRING_UTF8,o -> o.activeRelish1,
		ByteBufCodecs.STRING_UTF8,o -> o.activeRelish2,
		ByteBufCodecs.INT,o -> o.healing,
		ByteBufCodecs.FLOAT,o -> o.saturation,
		BeverageInfo::new);
	private Lazy<Collection<MobEffectInstance>> potionEffectsCollectionView=Lazy.of(()->new AbstractCollection<MobEffectInstance>() {
		@Override
		public Iterator<MobEffectInstance> iterator() {
			return Stream.concat(effects.stream(), swayeffects.stream()).map(t->t.effect).iterator();
		}
		@Override
		public int size() {
			return effects.size()+swayeffects.size();
		}
		@Override
		public boolean isEmpty() {
			return effects.isEmpty()&&swayeffects.isEmpty();
		}
		@Override
		public Object[] toArray() {
			return Stream.concat(effects.stream(), swayeffects.stream()).map(t->t.effect).toArray();
		}
		@Override
		public boolean add(MobEffectInstance e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(Object o) {
			throw new UnsupportedOperationException();
		}
		@Override
		public boolean addAll(Collection<? extends MobEffectInstance> c) {
			throw new UnsupportedOperationException();
		}
		@Override
		public boolean removeAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}
		@Override
		public boolean retainAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}
		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}
	});
	public Collection<MobEffectInstance> getPotionEffects() {
		return potionEffectsCollectionView.get();
	}

	public BeverageInfo(List<FloatemStack> stacks, List<ChancedEffect> effects, List<ChancedEffect> swayeffects, List<ChancedEffect> foodeffect, Fluid[] relishes, String activeRelish1,
		String activeRelish2, int healing, float saturation) {
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
	}

	public BeverageInfo(List<FloatemStack> stacks, List<ChancedEffect> effects, List<ChancedEffect> swayeffects, List<ChancedEffect> foodeffect, Fluid[] relishes, int healing,
		float saturation) {
		super();
		this.stacks = stacks;
		this.effects = effects;
		this.swayeffects = swayeffects;
		this.foodeffect = foodeffect;
		this.relishes = relishes;
		this.healing = healing;
		this.saturation = saturation;
	}
	public BeverageInfo(List<FloatemStack> stacks, List<ChancedEffect> effects, List<ChancedEffect> swayeffects, List<ChancedEffect> foodeffect, List<Optional<Fluid>> relishes, String activeRelish1,
		String activeRelish2, int healing, float saturation) {
		super();
		this.stacks = stacks;
		this.effects = effects;
		this.swayeffects = swayeffects;
		this.foodeffect = foodeffect;
		int i=0;
		for(Optional<Fluid> relish:relishes) {
			this.relishes[i++]=relish.orElse(null);
		}
		this.activeRelish1 = activeRelish1;
		this.activeRelish2 = activeRelish2;
		this.healing = healing;
		this.saturation = saturation;
	}
	public BeverageInfo copy() {
		return new BeverageInfo(stacks.stream().map(t->t.copy()).toList(),
			effects.stream().map(t->t.copy()).toList(),
			swayeffects.stream().map(t->t.copy()).toList(),
			foodeffect.stream().map(t->t.copy()).toList(),
			Arrays.copyOf(relishes,5),activeRelish1,activeRelish2,healing,saturation);
	}
	public List<Optional<Fluid>> getRelishList(){
		return List.of(Optional.ofNullable(relishes[0]),
			Optional.ofNullable(relishes[1]),
			Optional.ofNullable(relishes[2]),
			Optional.ofNullable(relishes[3]),
			Optional.ofNullable(relishes[4]));
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
				FluidStack fs=new FluidStack(f,1000);
				
				clr.add(ARGB.vector3fFromRGB24(FluidRenderHelper.getFluidColor(FluidRenderHelper.getFluidModel(fs), fs)));
				cnt++;
			}
		}
		if (cnt == 0) cnt = 1;
		clr = clr.div(cnt);
		return clr;
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

		for (ChancedEffect es : effects) {
			es.adjustParts(oparts, parts);
		}
		for (ChancedEffect es : foodeffect) {
			es.adjustParts(oparts, parts);
		}

		completeData();
		recalculateHAS();
		return this.handleSway();
	}

	public static boolean isEffectEquals(MobEffectInstance t1, MobEffectInstance t2) {
		return t1.getEffect() == t2.getEffect() && t1.getAmplifier() == t2.getAmplifier();
	}

	public void addEffect(MobEffectInstance eff, float parts) {
		for (ChancedEffect oes : effects) {
			if (oes.add(eff, parts)) {
				return;
			}
		}
		if (effects.size() < 3) {
			MobEffectInstance copy = new MobEffectInstance(eff);
			copy.duration /= parts;
			effects.add(new ChancedEffect(copy,1f));
		}
	}

	public Pair<List<CurrentSwayInfo>, Fluid> handleSway() {
		BeveragePendingContext ctx = new BeveragePendingContext(this);
		swayeffects.clear();
		List<CurrentSwayInfo> swi = SwayRecipe.recipes.stream().map(t -> t.value()).map(ctx::handleSwayRecipe).flatMap(Optional::stream)
			.map(t -> {
				for(MobEffectInstance me:t.getFirst())
					swayeffects.add(new ChancedEffect(me,1f));
				return t.getSecond();
			})
			.flatMap(Optional::stream)
			.sorted((t2, t1) -> Mth.ceil(t1.display - t2.display))
			.collect(Collectors.toList());
		swayeffects.sort(
			Comparator.<ChancedEffect, String>comparing(e -> e.effect.getEffect().getRegisteredName())
			.thenComparing(e -> e.chance));
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

		for (ChancedEffect es : f.effects) {
			boolean added = false;
			for (ChancedEffect oes : effects) {
				if (oes.merge(es, cparts, oparts)) {
					added = true;
					break;
				}
			}
			if (!added) {
				if (effects.size() < 3) {
					ChancedEffect copy=es.copy();
					copy.adjustParts(oparts, cparts);
					effects.add(copy);
				}
			}
		}
		for (FloatemStack fs : f.stacks) {
			this.addItem(new FloatemStack(fs.getStack(), fs.getCount() * oparts / cparts));
		}

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


	@Override
	public List<ChancedEffect> getEffects() {
		List<ChancedEffect> li = new ArrayList<>(effects);
		li.addAll(foodeffect);
		li.addAll(swayeffects);
		return li;
	}

	public void completeData() {
		stacks.removeIf(t->t.isEmpty());
		stacks.sort(Comparator.<FloatemStack>comparingDouble(e -> e.getCount()).thenComparingInt(t -> Item.getId(t.getItem())));
		foodeffect.sort(
			Comparator.<ChancedEffect, String>comparing(e -> e.effect.getEffect().getRegisteredName())
				.thenComparing(e -> e.chance));
		effects.sort(
			Comparator.<ChancedEffect, String>comparing(e -> e.effect.getEffect().getRegisteredName())
			.thenComparing(e -> e.chance));
		
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
					fvr.effects.forEach(foodeffect::add);
				continue;
			}
			FoodProperties f = fs.getStack().getComponents().get(DataComponents.FOOD);
			Consumable c = fs.getStack().getComponents().get(DataComponents.CONSUMABLE);
			if (f != null) {
				nh += fs.count * f.nutrition();
				ns += fs.count * f.saturation();
			}
			if(c!=null) {
				c.onConsumeEffects().stream().<ChancedEffect>flatMap(t->{
					if(t instanceof ApplyStatusEffectsConsumeEffect eff) {
						float chance=eff.probability();
						return eff.effects().stream().map(o->new ChancedEffect(o,chance));
					}
					return Stream.empty();
				}).forEach(foodeffect::add);
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

	@Override
	public Fluid getBase() {
		
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(relishes);
		result = prime * result + Objects.hash(effects, foodeffect, healing, saturation, stacks, swayeffects);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		BeverageInfo other = (BeverageInfo) obj;
		return Objects.equals(effects, other.effects) && Objects.equals(foodeffect, other.foodeffect) && healing == other.healing && Arrays.equals(relishes, other.relishes)
			&& Float.floatToIntBits(saturation) == Float.floatToIntBits(other.saturation) && Objects.equals(stacks, other.stacks) && Objects.equals(swayeffects, other.swayeffects);
	}

	@Override
	public Builder getFood(int extraHealing, int extraSaturation) {
		FoodProperties.Builder b = new FoodProperties.Builder();
		b.nutrition(healing+extraHealing);
		float extraSat=0;
		if(healing+extraHealing>0) {
			extraSat=extraSaturation/(healing+extraHealing);
		}
		if(Float.isNaN(saturation))
			b.saturationModifier(extraSat);
		else
			b.saturationModifier(saturation+extraSat);
		b.alwaysEdible();
		return b;
	}
	@Override
	public Consumable.Builder getConsumable() {
		Consumable.Builder b=Consumable.builder()
		.consumeSeconds(1.6F)
		.animation(ItemUseAnimation.DRINK)
		.sound(SoundEvents.GENERIC_DRINK)
		.hasConsumeParticles(true);
		for (ChancedEffect eff : effects) {
			eff.toPossibleEffects(b);
		}
		for (ChancedEffect eff : swayeffects) {
			eff.toPossibleEffects(b);
		}
		for (ChancedEffect ef : foodeffect) {
			ef.toPossibleEffects(b);
		}
		return b;
	}
	@Override
	public void addToTooltip(TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag, DataComponentGetter components) {
		RecipeHolder<RelishRecipe> r1 = RelishRecipe.recipes.get(activeRelish1);
		RecipeHolder<RelishRecipe> r2 = RelishRecipe.recipes.get(activeRelish2);
		if (!effects.isEmpty())
			PotionContents.addPotionTooltip(potionEffectsCollectionView.get(), tooltipAdder, 1, 20);

		if (r1 != null) {
			if (r2 != null) {
				tooltipAdder.accept(Utils.translate("tooltip." + CVMain.MODID + ".major_relish_2", r1.value().getText(), r2.value().getText()));
			} else
				tooltipAdder.accept(Utils.translate("tooltip." + CVMain.MODID + ".major_relish_1", r1.value().getText()));
		}
		
	}

}
