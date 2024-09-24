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

package com.khjxiaogu.convivium.client;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

public class InputParticleType extends ParticleType<InputParticleOption> {
    private final MapCodec<InputParticleOption> codec = InputParticleOption.codec(getType());
    private final StreamCodec<RegistryFriendlyByteBuf, InputParticleOption> streamCodec = InputParticleOption.streamCodec(getType());

    public InputParticleType(boolean overrideLimiter) {
        super(overrideLimiter);
    }

    public InputParticleType getType() {
        return this;
    }

    @Override
    public MapCodec<InputParticleOption> codec() {
        return this.codec;
    }
	public ParticleOptions with(ItemStack stack) {
		return InputParticleOption.create(this, Either.left(stack));
	}
	public ParticleOptions with(FluidStack stack) {
		return InputParticleOption.create(this, Either.right(stack));
	}
    @Override
    public StreamCodec<RegistryFriendlyByteBuf, InputParticleOption> streamCodec() {
        return this.streamCodec;
    }
}