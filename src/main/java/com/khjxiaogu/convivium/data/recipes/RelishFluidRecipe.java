package com.khjxiaogu.convivium.data.recipes;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;
import com.khjxiaogu.convivium.util.SUtils;
import com.teammoeg.caupona.data.IDataRecipe;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RelishFluidRecipe extends IDataRecipe {
	public Fluid fluid;
	public String relish;
	public Map<String,Float> variantData=new HashMap<>();
	public static RegistryObject<RecipeSerializer<?>> SERIALIZER;
	public static RegistryObject<RecipeType<Recipe<?>>> TYPE;
	public static Map<Fluid,RelishFluidRecipe> recipes;

	public RelishFluidRecipe(ResourceLocation id, Fluid fluid, String relish) {
		super(id);
		this.fluid = fluid;
		this.relish = relish;
	}
	public RelishFluidRecipe(ResourceLocation id,FriendlyByteBuf pb) {
		super(id);
		fluid=pb.readRegistryIdUnsafe(ForgeRegistries.FLUIDS);
		relish=pb.readUtf();
		variantData=SUtils.fromPacket(pb);
	}
	public RelishFluidRecipe(ResourceLocation id,JsonObject jo) {
		super(id);
		fluid=ForgeRegistries.FLUIDS.getValue(new ResourceLocation(GsonHelper.getAsString(jo, "fluid")));
		relish=GsonHelper.getAsString(jo, "relish");
		variantData=SUtils.fromJson(jo,"variants");
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
		json.addProperty("fluid",Utils.getRegistryName(fluid).toString());
		json.addProperty("relish",relish);
		json.add("variants",SUtils.toJson(variantData));
	}
	public void write(FriendlyByteBuf pb) {
		pb.writeRegistryIdUnsafe(ForgeRegistries.FLUIDS, fluid);
		pb.writeUtf(relish);
		SUtils.toPacket(pb, variantData);
	}
}
