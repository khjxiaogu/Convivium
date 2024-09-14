package com.khjxiaogu.convivium.client;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

public class InputParticleOption implements ParticleOptions {
    private final ParticleType<InputParticleOption> type;
    private final Either<ItemStack, FluidStack> stack;

    public static MapCodec<InputParticleOption> codec(ParticleType<InputParticleOption> particleType) {
        return Codec.mapEither(ItemStack.CODEC.fieldOf("item"),FluidStack.CODEC.fieldOf("fluid")).xmap(v -> new InputParticleOption(particleType, v), p_333908_ -> p_333908_.getStack()).fieldOf("stack");
    }

    public static StreamCodec<RegistryFriendlyByteBuf, InputParticleOption> streamCodec(ParticleType<InputParticleOption> type) {
        return ByteBufCodecs.either(ItemStack.STREAM_CODEC, FluidStack.STREAM_CODEC).map(o->new InputParticleOption(type,o),o->o.getStack());
    }

    private InputParticleOption(ParticleType<InputParticleOption> type, Either<ItemStack, FluidStack> stack) {
        this.type = type;
        this.stack = stack;
    }

    @Override
    public ParticleType<InputParticleOption> getType() {
        return this.type;
    }


    public static InputParticleOption create(ParticleType<InputParticleOption> type, Either<ItemStack, FluidStack> stack) {
        return new InputParticleOption(type, stack);
    }

	public Either<ItemStack, FluidStack> getStack() {
		return stack;
	}

}
