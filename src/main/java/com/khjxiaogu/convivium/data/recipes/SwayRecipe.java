package com.khjxiaogu.convivium.data.recipes;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonObject;
import com.khjxiaogu.convivium.data.recipes.compare.CompareCondition;
import com.khjxiaogu.convivium.data.recipes.numbers.INumber;
import com.khjxiaogu.convivium.data.recipes.relishcondition.RelishCondition;
import com.khjxiaogu.convivium.util.SUtils;
import com.mojang.datafixers.util.Pair;
import com.teammoeg.caupona.data.IDataRecipe;
import com.teammoeg.caupona.data.SerializeUtil;
import com.teammoeg.caupona.data.Writeable;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.RegistryObject;

public class SwayRecipe  extends IDataRecipe{
	public SwayRecipe(ResourceLocation id) {
		super(id);
		// TODO Auto-generated constructor stub
	}
	List<RelishCondition> relish;
	int priority;
	Map<String,INumber> locals;
	List<SwayEffect> effects;
	ResourceLocation icon;
	public static RegistryObject<RecipeSerializer<?>> SERIALIZER;
	public static RegistryObject<RecipeType<Recipe<?>>> TYPE;
	@Override
	public RecipeSerializer<?> getSerializer() {
		// TODO Auto-generated method stub
		return SERIALIZER.get();
	}
	@Override
	public RecipeType<?> getType() {
		// TODO Auto-generated method stub
		return TYPE.get();
	}
	@Override
	public void serializeRecipeData(JsonObject json) {
		// TODO Auto-generated method stub
		json.add("relish", SerializeUtil.toJsonList(relish, RelishCondition::serialize));
		json.addProperty("priority", priority);
		JsonObject jo=new JsonObject();
		for(Entry<String, INumber> p:locals.entrySet()) {
			jo.add(p.getKey(), p.getValue().serialize());
		}
		json.add("locals",jo);
		json.add("effects", SerializeUtil.toJsonList(effects, Writeable::serialize));
		json.addProperty("icon", icon.toString());
	}
	
	public void write(FriendlyByteBuf pb) {

	}
}
