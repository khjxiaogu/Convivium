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

import java.util.Optional;

import com.khjxiaogu.convivium.util.evaluator.VariantEnvironment;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class CurrentSwayInfo {
	public static final Codec<CurrentSwayInfo> CODEC = RecordCodecBuilder.create(t -> t.group(
		Codec.INT.fieldOf("sweetness").forGetter(i -> i.dsweet),
		Codec.INT.fieldOf("astringency").forGetter(i -> i.dastringent),
		Codec.INT.fieldOf("pungency").forGetter(i -> i.dpungent),
		Codec.INT.fieldOf("thickness").forGetter(i -> i.dthick),
		Codec.INT.fieldOf("soothingness").forGetter(i -> i.drousing),
		Codec.FLOAT.fieldOf("display").forGetter(i -> i.getDisplay()),
		Codec.INT.fieldOf("active").forGetter(i -> i.getActive()),
		Identifier.CODEC.fieldOf("icon").forGetter(i -> i.icon)).apply(t, CurrentSwayInfo::new));
	private int dsweet;
	private int dastringent;
	private int dpungent;
	private int dthick;
	private int drousing;
	private float display;
	private int active;
	public final Identifier icon;
	public Identifier image;

	public CurrentSwayInfo(Identifier ic, VariantEnvironment env) {
		display = (float)env.get(Constants.DISPLAY);
		icon = ic;
		if (getDisplay() > 0) {
			dsweet = fromVal(env.get(Constants.SWEETNESS_DELTA));
			dastringent = fromVal(env.get(Constants.ASTRINGENCY_DELTA));
			dpungent = fromVal(env.get(Constants.PUNGENCY_DELTA));
			dthick = fromVal(env.get(Constants.THICKNESS_DELTA));
			drousing = fromVal(env.get(Constants.SOOTHINGNESS_DELTA));
		}

	}

	public CurrentSwayInfo(int dsweet, int dastringent, int dpungent, int dthick, int drousing, float display, int active,
		Identifier icon) {
		super();
		this.dsweet = dsweet;
		this.dastringent = dastringent;
		this.dpungent = dpungent;
		this.dthick = dthick;
		this.drousing = drousing;
		this.display = display;
		this.setActive(active);
		this.icon = icon;
		this.image = Identifier.fromNamespaceAndPath(icon.getNamespace(), "textures/" + icon.getPath() + ".png");
	}

	public float getTasteDelta(String sw) {
		switch (sw) {
		case Constants.ASTRINGENCY:
			return this.dastringent/10f;
		case Constants.PUNGENCY:
			return this.dpungent/10f;
		case Constants.SOOTHINGNESS:
			return this.drousing/10f;
		case Constants.SWEETNESS:
			return this.dsweet/10f;
		case Constants.THICKNESS:
			return this.dthick/10f;
		}
		return 0;
	}

	public boolean shouldShow() {
		return getDisplay() > 0 || getActive() > 0;
	}

	public Optional<CurrentSwayInfo> toOptional() {
		return shouldShow() ? Optional.of(this) : Optional.empty();
	}

	public static int fromVal(double d) {
		boolean sign = d < 0;
		int v = Mth.floor(Math.abs(d)*10);
		if (sign) v = -v;

		return v;
	}

	public int getActive() {
		return active;
	}

	public float getDisplay() {
		return display;
	}

	public void setActive(int active) {
		this.active = active;
	}
}
