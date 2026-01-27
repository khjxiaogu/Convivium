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
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

public abstract class RotationRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {
	/**
	 * @param rendererDispatcherIn  
	 */
	public RotationRenderer() {
	}
	public abstract BakedModel getMainRotor(BlockState bs,T be,boolean isBlack,boolean isActive);

	@SuppressWarnings({ "deprecation", "resource" })
	@Override
	public void render(T blockEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer,
			int combinedLightIn, int combinedOverlayIn) {
		if (!blockEntity.getLevel().hasChunkAt(blockEntity.getBlockPos()))
			return;
		BlockState state = blockEntity.getBlockState();
		
		
		this.customRender(blockEntity, partialTicks, matrixStack, buffer, combinedLightIn, combinedOverlayIn);
		boolean active=state.getValue(KineticBasedBlock.ACTIVE);
		BakedModel model=getMainRotor(state,blockEntity,RotationUtils.isBlackGrid(blockEntity.getBlockPos()),active);
		if(model==null)return;
		ModelUtils.tesellate(blockEntity, state, model, buffer.getBuffer(RenderType.cutout()), matrixStack, combinedOverlayIn, ModelData.EMPTY);
		
	}
	@SuppressWarnings("unused")
	public void customRender(T blockEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer,
			int combinedLightIn, int combinedOverlayIn) {};
}