package com.khjxiaogu.convivium.data.recipes;

import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;
import com.khjxiaogu.convivium.data.recipes.nnumbers.INumber;
import com.khjxiaogu.convivium.data.recipes.relishcondition.RelishCondition;
import com.teammoeg.caupona.data.IDataRecipe;

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
		return null;
	}
	@Override
	public RecipeType<?> getType() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void serializeRecipeData(JsonObject json) {
		// TODO Auto-generated method stub
		
	}
}
