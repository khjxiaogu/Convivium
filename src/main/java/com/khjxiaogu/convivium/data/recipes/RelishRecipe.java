/*
 * Copyright (c) 2024 IEEM Trivium Society/khjxiaogu
 *
 * This file is part of Convivium.
 *
 * Convivium is free software: you can redistribute it and/or modify
 * it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 * the Free Software Foundation, version 3.
 *
 * Convivium is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 * You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 * along with Convivium. If not, see <https://www.gnu.org/licenses/>.
 */

package com.khjxiaogu.convivium.data.recipes;

import java.util.Map;

import com.khjxiaogu.convivium.CVMain;
import com.khjxiaogu.convivium.util.SUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teammoeg.caupona.data.IDataRecipe;
import com.teammoeg.caupona.util.Utils;

import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;

public class RelishRecipe extends IDataRecipe {
	public Identifier tag;
	public String relishName;
	public String relishFont;
	public Object2FloatOpenHashMap<String> variants;
	public TextColor color;
	public static Map<String, RecipeHolder<RelishRecipe>> recipes;
	public RelishRecipe(String name, Identifier tag, String color) {
		this();
		this.relishName=name;
		this.relishFont=name.substring(0,1);
		this.tag = tag;
		this.color = TextColor.parseColor(color).getOrThrow();
	}
	public static DeferredHolder<RecipeSerializer<?>,RecipeSerializer<RelishRecipe>> SERIALIZER;
	public static DeferredHolder<RecipeType<?>,RecipeType<RelishRecipe>> TYPE;
	public static final MapCodec<RelishRecipe> CODEC=RecordCodecBuilder.mapCodec(t->t.group(
		Identifier.CODEC.fieldOf("tag").forGetter(o->o.tag),
		Codec.STRING.fieldOf("relish").forGetter(o->o.relishName),
		Codec.STRING.optionalFieldOf("font","n").forGetter(o->o.relishFont),
		SUtils.VARIANTS_CODEC.fieldOf("variants").forGetter(o->o.variants),
		TextColor.CODEC.optionalFieldOf("color",TextColor.fromLegacyFormat(ChatFormatting.WHITE)).forGetter(o->o.color)
		).apply(t, RelishRecipe::new));
	public static final StreamCodec<RegistryFriendlyByteBuf,RelishRecipe> STREAM_CODEC=StreamCodec.composite(
		Identifier.STREAM_CODEC,o->o.tag,
		ByteBufCodecs.STRING_UTF8,o->o.relishName,
		ByteBufCodecs.STRING_UTF8,o->o.relishFont,
		SUtils.VARIANTS_STREAM_CODEC,o->o.variants,
		ByteBufCodecs.INT.map(TextColor::fromRgb, t->t.getValue()),o->o.color,
		RelishRecipe::new
		);
	public RelishRecipe() {
		this.variants=new Object2FloatOpenHashMap<String>();
	}

	public RelishRecipe(Identifier tag, String relishName,String relishFont, Map<String, Float> variantData, TextColor color) {
		super();
		this.tag = tag;
		this.relishFont=relishFont;
		this.relishName = relishName;
		this.variants=new Object2FloatOpenHashMap<String>(variantData);
		this.color = color;
	}

	@Override
	public RecipeSerializer<RelishRecipe> getSerializer() {
		return SERIALIZER.get();
	}
	public MutableComponent getText() {
		return getText(relishName,color);
	}
	public static MutableComponent getText(String relishName,TextColor color2) {
		return Utils.translate("gui." + CVMain.MODID +".relish."+relishName+".name").setStyle(Style.EMPTY.withColor(color2));
	}
	@Override
	public RecipeType<RelishRecipe> getType() {
		return TYPE.get();
	}
}
