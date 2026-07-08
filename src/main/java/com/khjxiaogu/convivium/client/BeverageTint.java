package com.khjxiaogu.convivium.client;

import org.jspecify.annotations.Nullable;

import com.khjxiaogu.convivium.CVComponents;
import com.khjxiaogu.convivium.util.BeverageInfo;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teammoeg.caupona.client.util.FluidRenderHelper;

import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;

public record BeverageTint(int defaultColor) implements ItemTintSource {
    public static final MapCodec<BeverageTint> MAP_CODEC = RecordCodecBuilder.mapCodec(
        i -> i.group(ExtraCodecs.ARGB_COLOR_CODEC.fieldOf("default").forGetter(BeverageTint::defaultColor)).apply(i, BeverageTint::new)
    );
	@Override
	public int calculate(ItemStack itemStack, @Nullable ClientLevel level, @Nullable LivingEntity owner) {
		BeverageInfo cmp=itemStack.get(CVComponents.BEVERAGE_INFO);
		if(cmp==null) {
			ResourceHandler<FluidResource> tint=Capabilities.Fluid.ITEM.getCapability(itemStack, ItemAccess.forStack(itemStack));
			
			if(tint!=null) {
				FluidStack stack=FluidUtil.getStack(tint, 0);
				if(!stack.isEmpty())
					return FluidRenderHelper.getFluidColor(FluidRenderHelper.getFluidModel(stack), stack);
			}
			return defaultColor();
		}
		return cmp.getIColor();
	}

	@Override
	public MapCodec<? extends ItemTintSource> type() {
		return MAP_CODEC;
	}

}
