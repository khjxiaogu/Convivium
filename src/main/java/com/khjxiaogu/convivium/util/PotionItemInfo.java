package com.khjxiaogu.convivium.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.item.ItemStack;

public class PotionItemInfo {
	public static final Codec<PotionItemInfo> CODEC=RecordCodecBuilder.create(t->t.group(ItemStack.CODEC.fieldOf("potion").forGetter(PotionItemInfo::getStack)).apply(t,PotionItemInfo::new));
	final ItemStack stack;

	public PotionItemInfo(ItemStack stack) {
		super();
		this.stack = stack;
	}

	public ItemStack getStack() {
		return stack;
	}


}
