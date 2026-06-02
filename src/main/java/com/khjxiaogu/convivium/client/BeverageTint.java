package com.khjxiaogu.convivium.client;

import org.jspecify.annotations.Nullable;

import com.khjxiaogu.convivium.CVComponents;
import com.khjxiaogu.convivium.util.BeverageInfo;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public record BeverageTint(int defaultColor) implements ItemTintSource {
    public static final MapCodec<BeverageTint> MAP_CODEC = RecordCodecBuilder.mapCodec(
        i -> i.group(ExtraCodecs.ARGB_COLOR_CODEC.fieldOf("default").forGetter(BeverageTint::defaultColor)).apply(i, BeverageTint::new)
    );
	@Override
	public int calculate(ItemStack itemStack, @Nullable ClientLevel level, @Nullable LivingEntity owner) {
		BeverageInfo cmp=itemStack.get(CVComponents.BEVERAGE_INFO);
		if(cmp==null)
			return defaultColor();
		return cmp.getIColor();
	}

	@Override
	public MapCodec<? extends ItemTintSource> type() {
		return MAP_CODEC;
	}

}
