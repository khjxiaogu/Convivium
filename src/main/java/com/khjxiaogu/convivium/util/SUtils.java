package com.khjxiaogu.convivium.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.teammoeg.caupona.data.SerializeUtil;

import net.minecraft.network.FriendlyByteBuf;

public class SUtils {
	public static Map<String,Float> fromJson(JsonObject json,String name){
		Map<String,Float> variantData=new HashMap<>();
		if(json.has(name)) {
			json.get(name).getAsJsonObject().entrySet().forEach(e->{
				variantData.put(e.getKey(), e.getValue().getAsFloat());
				
			});
		}
		return variantData;
	}
	public static JsonObject toJson(Map<String,Float> variantData){
		JsonObject jo=new JsonObject();
		for(Entry<String, Float> e:variantData.entrySet()) {
			jo.addProperty(e.getKey(),e.getValue());
		}
		return jo;
	}
	public static Map<String,Float> fromPacket(FriendlyByteBuf pb){
		Map<String,Float> variantData=new HashMap<>();
		SerializeUtil.readList(pb,p->Pair.of(pb.readUtf(), pb.readFloat())).forEach(p->variantData.put(p.getFirst(), p.getSecond()));
		return variantData;
	}
	public static void toPacket(FriendlyByteBuf pb,Map<String,Float> variantData){
		SerializeUtil.writeList(pb,variantData.entrySet(),(e,p)->{p.writeUtf(e.getKey());p.writeFloat(e.getValue());});
	}
}
