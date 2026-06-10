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
import com.khjxiaogu.convivium.blocks.aqueduct.AqueductControllerBlock;
import com.khjxiaogu.convivium.blocks.aqueduct.AqueductControllerBlockEntity;
import com.khjxiaogu.convivium.blocks.kinetics.KineticBasedBlock;
import com.khjxiaogu.convivium.util.RotationUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.teammoeg.caupona.client.util.DynamicBlockModelReference;
import com.teammoeg.caupona.client.util.FluidRenderHelper;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;


public class AqueductMainRenderer implements BlockEntityRenderer<AqueductControllerBlockEntity,AqueductMainRenderState> {
	public static final DynamicBlockModelReference rotor_wheels=DynamicBlockModelReference.getModel(CVMain.rl("aqueduct_wavemaker_rotor_wheels"));
	public static final DynamicBlockModelReference rotor_cogs=DynamicBlockModelReference.getModel(CVMain.rl("aqueduct_wavemaker_rotor_cogs"));

	/**
	 * @param rendererDispatcherIn  
	 */
	public AqueductMainRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
	}

	@Override
	public AqueductMainRenderState createRenderState() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void submit(AqueductMainRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
		poseStack.pushPose();
		poseStack.rotateAround(state.hrotation,0.5f,0.5f,0.5f);
		QuadInstance quadInstance=new QuadInstance();
		quadInstance.setLightCoords(state.lightCoords);
		if(state.shouldApart) 
			rotor_wheels.submit(submitNodeCollector, poseStack, RenderTypes.translucentMovingBlock(), quadInstance);
		if(state.active)
			poseStack.rotateAround(state.rotation,0.5f,0.5f,0.5f);
		rotor_cogs.submit(submitNodeCollector, poseStack, RenderTypes.translucentMovingBlock(), quadInstance);
		if(!state.shouldApart) {
			rotor_wheels.submit(submitNodeCollector, poseStack, RenderTypes.translucentMovingBlock(), quadInstance);
		}
		poseStack.popPose();
		
		poseStack.pushPose();
		poseStack.translate(0, 15/16f, 0);
		poseStack.mulPose(FluidRenderHelper.rotate90);
		FluidRenderHelper.submitColoredTexturedRect(submitNodeCollector, poseStack, state.spite,
			0, 0, 1, 1,
			state.color, state.lightCoords, OverlayTexture.NO_OVERLAY);
		poseStack.popPose();
	}
	@Override
	public void extractRenderState(AqueductControllerBlockEntity blockEntity, AqueductMainRenderState state, float partialTicks, Vec3 cameraPosition, @Nullable CrumblingOverlay breakProgress) {
		BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
		boolean isBlack=RotationUtils.isBlackGrid(blockEntity.getBlockPos());
		BlockState bs=blockEntity.getBlockState();
		state.shouldApart=bs.getValue(KineticBasedBlock.ACTIVE)&&bs.getValue(KineticBasedBlock.LOCKED);
		state.rotation=RotationUtils.getRotation(partialTicks,0f,0f,1f,isBlack);
		state.hrotation=new Quaternionf().rotateAxis((float) (bs.getValue(AqueductControllerBlock.FACING).toYRot()*Math.PI/180f),0,-1,0);
		BlockState blockState=blockEntity.getBlockState();
		if(blockState.hasProperty(KineticBasedBlock.ACTIVE))
			state.active=blockState.getValue(KineticBasedBlock.ACTIVE);
		FluidStack fs=new FluidStack(Fluids.WATER,1000);
		FluidModel model=FluidRenderHelper.getFluidModel(fs);
		state.spite=model.stillMaterial().sprite();
		state.color=FluidRenderHelper.getFluidColor(model, fs);
	}

}