/*
 * Copyright (c) 2023 IEEM Trivium Society/khjxiaogu
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

package com.khjxiaogu.convivium.client.renderer;

import org.joml.AxisAngle4f;
import org.joml.Quaternionf;

import com.khjxiaogu.convivium.CVBlocks;
import com.khjxiaogu.convivium.blocks.vending.BeverageVendingBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.teammoeg.caupona.blocks.CPHorizontalBlock;

import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class VendingRenderer implements BlockEntityRenderer<BeverageVendingBlockEntity> {
	static final Quaternionf rotation= new Quaternionf(new AxisAngle4f((float) Math.PI,0,0,1));
	@SuppressWarnings({ "deprecation", "resource" })
	@Override
	public void render(BeverageVendingBlockEntity blockEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer,
			int combinedLightIn, int combinedOverlayIn) {
		if (!blockEntity.getLevel().hasChunkAt(blockEntity.getBlockPos()))
			return;
		BlockState state = blockEntity.getBlockState();
		if(!state.is(CVBlocks.BEVERAGE_VENDING_MACHINE.get()))return;
		Direction dir=state.getValue(CPHorizontalBlock.FACING);
		matrixStack.pushPose();
		matrixStack.rotateAround(new Quaternionf(new AxisAngle4f(-(float)(dir.toYRot()/180*Math.PI),0,1,0)),0.5f,0.5f,0.5f);
		matrixStack.scale(1/38f,1/38f, 1);
		matrixStack.mulPose(rotation);
		matrixStack.translate(0,0,3/128f);
		String todraw;
		if(blockEntity.amt<10)
			todraw=" "+blockEntity.amt;
		else
			todraw=""+blockEntity.amt;
		font.drawInBatch(todraw,-14,-8,0xffffff,false, matrixStack.last().pose(), buffer, Font.DisplayMode.NORMAL,0, 15728880);
		matrixStack.popPose();
	}

	private final Font font;
	/**
	 * @param rendererDispatcherIn  
	 */
	public VendingRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
		font=rendererDispatcherIn.getFont();
	}
	
}