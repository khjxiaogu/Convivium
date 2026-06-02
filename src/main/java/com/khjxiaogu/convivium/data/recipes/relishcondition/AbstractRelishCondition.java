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

import java.util.function.BiFunction;
import java.util.function.Function;

import com.mojang.datafixers.Products.P1;
import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public abstract class AbstractRelishCondition implements RelishCondition{
	protected String relish;
	public static <P extends AbstractRelishCondition> P1<Mu<P>,String>  codecStart(Instance<P> i) {
		return i.group(Codec.STRING.fieldOf("relish").forGetter(o->o.relish));
	}
	public static <T extends AbstractRelishCondition> StreamCodec<RegistryFriendlyByteBuf,T> createStreamCodec(Function<String,T> factory) {
		return StreamCodec.composite(ByteBufCodecs.STRING_UTF8,o->o.relish, factory);
	}
	public static <T extends AbstractRelishCondition,A> StreamCodec<RegistryFriendlyByteBuf,T> createStreamCodec(StreamCodec<? super RegistryFriendlyByteBuf,A> codec1,Function<T,A> func1,BiFunction<String,A,T> factory) {
		return StreamCodec.composite(ByteBufCodecs.STRING_UTF8,o->o.relish,codec1,func1, factory);
	}
	public static <T extends AbstractRelishCondition,A,B> StreamCodec<RegistryFriendlyByteBuf,T> createStreamCodec(StreamCodec<? super RegistryFriendlyByteBuf,A> codec1,Function<T,A> func1,StreamCodec<? super RegistryFriendlyByteBuf,B> codec2,Function<T,B> func2,Function3<String,A,B,T> factory) {
		return StreamCodec.composite(ByteBufCodecs.STRING_UTF8,o->o.relish,codec1,func1,codec2,func2, factory);
	}
	public AbstractRelishCondition(String relish) {
		super();
		this.relish = relish;
	}
}
