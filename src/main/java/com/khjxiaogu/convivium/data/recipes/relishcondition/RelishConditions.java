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

package com.khjxiaogu.convivium.data.recipes.relishcondition;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teammoeg.caupona.data.CachedDataDeserializer;
import com.teammoeg.caupona.data.Deserializer;

import net.minecraft.network.FriendlyByteBuf;

public class RelishConditions {
	private static CachedDataDeserializer<RelishCondition,JsonObject> relishes=new CachedDataDeserializer<>() {

		@Override
		protected RelishCondition internalOf(JsonObject json) {
			return getDeserializer(json.get("type").getAsString()).read(json);
		}
		
	};
	/*
	public static final Codec<MajorRelishCondition> MAJOR_CODEC=RecordCodecBuilder.create(t->
	t.group(Codec.STRING.fieldOf("relish").forGetter(o->o.relish)).apply(t, MajorRelishCondition::new));
	public static final Codec<OnlyMajorRelishCondition> ONLY_MAJOR_CODEC=RecordCodecBuilder.create(t->
	t.group(Codec.STRING.fieldOf("relish").forGetter(o->o.relish)).apply(t, OnlyMajorRelishCondition::new));
	private static final Map<String,Codec<RelishCondition>> codecs=new HashMap<>();
	public static String findType(Codec<RelishCondition> c) {
		for(Entry<String, Codec<RelishCondition>> cd:codecs.entrySet()) {
			if(cd.getValue().equals(c))
				return cd.getKey();
		}
		return null;
	}
	public static final Codec<RelishCondition> CODEC=
			Codec.STRING.flatXmap(
				s->Optional.ofNullable(codecs.get(s)).map(DataResult::success).orElseGet(()->DataResult.error(()->"Unknown type")),
				c->Optional.ofNullable(findType(c)).map(DataResult::success).orElseGet(()->DataResult.error(()->"Unknown type")))
			.dispatch(o->codecs.get(o.getType()),o->o);*/
	static {
		register("major", MajorRelishCondition::new, MajorRelishCondition::new);
		register("only_major", OnlyMajorRelishCondition::new, OnlyMajorRelishCondition::new);
		register("compare", RelishCompareCondition::new, RelishCompareCondition::new);
		register("contains",HasRelishCondition::new, HasRelishCondition::new);
		register("contains_fluid",HasFluidCondition::new, HasFluidCondition::new);
		
		
		register("and", AndRelishCondition::new, AndRelishCondition::new);
		register("or", OrRelishCondition::new, OrRelishCondition::new);
	}
	public static void register(String name, Deserializer<JsonObject, RelishCondition> des) {
		relishes.register(name, des);
	}

	public static void register(String name, Function<JsonObject, RelishCondition> rjson,
			Function<FriendlyByteBuf, RelishCondition> rpacket) {
		relishes.register(name, rjson, rpacket);
	}

	public static RelishCondition of(JsonObject jsonElement) {
		return relishes.of(jsonElement);
	}

	public static RelishCondition of(FriendlyByteBuf buffer) {
		return relishes.of(buffer);
	}

	public static void write(RelishCondition e, FriendlyByteBuf buffer) {
		buffer.writeUtf(e.getType());
		e.write(buffer);
	}

	public static void clearCache() {
		relishes.clearCache();
	}
}
