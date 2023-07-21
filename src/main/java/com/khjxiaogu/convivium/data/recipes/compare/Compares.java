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
		register("greater", GT::new, GT::new);
		register("equals", EQ::new, EQ::new);
		register("lesser", LT::new, LT::new);
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
