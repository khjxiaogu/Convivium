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

import org.jspecify.annotations.Nullable;

import com.khjxiaogu.convivium.blocks.kinetics.KineticBasedBlock;
import com.khjxiaogu.convivium.util.RotationUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.teammoeg.caupona.client.util.DynamicBlockModelReference;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public abstract class RotationRenderer<T extends BlockEntity,S extends RotationRenderState> implements BlockEntityRenderer<T,S> {
	private final QuadInstance quadInstance = new QuadInstance();
	/**
	 * @param rendererDispatcherIn  
	 */
	public RotationRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
	}
	public abstract DynamicBlockModelReference getMainRotor(BlockState bs,T be);

	@SuppressWarnings("unused")
	public void customRender(S state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera, QuadInstance instance) {};
	@SuppressWarnings("unused")
	public void customRenderRotated(S state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera, QuadInstance instance) {}
	@Override
	public void extractRenderState(T blockEntity, S state, float partialTicks, Vec3 cameraPosition, @Nullable CrumblingOverlay breakProgress) {
		BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
		state.rotation=null;
		BlockState blockState=blockEntity.getBlockState();
		if(blockState.hasProperty(KineticBasedBlock.ACTIVE)&&blockState.getValue(KineticBasedBlock.ACTIVE)) 
			state.rotation=RotationUtils.getYRotation(partialTicks,blockEntity.getBlockPos());
		state.rotor=getMainRotor(blockState,blockEntity);
	}
	@Override
	public void submit(S state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
		if(state.rotor==null)return;
		quadInstance.setLightCoords(state.lightCoords);
		quadInstance.setOverlayCoords(OverlayTexture.NO_OVERLAY);
		poseStack.pushPose();
		this.customRender(state, poseStack, submitNodeCollector, camera, quadInstance);
		if(state.rotation!=null)
		poseStack.rotateAround(state.rotation,0.5f,0.5f,0.5f);
		this.customRenderRotated(state, poseStack, submitNodeCollector, camera, quadInstance);
		if(state.rotation!=null)
			state.rotor.submit(submitNodeCollector, poseStack, RenderTypes.translucentMovingBlock(), quadInstance);
		poseStack.popPose();
	};
}