package com.khjxiaogu.convivium.data.recipes;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;
import com.khjxiaogu.convivium.CVMain;
import com.khjxiaogu.convivium.util.SUtils;
import com.teammoeg.caupona.data.IDataRecipe;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.RegistryObject;

public class RelishRecipe extends IDataRecipe {
	public ResourceLocation tag;
	public String relishName;
	public Map<String,Float> variantData=new HashMap<>();
	public String color;
	public static Map<String,RelishRecipe> recipes;
	public RelishRecipe(ResourceLocation id,String name, ResourceLocation tag, String color) {
		super(id);
		this.relishName=name;
		this.tag = tag;
		this.color = color;
	}
	public static RegistryObject<RecipeSerializer<?>> SERIALIZER;
	public static RegistryObject<RecipeType<Recipe<?>>> TYPE;
	public RelishRecipe(ResourceLocation id) {
		super(id);
	}

	public RelishRecipe(ResourceLocation id,FriendlyByteBuf pb) {
		super(id);
		relishName=pb.readUtf();
		tag=pb.readResourceLocation();
		color=pb.readUtf();
		variantData=SUtils.fromPacket(pb);
	}
	public RelishRecipe(ResourceLocation id,JsonObject jo) {
		super(id);
		relishName=GsonHelper.getAsString(jo, "name",id.toString());
		tag=new ResourceLocation(GsonHelper.getAsString(jo, "tag"));
		color=GsonHelper.getAsString(jo, "color","WHITE");
		variantData=SUtils.fromJson(jo,"variants");
	}
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER.get();
	}
	public MutableComponent getText() {
		return getText(relishName,color);
	}
	public static MutableComponent getText(String relishName,String color2) {
		return Utils.translate("gui." + CVMain.MODID +".relish."+relishName+".name").setStyle(Style.EMPTY.withColor(TextColor.parseColor(color2)));
	}
	@Override
	public RecipeType<?> getType() {
		return TYPE.get();
	}

	@Override
	public void serializeRecipeData(JsonObject json) {
		json.addProperty("name", relishName);
		json.addProperty("tag", tag.toString());
		json.addProperty("color", color);
		json.add("variants",SUtils.toJson(variantData));
	}
	public void write(FriendlyByteBuf pb) {
		pb.writeUtf(relishName);
		pb.writeResourceLocation(tag);
		pb.writeUtf(color);
		SUtils.toPacket(pb, variantData);
	}
}
