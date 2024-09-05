package com.khjxiaogu.convivium.util;

import java.util.Objects;
import java.util.Optional;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

public class NullableItemCodec<A> implements Codec<Optional<A>> {
	Codec<A> wrapped;


	public NullableItemCodec(Codec<A> wrapped) {
		super();
		this.wrapped = wrapped;
	}

	@Override
	public <T> DataResult<T> encode(Optional<A> input, DynamicOps<T> ops, T prefix) {
		if(input.isEmpty())
			return DataResult.success(ops.empty());
		return wrapped.encode(input.get(), ops, prefix);
	}

	@Override
	public <T> DataResult<Pair<Optional<A>, T>> decode(DynamicOps<T> ops, T input) {
		if(Objects.equals(ops.empty(), input))
			return DataResult.success(Pair.of(Optional.empty(), input));
		return wrapped.decode(ops,input).map(p->p.mapFirst(Optional::of));
	}

}
