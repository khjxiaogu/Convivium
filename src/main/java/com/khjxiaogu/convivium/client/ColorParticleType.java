package com.khjxiaogu.convivium.client;

import com.mojang.serialization.MapCodec;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.codec.StreamCodec;

public class ColorParticleType extends ParticleType<ColorParticleOption> {
    private final MapCodec<ColorParticleOption> codec = ColorParticleOption.codec(getType());
    private final StreamCodec<? super ByteBuf, ColorParticleOption> streamCodec = ColorParticleOption.streamCodec(getType());

    public ColorParticleType(boolean overrideLimiter) {
        super(overrideLimiter);
    }

    public ColorParticleType getType() {
        return this;
    }

    @Override
    public MapCodec<ColorParticleOption> codec() {
        return this.codec;
    }
	public ParticleOptions withColor(int color) {
		return ColorParticleOption.create(this, color);
	}
    @Override
    public StreamCodec<? super ByteBuf, ColorParticleOption> streamCodec() {
        return this.streamCodec;
    }
}