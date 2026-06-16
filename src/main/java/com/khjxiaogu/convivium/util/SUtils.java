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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.DoubleBinaryOperator;
import java.util.function.Function;
import java.util.function.IntFunction;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.teammoeg.caupona.util.SerializeUtil;

import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.floats.FloatBinaryOperator;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class SUtils {
	public static final FloatBinaryOperator SUM=(a,b)->a+b;
	public static final DoubleBinaryOperator DBL_SUM=(a,b)->a+b;
	public static final BiFunction<Integer,Integer,Integer> INT_SUM=(a,b)->a+b;
	public static Map<String, Float> fromJson(JsonObject json, String name) {
		Map<String, Float> variantData = new HashMap<>();
		if (json.has(name)) {
			json.get(name).getAsJsonObject().entrySet().forEach(e -> {
				variantData.put(e.getKey(), e.getValue().getAsFloat());

			});
		}
		return variantData;
	}

	public static JsonObject toJson(Map<String, Float> variantData) {
		JsonObject jo = new JsonObject();
		for (Entry<String, Float> e : variantData.entrySet()) {
			jo.addProperty(e.getKey(), e.getValue());
		}
		return jo;
	}

	public static Map<String, Float> fromPacket(FriendlyByteBuf pb) {
		Map<String, Float> variantData = new HashMap<>();
		SerializeUtil.readList(pb, p -> Pair.of(p.readUtf(), p.readFloat())).forEach(p -> variantData.put(p.getFirst(), p.getSecond()));
		return variantData;
	}

	public static void toPacket(FriendlyByteBuf pb, Map<String, Float> variantData) {
		SerializeUtil.writeList(pb, variantData.entrySet(), (e, p) -> {
			p.writeUtf(e.getKey());
			p.writeFloat(e.getValue());
		});
	}
	public static <K,V,T extends Map<K,V>> Function<List<Pair<K,V>>,T> toMap(IntFunction<T> sup){
		return list->{
			T varData=sup.apply(list.size());
			for(Pair<K, V> pair:list) {
				varData.put(pair.getFirst(), pair.getSecond());
			}
			return varData;
		};
	}
	public static <K,V,T extends Map<K,V>> Function<T,List<Pair<K,V>>> toPairList(){
		return map->{
			List<Pair<K,V>> list=new ArrayList<>();
			for(Entry<K, V> e:map.entrySet())
				list.add(Pair.of(e.getKey(),e.getValue()));
			return list;
		};
	}
	public static final Codec<Map<String, Float>> VARIANTS_CODEC=Codec.compoundList(Codec.STRING, Codec.FLOAT)
		.xmap(toMap(HashMap::new), toPairList());
	public static final StreamCodec<ByteBuf, Object2FloatOpenHashMap<String>> VARIANTS_STREAM_CODEC=ByteBufCodecs
		.map(Object2FloatOpenHashMap::new, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.FLOAT);
}

