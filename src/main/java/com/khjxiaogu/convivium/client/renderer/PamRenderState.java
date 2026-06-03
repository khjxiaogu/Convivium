package com.khjxiaogu.convivium.client.renderer;

import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.neoforged.neoforge.fluids.FluidStack;

public class PamRenderState extends RotationRenderState {
	FluidStack fluid;
	ItemStackRenderState[] stacks=new ItemStackRenderState[6];
}
