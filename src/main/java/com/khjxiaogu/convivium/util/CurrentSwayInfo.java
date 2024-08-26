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
			Codec.INT.fieldOf("active").forGetter(i->i.active),
			ResourceLocation.CODEC.fieldOf("icon").forGetter(i->i.icon)).apply(t,CurrentSwayInfo::new));
	public int dsweet;
	public int dastringent;
	public int dpungent;
	public int dthick;
	public int drousing;
	public double display;
	public int active;
	public ResourceLocation icon;
	public ResourceLocation image;
	public CurrentSwayInfo(ResourceLocation ic,VariantEnvironment env) {
		display=env.get(Constants.DISPLAY);
		icon=ic;
		if(display>0) {	
			dsweet=fromVal(env.get(Constants.SWEETNESS_DELTA));
			dastringent=fromVal(env.get(Constants.ASTRINGENCY_DELTA));
			dpungent=fromVal(env.get(Constants.PUNGENCY_DELTA));
			dthick=fromVal(env.get(Constants.THICKNESS_DELTA));
			drousing=fromVal(env.get(Constants.SOOTHINGNESS_DELTA));
		}
		
	}
	public CurrentSwayInfo(int dsweet, int dastringent, int dpungent, int dthick, int drousing, double display,int active,
			ResourceLocation icon) {
		super();
		this.dsweet = dsweet;
		this.dastringent = dastringent;
		this.dpungent = dpungent;
		this.dthick = dthick;
		this.drousing = drousing;
		this.display = display;
		this.active=active;
		this.icon = icon;
		this.image=ResourceLocation.fromNamespaceAndPath(icon.getNamespace(),"textures/"+icon.getPath()+".png");
	}
	public int getTasteDelta(String sw) {
		switch(sw) {
		case Constants.ASTRINGENCY:return this.dastringent;
		case Constants.PUNGENCY:return this.dpungent;
		case Constants.SOOTHINGNESS:return this.drousing;
		case Constants.SWEETNESS:return this.dsweet;
		case Constants.THICKNESS:return this.dthick;
		}
		return 0;
	}
	public boolean shouldShow() {
		return display>0||active>0;
	}
	public Optional<CurrentSwayInfo> toOptional(){
		return shouldShow()?Optional.of(this):Optional.empty();
	}
	public static int fromVal(double d) {
		boolean sign=d<0;
		int v=Mth.floor(Math.abs(d));
		if(v>3)
			v=3;
		if(sign)v=-v;
		
		return v;
	}
}
