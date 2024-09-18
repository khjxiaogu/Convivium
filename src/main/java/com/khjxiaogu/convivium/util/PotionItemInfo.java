package com.khjxiaogu.convivium.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.item.ItemStack;

public class PotionItemInfo {
	public static final Codec<PotionItemInfo> CODEC = RecordCodecBuilder.create(t -> t.group(ItemStack.CODEC.fieldOf("potion").forGetter(PotionItemInfo::getStack)).apply(t, PotionItemInfo::new));
	final ItemStack stack;

	public PotionItemInfo(ItemStack stack) {
		super();
		this.stack = stack;
	}

	public ItemStack getStack() {
		return stack;
	}

	@Override
	public int hashCode() {
		return ItemStack.hashItemAndComponents(stack);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PotionItemInfo other = (PotionItemInfo) obj;
		if (stack == null) {
			if (other.stack != null)
				return false;
		} else if (!stack.equals(other.stack))
			return false;
		return true;
	}

}
