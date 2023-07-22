package com.khjxiaogu.convivium.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.khjxiaogu.convivium.data.recipes.SwayRecipe;
import com.mojang.datafixers.util.Pair;
import com.teammoeg.caupona.data.SerializeUtil;
import com.teammoeg.caupona.data.recipes.FoodValueRecipe;
import com.teammoeg.caupona.util.FloatemStack;
import com.teammoeg.caupona.util.IFoodInfo;
import com.teammoeg.caupona.util.StewInfo;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;

public class BeverageInfo implements IFoodInfo {
	public List<FloatemStack> stacks;
	public List<MobEffectInstance> effects;
	public List<MobEffectInstance> swayeffects;
	public List<Pair<MobEffectInstance, Float>> foodeffect;
	public Fluid[] relishes=new Fluid[5];
	public int healing;
	public float saturation;
	public int heat;
	public BeverageInfo() {
		effects = new ArrayList<>();
		swayeffects = new ArrayList<>();
		stacks = new ArrayList<>();
		foodeffect = new ArrayList<>();
	}

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
		swayeffects=nbt.getList("sway", 10).stream().map(e->MobEffectInstance.load((CompoundTag)e))
				.collect(Collectors.toList());
		heat=nbt.getInt("heat");
		ListTag list=nbt.getList("relish",8);
		for(int i=0;i<5;i++) {
			relishes[i]=ForgeRegistries.FLUIDS.getValue(new ResourceLocation(list.getString(i)));
		}
	}
	public float getDensity() {
		return stacks.stream().map(FloatemStack::getCount).reduce(0f, Float::sum);
	}
	public int getRelishCount() {
		for(int i=0;i<5;i++) {
			if(relishes[i]==null)
				return i+1;
		}
		return 5;
	}
	public CompoundTag save() {
		CompoundTag tag=new CompoundTag();
		write(tag);
		return tag;
	}
	public List<CurrentSwayInfo> adjustParts(float oparts, float parts) {
		for (FloatemStack fs : stacks) {
			fs.setCount(fs.getCount() * oparts / parts);
		}

		for (MobEffectInstance es : effects) {
			es.duration = (int) (es.duration * oparts / parts);
		}
		for (Pair<MobEffectInstance, Float> es : foodeffect) {
			es.getFirst().duration = (int) (es.getFirst().duration * oparts / parts);
		}
		heat=(int) (heat*oparts/parts);
		
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
	public List<CurrentSwayInfo> handleSway() {
		BeveragePendingContext ctx=new BeveragePendingContext(this);
		swayeffects.clear();
		List<CurrentSwayInfo> swi=
		SwayRecipe.recipes.stream().map(ctx::handleSwayRecipe).flatMap(Optional::stream)
		.map(t->{
				swayeffects.addAll(t.getFirst());
				return t.getSecond();
		})
		.flatMap(Optional::stream)
		.sorted((t2, t1) -> Mth.ceil(t1.display - t2.display))
		.collect(Collectors.toList());
		swayeffects.sort(Comparator.<MobEffectInstance>comparingInt(e -> MobEffect.getId(e.getEffect()))
				.thenComparingInt(e->e.getAmplifier()).thenComparingInt(e->e.getDuration()));
		recalculateHAS();
		return swi;
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
					MobEffectInstance copy=new MobEffectInstance(es);
					copy.duration=(int) (copy.duration*oparts/cparts);
					effects.add(copy);
				}
			}
		}
		for (FloatemStack fs : f.stacks) {
			this.addItem(new FloatemStack(fs.getStack(), fs.getCount() * oparts / cparts));
		}
		heat+=f.heat*oparts/cparts;

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
		nbt.put("sway", SerializeUtil.toNBTList(swayeffects, e -> e.save(new CompoundTag())));
		ListTag relishes=new ListTag();
		for(Fluid s:this.relishes) {
			relishes.add(StringTag.valueOf(Utils.getRegistryName(s).toString()));
		}
		nbt.put("relish", relishes);
		nbt.putInt("heal", healing);
		nbt.putFloat("sat", saturation);
		nbt.putInt("heat", heat);
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
		for (MobEffectInstance ef : effects) {
			b.effect(()->new MobEffectInstance(ef), 1);
		}
		for (MobEffectInstance ef : swayeffects) {
			b.effect(()->new MobEffectInstance(ef), 1);
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
		for (MobEffectInstance eff : swayeffects) {
			if (eff != null) {
				li.add(Pair.of(()->new MobEffectInstance(eff), 1f));
			}
		}
		return null;
	}
	public void completeData() {
		stacks.sort(Comparator.<FloatemStack>comparingDouble(e -> e.getCount()).thenComparingInt(t->Item.getId(t.getItem())));
		foodeffect.sort(
				Comparator.<Pair<MobEffectInstance, Float>>comparingInt(e -> MobEffect.getId(e.getFirst().getEffect()))
						.thenComparing(Pair::getSecond));
		effects.sort(
				Comparator.<MobEffectInstance>comparingInt(e -> MobEffect.getId(e.getEffect()))
				.thenComparingInt(e->e.getAmplifier()).thenComparingInt(e->e.getDuration()));
		
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
				if(fvr.effects!=null)
					foodeffect.addAll(fvr.effects);
				continue;
			}
			FoodProperties f = fs.getStack().getFoodProperties(null);
			if (f != null) {
				nh += fs.getCount() * f.getNutrition();
				ns += fs.getCount() * f.getSaturationModifier()* f.getNutrition();
				foodeffect.addAll(f.getEffects());
			}
		}
		int conv = (int) (0.075 * nh);
		this.healing = (int) Math.ceil(nh - conv);
		ns += conv / 2f;
		if(this.healing>0)
			this.saturation = Math.max(0.6f, ns / this.healing);
		else
			this.saturation =0;
	}
	
	public void addItem(ItemStack is, float parts) {
		for (FloatemStack i : stacks) {
			if (i.equals(is)) {
				i.setCount(i.getCount() + is.getCount() / parts);
				return;
			}
		}
		stacks.add(new FloatemStack(is.copy(), is.getCount() / parts));
	}

	public void addItem(FloatemStack is) {
		for (FloatemStack i : stacks) {
			if (i.equals(is.getStack())) {
				i.setCount(i.getCount() + is.getCount());
				return;
			}
		}
		stacks.add(is);
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((effects == null) ? 0 : effects.hashCode());
		result = prime * result + Arrays.hashCode(relishes);
		result = prime * result + ((stacks == null) ? 0 : stacks.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BeverageInfo other = (BeverageInfo) obj;
		if (effects == null) {
			if (other.effects != null)
				return false;
		} else if (!effects.equals(other.effects))
			return false;
		if (!Arrays.equals(relishes, other.relishes))
			return false;
		if (stacks == null) {
			if (other.stacks != null)
				return false;
		} else if (!stacks.equals(other.stacks))
			return false;
		return true;
	}


}
