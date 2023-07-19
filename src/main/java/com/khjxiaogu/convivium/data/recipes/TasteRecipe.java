package com.khjxiaogu.convivium.data.recipes;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.realmsclient.util.JsonUtils;
import com.teammoeg.caupona.data.IDataRecipe;
import com.teammoeg.caupona.data.SerializeUtil;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.RegistryObject;

public class TasteRecipe extends IDataRecipe {
	public Map<String,Float> variantData=new HashMap<>();
	public int priority;
	public Ingredient item;
	public static RegistryObject<RecipeSerializer<?>> SERIALIZER;
	public static RegistryObject<RecipeType<Recipe<?>>> TYPE;
	public TasteRecipe(ResourceLocation id) {
		super(id);
	}
	public TasteRecipe(ResourceLocation id,JsonObject json) {
		super(id);
		item=Ingredient.fromJson(json.get("item"));
		priority=JsonUtils.getIntOr("priority", json, 0);
		if(json.has("variants")) {
			json.get("variants").getAsJsonObject().entrySet().forEach(e->{
				variantData.put(e.getKey(), e.getValue().getAsFloat());
				
			});
		}
	}
	public TasteRecipe(ResourceLocation id,FriendlyByteBuf pb) {
		super(id);
		item=Ingredient.fromNetwork(pb);
		priority=pb.readVarInt();
		variantData.clear();
		SerializeUtil.readList(pb,p->Pair.of(pb.readUtf(), pb.readFloat())).forEach(p->variantData.put(p.getFirst(), p.getSecond()));
	}
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER.get();
	}

	@Override
	public RecipeType<?> getType() {
		return TYPE.get();
	}

	@Override
	public void serializeRecipeData(JsonObject json) {
		json.add("item", item.toJson());
		json.addProperty("priority",priority);
		JsonObject jo=new JsonObject();
		for(Entry<String, Float> e:variantData.entrySet()) {
			jo.addProperty(e.getKey(),e.getValue());
		}
		json.add("variants",jo);
	}
	public void write(FriendlyByteBuf pb) {
		item.toNetwork(pb);
		pb.writeVarInt(priority);
		SerializeUtil.writeList(pb,variantData.entrySet(),(e,p)->{p.writeUtf(e.getKey());p.writeFloat(e.getValue());});
	}
}
