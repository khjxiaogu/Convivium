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

package com.khjxiaogu.convivium.client.renderer;


import org.joml.Quaternionf;

import com.khjxiaogu.convivium.blocks.vending.BeverageVendingBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.teammoeg.caupona.client.util.RenderHelper;

import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.phys.Vec3;

public class VendingRenderer implements BlockEntityRenderer<BeverageVendingBlockEntity,VendingRenderState> {
	static final Quaternionf rotation= new Quaternionf().rotateAxis((float) Math.PI,0,0,1);
	/**
	 * @param rendererDispatcherIn  
	 */
	public VendingRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
	}
	@Override
	public VendingRenderState createRenderState() {
		return new VendingRenderState();
	}
	@Override
	public void submit(VendingRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
		poseStack.pushPose();
		poseStack.rotateAround(state.rotation,0.5f,0.5f,0.5f);
		poseStack.scale(1/38f,1/38f, 1);
		poseStack.mulPose(rotation);
		poseStack.translate(0,0,3/128f);
		
		
		submitNodeCollector.submitText(poseStack, -14, -8, state.num, false, Font.DisplayMode.POLYGON_OFFSET, state.lightCoords,0xffffffff, 0, 0);
		poseStack.popPose();
		
	}
	@Override
	public void extractRenderState(BeverageVendingBlockEntity blockEntity, VendingRenderState state, float partialTicks, Vec3 cameraPosition, CrumblingOverlay breakProgress) {
		BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
		String todraw;
		if(blockEntity.amt<10)
			todraw=" "+blockEntity.amt;
		else
			todraw=""+blockEntity.amt;
		state.num=Component.literal(todraw).getVisualOrderText();
		
		state.rotation=RenderHelper.getRotation(blockEntity.getBlockState().getValue(HorizontalDirectionalBlock.FACING));
	}
	
}