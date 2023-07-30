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

import com.khjxiaogu.convivium.blocks.kinetics.KineticBasedBlock;
import com.khjxiaogu.convivium.util.RotationUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.teammoeg.caupona.client.util.DynamicBlockModelReference;
import com.teammoeg.caupona.client.util.ModelUtils;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public abstract class RotationRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {
	/**
	 * @param rendererDispatcherIn  
	 */
	public RotationRenderer() {
	}
	public abstract DynamicBlockModelReference getMainRotor(BlockState bs,T be);

	@SuppressWarnings({ "deprecation", "resource" })
	@Override
	public void render(T blockEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer,
			int combinedLightIn, int combinedOverlayIn) {
		if (!blockEntity.getLevel().hasChunkAt(blockEntity.getBlockPos()))
			return;
		BlockState state = blockEntity.getBlockState();
		DynamicBlockModelReference model=getMainRotor(state,blockEntity);
		if(model==null)return;
		matrixStack.pushPose();
		this.customRender(blockEntity, partialTicks, matrixStack, buffer, combinedLightIn, combinedOverlayIn);
		if(state.getValue(KineticBasedBlock.ACTIVE)) 
			matrixStack.rotateAround(RotationUtils.getYRotation(partialTicks,blockEntity.getBlockPos()),0.5f,0.5f,0.5f);
		this.customRenderRotated(blockEntity, partialTicks, matrixStack, buffer, combinedLightIn, combinedOverlayIn);
		ModelUtils.renderModel(model,buffer.getBuffer(RenderType.cutout()), matrixStack, combinedLightIn, combinedOverlayIn);
		matrixStack.popPose();
	}
	@SuppressWarnings("unused")
	public void customRender(T blockEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer,
			int combinedLightIn, int combinedOverlayIn) {};
	@SuppressWarnings("unused")
	public void customRenderRotated(T blockEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer,
			int combinedLightIn, int combinedOverlayIn) {};
}