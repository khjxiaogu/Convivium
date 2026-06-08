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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.khjxiaogu.convivium.CVMain;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teammoeg.caupona.data.IDataRecipe;
import com.teammoeg.caupona.util.SerializeUtil;
import com.teammoeg.caupona.util.Utils;

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
	public Map<String,Float> variantData=new HashMap<>();
	public TextColor color;
	public static Map<String, RecipeHolder<RelishRecipe>> recipes;
	public RelishRecipe(String name, Identifier tag, String color) {
		this.relishName=name;
		this.tag = tag;
		this.color = TextColor.parseColor(color).getOrThrow();
	}
	public static DeferredHolder<RecipeSerializer<?>,RecipeSerializer<RelishRecipe>> SERIALIZER;
	public static DeferredHolder<RecipeType<?>,RecipeType<RelishRecipe>> TYPE;
	public static final MapCodec<RelishRecipe> CODEC=RecordCodecBuilder.mapCodec(t->t.group(
		Identifier.CODEC.fieldOf("tag").forGetter(o->o.tag),
		Codec.STRING.fieldOf("relish").forGetter(o->o.relishName),
		Codec.compoundList(Codec.STRING, Codec.FLOAT).optionalFieldOf("variants").forGetter(o->Optional.of(o.variantData.entrySet().stream().map(e->Pair.of(e.getKey(),e.getValue())).collect(Collectors.toList()))),
		TextColor.CODEC.optionalFieldOf("color",TextColor.fromLegacyFormat(ChatFormatting.WHITE)).forGetter(o->o.color)
		).apply(t, RelishRecipe::new));
	public static final StreamCodec<RegistryFriendlyByteBuf,RelishRecipe> STREAM_CODEC=StreamCodec.composite(
		Identifier.STREAM_CODEC,o->o.tag,
		ByteBufCodecs.STRING_UTF8,o->o.relishName,
		SerializeUtil.pair(ByteBufCodecs.STRING_UTF8, ByteBufCodecs.FLOAT).apply(ByteBufCodecs.list()), o->o.variantData.entrySet().stream().map(e->Pair.of(e.getKey(),e.getValue())).collect(Collectors.toList()),
		ByteBufCodecs.INT.map(TextColor::fromRgb, t->t.getValue()),o->o.color,
		RelishRecipe::new
		);
	public RelishRecipe() {
	}

	public RelishRecipe(Identifier tag, String relishName, List<Pair<String, Float>> variantData, TextColor color) {
		super();
		this.tag = tag;
		this.relishName = relishName;
		variantData.stream().forEach(p->this.variantData.put(p.getFirst(),p.getSecond()));
		this.color = color;
	}

	public RelishRecipe(Identifier tag, String relishName, Optional<List<Pair<String, Float>>> variantData,TextColor color) {
		super();
		this.tag = tag;
		this.relishName = relishName;
		variantData.ifPresent(o->o.stream().forEach(p->this.variantData.put(p.getFirst(),p.getSecond())));
		this.color = color;
	}
/*
	public RelishRecipe(Identifier id,FriendlyByteBuf pb) {
		super(id);
		relishName=pb.readUtf();
		tag=pb.readIdentifier();
		color=pb.readUtf();
		variantData=SUtils.fromPacket(pb);
	}*/
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
/*
	public void write(FriendlyByteBuf pb) {
		pb.writeUtf(relishName);
		pb.writeIdentifier(tag);
		pb.writeUtf(color);
		SUtils.toPacket(pb, variantData);
	}*/
}
