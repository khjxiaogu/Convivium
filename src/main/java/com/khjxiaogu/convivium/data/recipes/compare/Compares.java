package com.khjxiaogu.convivium.data.recipes.compare;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import com.google.gson.JsonObject;
import com.teammoeg.caupona.data.CachedDataDeserializer;
import com.teammoeg.caupona.data.Deserializer;
import com.teammoeg.caupona.data.InvalidRecipeException;

import net.minecraft.network.FriendlyByteBuf;

public class Compares {
	private static CachedDataDeserializer<Compare,JsonObject> compares=new CachedDataDeserializer<>() {

		@Override
		protected Compare internalOf(JsonObject json) {
			return getDeserializer(json.get("comparator").getAsString()).read(json);
		}
		
	};
	static {
		register("half", Halfs::new, Halfs::new);
		register("mainly", Mainly::new, Mainly::new);
		register("contains", Must::new, Must::new);
		register("mainlyOf", MainlyOfType::new, MainlyOfType::new);
		register("only", Only::new, Only::new);
	}
	public static void register(String name, Deserializer<JsonObject, Compare> des) {
		compares.register(name, des);
	}

	public static void register(String name, Function<JsonObject, Compare> rjson,
			Function<FriendlyByteBuf, Compare> rpacket) {
		compares.register(name, rjson, rpacket);
	}

	public static Compare of(JsonObject jsonElement) {
		return compares.of(jsonElement);
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
