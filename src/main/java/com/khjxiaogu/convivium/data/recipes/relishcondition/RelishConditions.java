package com.khjxiaogu.convivium.data.recipes.relishcondition;

import java.util.function.Function;

import com.google.gson.JsonObject;
import com.teammoeg.caupona.data.CachedDataDeserializer;
import com.teammoeg.caupona.data.Deserializer;
import net.minecraft.network.FriendlyByteBuf;

public class RelishConditions {
	private static CachedDataDeserializer<RelishCondition,JsonObject> compares=new CachedDataDeserializer<>() {

		@Override
		protected RelishCondition internalOf(JsonObject json) {
			return getDeserializer(json.get("comparator").getAsString()).read(json);
		}
		
	};
	static {
		register("major", MajorRelishCondition::new, MajorRelishCondition::new);
		register("only_major", OnlyMajorRelishCondition::new, OnlyMajorRelishCondition::new);
		register("compare", RelishCompareCondition::new, RelishCompareCondition::new);
	}
	public static void register(String name, Deserializer<JsonObject, RelishCondition> des) {
		compares.register(name, des);
	}

	public static void register(String name, Function<JsonObject, RelishCondition> rjson,
			Function<FriendlyByteBuf, RelishCondition> rpacket) {
		compares.register(name, rjson, rpacket);
	}

	public static RelishCondition of(JsonObject jsonElement) {
		return compares.of(jsonElement);
	}

	public static RelishCondition of(FriendlyByteBuf buffer) {
		return compares.of(buffer);
	}

	public static void write(RelishCondition e, FriendlyByteBuf buffer) {
		buffer.writeUtf(e.getType());
		e.write(buffer);
	}

	public static void clearCache() {
		compares.clearCache();
	}
}
