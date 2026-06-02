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

package com.khjxiaogu.convivium.data.recipes.relishcondition;

import com.khjxiaogu.convivium.util.BeveragePendingContext;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teammoeg.caupona.data.TranslationProvider;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.material.Fluid;

public class HasFluidCondition implements RelishCondition {
	public static final MapCodec<HasFluidCondition> CODEC=RecordCodecBuilder.mapCodec(t->t.group(
		BuiltInRegistries.FLUID.byNameCodec().fieldOf("relish").forGetter(o->o.f))
		.apply(t, HasFluidCondition::new));
	Fluid f;
	public static final StreamCodec<RegistryFriendlyByteBuf,HasFluidCondition> STREAM_CODEC=StreamCodec.composite(
			ByteBufCodecs.registry(Registries.FLUID),o->o.f,
			HasFluidCondition::new);
	

	public HasFluidCondition(Fluid relish) {
		this.f=relish;
	}

	@Override
	public boolean test(BeveragePendingContext t) {
		return t.relishFluids.contains(f);
	}

	@Override
	public String getTranslation(TranslationProvider p) {
		return p.getTranslation("recipe.convivium.relish_cond.contains_fluid",f.getFluidType().getDescription());
	}


}
