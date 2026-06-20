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
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.joml.Vector3f;

import com.khjxiaogu.convivium.CVFluids;
import com.khjxiaogu.convivium.CVMain;
import com.khjxiaogu.convivium.data.recipes.BeverageTypeRecipe;
import com.khjxiaogu.convivium.data.recipes.RelishFluidRecipe;
import com.khjxiaogu.convivium.data.recipes.RelishRecipe;
import com.khjxiaogu.convivium.data.recipes.SwayRecipe;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teammoeg.caupona.client.util.FluidRenderHelper;
import com.teammoeg.caupona.components.IFoodInfo;
import com.teammoeg.caupona.util.ChancedEffect;
import com.teammoeg.caupona.util.FloatemStack;
import com.teammoeg.caupona.util.Utils;

import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntRBTreeMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
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
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.fluids.FluidStack;

public class BeverageInfo implements IFoodInfo,TooltipProvider {
	public List<FloatemStack> stacks;
	public Object2FloatOpenHashMap<String> variants;
	public List<ChancedEffect> effects;
	public List<ChancedEffect> swayeffects;
	public List<ChancedEffect> foodeffect;
	public Object2IntRBTreeMap<Holder<Fluid>> relishes=new Object2IntRBTreeMap<Holder<Fluid>>(Comparator.comparing(t->t.getKey()));
	public List<String> activeRelish = new ArrayList<>(3);

	public BeverageInfo() {
		effects = new ArrayList<>();
		swayeffects = new ArrayList<>();
		stacks = new ArrayList<>();
		variants=new Object2FloatOpenHashMap<>();
		foodeffect = new ArrayList<>();
	}

	public static final Codec<BeverageInfo> CODEC = RecordCodecBuilder.create(t -> t.group(
		Codec.list(FloatemStack.CODEC).fieldOf("items").forGetter(o -> o.stacks),
		SUtils.VARIANTS_CODEC.fieldOf("variants").forGetter(o->o.variants),
		Codec.list(ChancedEffect.CODEC).fieldOf("effects").forGetter(o -> o.effects),
		Codec.list(ChancedEffect.CODEC).fieldOf("sway").forGetter(o -> o.swayeffects),
		Codec.list(ChancedEffect.CODEC).fieldOf("foodeffects").forGetter(o -> o.foodeffect),
		Codec.unboundedMap(BuiltInRegistries.FLUID.holderByNameCodec(), Codec.INT).fieldOf("relish").forGetter(o->o.relishes),
		Codec.list(Codec.STRING,0,2).fieldOf("activeRelish").forGetter(o -> o.activeRelish)).apply(t, BeverageInfo::new));
	public static final StreamCodec<RegistryFriendlyByteBuf,BeverageInfo> STREAM_CODEC = StreamCodec.composite(
		FloatemStack.STREAM_CODEC.apply(ByteBufCodecs.list()),o -> o.stacks,
		SUtils.VARIANTS_STREAM_CODEC,o -> o.variants,
		ChancedEffect.STREAM_CODEC.apply(ByteBufCodecs.list()),o -> o.effects,
		ChancedEffect.STREAM_CODEC.apply(ByteBufCodecs.list()),o -> o.swayeffects,
		ChancedEffect.STREAM_CODEC.apply(ByteBufCodecs.list()),o -> o.foodeffect,
		ByteBufCodecs.map(HashMap::new, ByteBufCodecs.holderRegistry(Registries.FLUID), ByteBufCodecs.INT),o->o.relishes,
		ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list(2)),o -> o.activeRelish,
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

	public BeverageInfo(List<FloatemStack> stacks,Map<String,Float> variants, List<ChancedEffect> effects, List<ChancedEffect> swayeffects, List<ChancedEffect> foodeffect, Map<Holder<Fluid>,Integer> relishes, List<String> activeRelish) {
		super();
		this.stacks = stacks;
		this.effects = effects;
		this.variants=new Object2FloatOpenHashMap<>(variants);
		this.swayeffects = swayeffects;
		this.foodeffect = foodeffect;
		this.relishes.putAll(relishes);
		this.activeRelish.addAll(activeRelish);
	}
	public BeverageInfo copy() {
		return new BeverageInfo(stacks.stream().map(t->t.copy()).collect(Collectors.toList()),
				variants,
			effects.stream().map(t->t.copy()).collect(Collectors.toList()),
			swayeffects.stream().map(t->t.copy()).collect(Collectors.toList()),
			foodeffect.stream().map(t->t.copy()).collect(Collectors.toList()),
			relishes,activeRelish);
	}
	public int getIColor() {
		return getIColor(relishes);
	}
	public int addableRelish(int origAmt,int toMixAmt) {
		int remain=5-toMixAmt;
		if(relishes.size()<=1)
			return Math.min(remain,origAmt);
		int total=0;
		for(int ent:relishes.values()) {
			total+=ent;
		}
		if(total==5)
			return 0;
		
		if(total==origAmt) {
			if(remain==origAmt)
				return remain;
			return 0;
		}
		if(relishes.size()==2&&total*2==origAmt) {
			if(remain==4)
				return origAmt;
			if(remain==2)
				return remain;
		}
		return 0;
	}
	public int addableRelish(int origAmt) {
		if(relishes.size()<=1)
			return 5-origAmt;
		int total=0;
		for(int ent:relishes.values()) {
			total+=ent;
		}
		if(total==5)
			return 0;
		if(total==origAmt||(relishes.size()==2&&total*2==origAmt)) {
			return 5-origAmt;
		}
		return 0;
	}
	public void addRelish(int origAmt,Holder<Fluid> added,int addAmt) {
		Object2IntOpenHashMap<Holder<Fluid>> relishes=new Object2IntOpenHashMap<Holder<Fluid>>();
		relishes.putAll(this.relishes);
		int total=0;
		for(int ent:relishes.values()) {
			total+=ent;
		}
		final int ftotal=total;
		relishes.replaceAll((_,t)->t*origAmt/ftotal);
		relishes.mergeInt(added, addAmt, (a,b)->a+b);
		completeRatio(relishes);
		this.relishes.clear();
		this.relishes.putAll(relishes);
	}
	public void removeRelish(int origAmt,Holder<Fluid> added,int remAmt) {
		if(relishes.size()==1) {
			if(origAmt<=remAmt) {
				relishes.clear();
				return;
			}
			return;
		}
		Object2IntOpenHashMap<Holder<Fluid>> relishes=new Object2IntOpenHashMap<Holder<Fluid>>();
		relishes.putAll(this.relishes);
		int total=0;
		for(int ent:relishes.values()) {
			total+=ent;
		}
		final int ftotal=total;
		relishes.replaceAll((_,t)->t*origAmt/ftotal);
		relishes.mergeInt(added, -remAmt, (a,b)->a+b);
		relishes.values().removeIf(t->t<=0);
		completeRatio(relishes);
		this.relishes.clear();
		this.relishes.putAll(relishes);
	}
	public void exchangeRelish(int origAmt,Holder<Fluid> original,Holder<Fluid> added,int remAmt) {
		Object2IntOpenHashMap<Holder<Fluid>> relishes=new Object2IntOpenHashMap<Holder<Fluid>>();
		relishes.putAll(this.relishes);
		int total=0;
		for(int ent:relishes.values()) {
			total+=ent;
		}
		final int ftotal=total;
		relishes.replaceAll((_,t)->t*origAmt/ftotal);
		relishes.mergeInt(original, -remAmt, (a,b)->a+b);
		relishes.mergeInt(added, remAmt, (a,b)->a+b);
		relishes.values().removeIf(t->t<=0);
		completeRatio(relishes);
		this.relishes.clear();
		this.relishes.putAll(relishes);
	}
	public void addRelishes(int origAmt,Object2IntMap<Holder<Fluid>> added,int addAmt) {
		Object2IntOpenHashMap<Holder<Fluid>> relishes=new Object2IntOpenHashMap<Holder<Fluid>>();
		relishes.putAll(this.relishes);
		int total=0;
		for(int ent:relishes.values()) {
			total+=ent;
		}
		final int ftotal=total;
		int atotal=0;
		for(int ent:added.values()) {
			atotal+=ent;
		}
		final int fatotal=atotal;
		relishes.replaceAll((_,t)->t*origAmt/ftotal);
		for(Entry<Holder<Fluid>> ent:added.object2IntEntrySet())
			relishes.mergeInt(ent.getKey(), ent.getIntValue()*addAmt/fatotal, (a,b)->a+b);
		completeRatio(relishes);
		this.relishes.clear();
		this.relishes.putAll(relishes);
	}
	public static void completeRatio(Object2IntMap<Holder<Fluid>> map) {
		if(map.size()==1) {
			map.replaceAll((_,_)->1);
		}else
		if(map.size()==2) {
			for(int ent:map.values()) {
				if(ent!=2)
					return;
			}
			map.replaceAll((_,_)->1);
		}
	}
	public static int getIColor(Object2IntMap<Holder<Fluid>> relishes) {
		
		Vector3f clr = new Vector3f();
		int cnt = 0;
		for (Entry<Holder<Fluid>> ent:relishes.object2IntEntrySet()) {
			Fluid f = ent.getKey().value();
			if (f != null) {
				FluidStack fs=new FluidStack(f,1000);
				clr.add(ARGB.vector3fFromRGB24(FluidRenderHelper.getFluidColor(FluidRenderHelper.getFluidModel(fs), fs)).mul(ent.getIntValue()));
				cnt+=ent.getIntValue();
			}
		}
		if (cnt == 0) cnt = 1;
		clr = clr.div(cnt);
		return ARGB.colorFromFloat(1f, clr.x, clr.y, clr.z);
	}

	public float getDensity() {
		return stacks.stream().map(FloatemStack::getCount).reduce(0f, Float::sum);
	}

	public int getRelishCount() {
		int num=0;
		for(int i:relishes.values()) {
			num+=i;
		}
		return num;
	}

	public Pair<List<CurrentSwayInfo>, Either<BeverageTypeRecipe, Fluid>> adjustParts(float oparts, float parts) {
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

	public Pair<List<CurrentSwayInfo>, Either<BeverageTypeRecipe,Fluid>> handleSway() {
		BeveragePendingContext ctx = new BeveragePendingContext(this);
		swayeffects.clear();
		List<CurrentSwayInfo> swi = SwayRecipe.recipes.stream().map(t -> t.value()).map(ctx::handleSwayRecipe).flatMap(Optional::stream)
			.map(t -> {
				for(MobEffectInstance me:t.getFirst())
					swayeffects.add(new ChancedEffect(me,1f));
				return t.getSecond();
			})
			.flatMap(Optional::stream)
			.sorted((t2, t1) -> Mth.ceil(t1.getDisplay() - t2.getDisplay()))
			.collect(Collectors.toList());
		swayeffects.sort(
			Comparator.<ChancedEffect, String>comparing(e -> e.effect.getEffect().getRegisteredName())
			.thenComparing(e -> e.chance));
		recalculateHAS();
		return Pair.of(swi,
			BeverageTypeRecipe.sorted.stream().map(t -> t.value()).filter(t -> t.matches(ctx)).<Either<BeverageTypeRecipe,Fluid>>map(t -> Either.left(t)).findFirst()
				.orElse(Either.right(CVFluids.mixedf.get())));
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
		return 0;
	}

	@Override
	public float getSaturation() {
		return 0;
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
		for (FloatemStack fs : stacks) {
			Consumable c = fs.getStack().getComponents().get(DataComponents.CONSUMABLE);
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
	public Builder getFood(int extraHealing, int extraSaturation) {
		FoodProperties.Builder b = new FoodProperties.Builder();
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
	private static String handleNumber(float value) {
		int rv=((int)value);
		if(rv>0)
			return "+"+rv;
		if(rv<0)
			return "-"+rv;
		return " 0";
	}
	@Override
	public void addToTooltip(TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag, DataComponentGetter components) {
		RecipeHolder<RelishRecipe> r1 = RelishRecipe.recipes.get(activeRelish.get(0));
		
		RecipeHolder<RelishRecipe> r2 =null;
		if(activeRelish.size()>1)
			r2 = RelishRecipe.recipes.get(activeRelish.get(1));
		if(!this.relishes.isEmpty()) {
			MutableComponent comp=Component.literal("").withStyle(Style.EMPTY.withFont(new FontDescription.Resource(CVMain.rl("relish"))));
			for(Entry<Holder<Fluid>> relish:this.relishes.object2IntEntrySet()) {
				RecipeHolder<RelishFluidRecipe> rcp=RelishFluidRecipe.recipes.get(relish.getKey());
				if(rcp!=null) {
					RecipeHolder<RelishRecipe> rp=RelishRecipe.recipes.get(rcp.value().relish);
					if(rp!=null) {
						for(int i=0;i<relish.getIntValue();i++)
							comp.append(rp.value().relishFont);
						continue;
					}
				}
				for(int i=0;i<relish.getIntValue();i++)
					comp.append("n");
			}
			tooltipAdder.accept(Component.translatable("tooltip.convivium.relishes").append(comp));
		}
		MutableComponent comp2=Component.literal("");
		comp2.append(Component.translatable("tooltip.convivium.taste.sweetness",handleNumber(variants.getFloat("sweetness")))).append(Component.literal(" "));
		comp2.append(Component.translatable("tooltip.convivium.taste.astringency",handleNumber(variants.getFloat("astringency")))).append(Component.literal(" "));
		comp2.append(Component.translatable("tooltip.convivium.taste.pungency",handleNumber(variants.getFloat("pungency")))).append(Component.literal(" "));
		comp2.append(Component.translatable("tooltip.convivium.taste.thickness",handleNumber(variants.getFloat("thickness")))).append(Component.literal(" "));
		comp2.append(Component.translatable("tooltip.convivium.taste.soothingness",handleNumber(variants.getFloat("soothingness")))).append(Component.literal(" "));
		tooltipAdder.accept(comp2);
		if (r1 != null) {
			if (r2 != null) {
				tooltipAdder.accept(Utils.translate("tooltip." + CVMain.MODID + ".major_relish_2", r1.value().getText(), r2.value().getText()));
			} else
				tooltipAdder.accept(Utils.translate("tooltip." + CVMain.MODID + ".major_relish_1", r1.value().getText()));
		}
		if (!effects.isEmpty())
			PotionContents.addPotionTooltip(potionEffectsCollectionView.get(), tooltipAdder, 1, 20);

	}

	@Override
	public int hashCode() {
		return Objects.hash(effects, relishes, stacks);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		BeverageInfo other = (BeverageInfo) obj;
		return Objects.equals(effects, other.effects) && Objects.equals(relishes, other.relishes) && Objects.equals(stacks, other.stacks);
	}




}
