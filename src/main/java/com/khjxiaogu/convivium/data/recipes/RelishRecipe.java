package com.khjxiaogu.convivium.data.recipes;

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
import net.minecraftforge.common.util.JsonUtils;
import net.minecraftforge.registries.RegistryObject;

public class RelishRecipe extends IDataRecipe {
	public ResourceLocation tag;
	public Map<String,Float> variantData;
	public String color;
	public RelishRecipe(ResourceLocation id, ResourceLocation tag, String color) {
		super(id);
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
		tag=pb.readResourceLocation();
		color=pb.readUtf();
		variantData=SUtils.fromPacket(pb);
	}
	public RelishRecipe(ResourceLocation id,JsonObject jo) {
		super(id);
		tag=new ResourceLocation(GsonHelper.getAsString(jo, "tag"));
		color=GsonHelper.getAsString(jo, "color","WHITE");
		variantData=SUtils.fromJson(jo,"variants");
	}
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER.get();
	}
	public MutableComponent getText() {
		return Utils.translate("gui." + CVMain.MODID +"relish.name").setStyle(Style.EMPTY.withColor(TextColor.parseColor(color)));
	}
	@Override
	public RecipeType<?> getType() {
		return TYPE.get();
	}

	@Override
	public void serializeRecipeData(JsonObject json) {
		json.addProperty("tag", tag.toString());
		json.addProperty("color", color);
		json.add("variants",SUtils.toJson(variantData));
	}
	public void write(FriendlyByteBuf pb) {
		pb.writeResourceLocation(tag);
		pb.writeUtf(color);
		SUtils.toPacket(pb, variantData);
	}
}
