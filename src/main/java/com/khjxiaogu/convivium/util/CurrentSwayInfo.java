package com.khjxiaogu.convivium.util;

import java.util.Optional;

import com.khjxiaogu.convivium.util.evaluator.VariantEnvironment;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class CurrentSwayInfo {
	public static final Codec<CurrentSwayInfo> CODEC=RecordCodecBuilder.create(t->t.group(
			Codec.INT.fieldOf("sweetness").forGetter(i->i.dsweet),
			Codec.INT.fieldOf("astringency").forGetter(i->i.dastringent),
			Codec.INT.fieldOf("pungency").forGetter(i->i.dpungent),
			Codec.INT.fieldOf("thickness").forGetter(i->i.dthick),
			Codec.INT.fieldOf("soothingness").forGetter(i->i.drousing),
			Codec.DOUBLE.fieldOf("display").forGetter(i->i.display),
			ResourceLocation.CODEC.fieldOf("icon").forGetter(i->i.icon)).apply(t,CurrentSwayInfo::new));
	public int dsweet;
	public int dastringent;
	public int dpungent;
	public int dthick;
	public int drousing;
	public double display;
	public ResourceLocation icon;
	public CurrentSwayInfo(ResourceLocation ic,VariantEnvironment env) {
		display=env.get("display");
		if(display>0) {
			icon=ic;
			dsweet=fromVal(env.get("sweetnessDelta"));
			dastringent=fromVal(env.get("astringencyDelta"));
			dpungent=fromVal(env.get("pungencyDelta"));
			dthick=fromVal(env.get("thicknessDelta"));
			drousing=fromVal(env.get("soothingnessDelta"));
		}
	}
	public CurrentSwayInfo(int dsweet, int dastringent, int dpungent, int dthick, int drousing, double display,
			ResourceLocation icon) {
		super();
		this.dsweet = dsweet;
		this.dastringent = dastringent;
		this.dpungent = dpungent;
		this.dthick = dthick;
		this.drousing = drousing;
		this.display = display;
		this.icon = icon;
	}
	public boolean shouldShow() {
		return display>0;
	}
	public Optional<CurrentSwayInfo> toOptional(){
		return shouldShow()?Optional.of(this):Optional.empty();
	}
	public static int fromVal(double d) {
		boolean sign=d<0;
		int v=Mth.floor(Math.abs(d));
		if(sign)v=-v;
		if(v>3)
			v=3;
		return v;
	}
}
