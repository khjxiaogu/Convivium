package com.khjxiaogu.convivium.data.recipes;

import java.util.List;
import java.util.Map;
import com.google.gson.JsonObject;
import com.khjxiaogu.convivium.util.SUtils;
import com.mojang.realmsclient.util.JsonUtils;
import com.teammoeg.caupona.data.IDataRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.RegistryObject;

public class TasteRecipe extends IDataRecipe {
	public Map<String,Float> variantData;
	public int priority;
	public Ingredient item;
	public static RegistryObject<RecipeSerializer<?>> SERIALIZER;
	public static RegistryObject<RecipeType<Recipe<?>>> TYPE;
	public static List<TasteRecipe> recipes;
	public TasteRecipe(ResourceLocation id) {
		super(id);
	}
	public TasteRecipe(ResourceLocation id,JsonObject json) {
		super(id);
		item=Ingredient.fromJson(json.get("item"));
		priority=GsonHelper.getAsInt(json,"priority", 0);
		variantData=SUtils.fromJson(json, "variants");
	}
	public TasteRecipe(ResourceLocation id,FriendlyByteBuf pb) {
		super(id);
		item=Ingredient.fromNetwork(pb);
		priority=pb.readVarInt();
		variantData=SUtils.fromPacket(pb);
		
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
		json.add("variants",SUtils.toJson(variantData));
	}
	public void write(FriendlyByteBuf pb) {
		item.toNetwork(pb);
		pb.writeVarInt(priority);
		SUtils.toPacket(pb, variantData);
	}
}
