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
import org.jspecify.annotations.Nullable;

import com.khjxiaogu.convivium.CVMain;
import com.khjxiaogu.convivium.blocks.kinetics.AeolipileBlockEntity;
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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class AeolipileRenderer implements BlockEntityRenderer<AeolipileBlockEntity,AeolipileRenderState> {
	public static final DynamicBlockModelReference aeolipile=DynamicBlockModelReference.getModel(CVMain.rl("aeolipile_rotor"));
	public static final DynamicBlockModelReference aeolipile_cw=DynamicBlockModelReference.getModel(CVMain.rl("aeolipile_rotor_clockwise"));
	/**
	 * @param rendererDispatcherIn  
	 */
	public AeolipileRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
	}





	@Override
	public AeolipileRenderState createRenderState() {
		return new AeolipileRenderState();
	}


	@Override
	public void submit(AeolipileRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
		poseStack.pushPose();

		poseStack.rotateAround(state.rotation,0.5f,0.5f,0.5f);

		//System.out.println("render");
		QuadInstance quadInstance=new QuadInstance();
		quadInstance.setLightCoords(state.lightCoords);
		state.rotor.submit(submitNodeCollector, poseStack, RenderTypes.translucentMovingBlock(), quadInstance);
		poseStack.popPose();
	}


	@Override
	public void extractRenderState(AeolipileBlockEntity blockEntity, AeolipileRenderState state, float partialTicks, Vec3 cameraPosition, @Nullable CrumblingOverlay breakProgress) {
		BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
		BlockState bs = blockEntity.getBlockState();
		Direction facing=bs.getValue(KineticBasedBlock.FACING);

		BlockPos facingPos=blockEntity.getFacingPos();
		boolean isBlack=RotationUtils.isBlackGrid(facingPos);
		state.active=bs.getValue(KineticBasedBlock.ACTIVE);
		if(state.active) 
			state.rotation=RotationUtils.getRotation(partialTicks,1f,0f,0f,(float) (facing.toYRot()*Math.PI/180f),0,-1,0,isBlack);
		else
			state.rotation=new Quaternionf().rotateAxis((float) (facing.toYRot()*Math.PI/180f),0,-1,0);
		state.rotor=isBlack?aeolipile_cw:aeolipile;
	}

}