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

package com.khjxiaogu.convivium.data.recipes.compare;

import java.util.function.Function;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.teammoeg.caupona.data.CachedDataDeserializer;
import com.teammoeg.caupona.data.Deserializer;

import net.minecraft.network.FriendlyByteBuf;

public class Compares {
	private static CachedDataDeserializer<Compare,JsonObject> compares=new CachedDataDeserializer<>() {

		@Override
		protected Compare internalOf(JsonObject json) {
			return getDeserializer(json.get("comparator").getAsString()).read(json);
		}
		
	};
	static {
		register("greater",v-> GT.C,v-> GT.C);
		register("equals",v-> EQ.C,v-> EQ.C);
		register("lesser",v-> LT.C,v-> LT.C);
		register("greater_equals",v-> GE.C,v-> GE.C);
		register("not_equals",v-> NE.C,v-> NE.C);
		register("lesser_equals",v-> LE.C,v-> LE.C);
		
	}
	public static void register(String name, Deserializer<JsonObject, Compare> des) {
		compares.register(name, des);
	}

	public static void register(String name, Function<JsonObject, Compare> rjson,
			Function<FriendlyByteBuf, Compare> rpacket) {
		compares.register(name, rjson, rpacket);
	}
	private static final JsonObject dummy=new JsonObject();
	public static Compare of(JsonElement jsonElement) {
		if(jsonElement.isJsonPrimitive()) {
			return compares.getDeserializer(jsonElement.getAsString()).read(dummy);
		}
		return compares.of(jsonElement.getAsJsonObject());
	}

	public static Compare of(FriendlyByteBuf buffer) {
		return compares.of(buffer);
	}

	public static void write(Compare e, FriendlyByteBuf buffer) {
		buffer.writeUtf(e.getType());
		e.write(buffer);
	}

	public static void clearCache() {
		compares.clearCache();
	}
}
