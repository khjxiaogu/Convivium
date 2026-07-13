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

package com.khjxiaogu.convivium.data.recipes.numbers;

import java.util.function.ToDoubleFunction;

import com.khjxiaogu.convivium.util.evaluator.IEnvironment;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public interface INumber extends ToDoubleFunction<IEnvironment>{
	Codec<INumber> STRING_CODEC=Codec.STRING.flatXmap(Expression::parse, INumber::asExpression);
	Codec<INumber> NUMBER_CODEC=Codec.DOUBLE.flatComapMap(Expression::of,INumber::asConstant);
	Codec<INumber> CODEC=Codec.withAlternative(NUMBER_CODEC,STRING_CODEC);
	
	public static final StreamCodec<ByteBuf,INumber> STREAM_CODEC=ByteBufCodecs.either(
		ByteBufCodecs.STRING_UTF8.map(Expression::new,Expression::expr),
		ByteBufCodecs.DOUBLE.map(Expression::of,Expression.Constant::num))
		.map(Either::unwrap, o->(o instanceof Expression.Constant cons)?Either.right(cons):Either.left((Expression)o));
	public DataResult<Double> asConstant();
	public DataResult<String> asExpression();
}
