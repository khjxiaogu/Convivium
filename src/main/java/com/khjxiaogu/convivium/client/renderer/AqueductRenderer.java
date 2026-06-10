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

import com.khjxiaogu.convivium.blocks.aqueduct.AqueductBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.teammoeg.caupona.client.util.FluidRenderHelper;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;


public abstract class AqueductRenderer<T extends BlockEntity,S extends AqueductRenderState> implements BlockEntityRenderer<T,S> {
	public static class Aqueduct extends AqueductRenderer<AqueductBlockEntity,AqueductRenderState>{
		public Aqueduct(Context rendererDispatcherIn) {
			super(rendererDispatcherIn);
		}

		@Override
		public AqueductRenderState createRenderState() {
			return new AqueductRenderState();
		}
		
	}
	/**
	 * @param rendererDispatcherIn  
	 */
	public AqueductRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
	}
	@Override
	public void submit(S state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
		// TODO Auto-generated method stub
		poseStack.pushPose();
		poseStack.translate(0, 15/16f, 0);
		poseStack.mulPose(FluidRenderHelper.rotate90);
		FluidRenderHelper.submitColoredTexturedRect(submitNodeCollector, poseStack, state.spite,
			0, 0, 1, 1,
			state.color, state.lightCoords, OverlayTexture.NO_OVERLAY);
		poseStack.popPose();
	}
	@Override
	public void extractRenderState(T blockEntity, S state, float partialTicks, Vec3 cameraPosition, @Nullable CrumblingOverlay breakProgress) {
		BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
		FluidStack fs=new FluidStack(Fluids.WATER,1000);
		FluidModel model=FluidRenderHelper.getFluidModel(fs);
		state.spite=model.stillMaterial().sprite();
		state.color=FluidRenderHelper.getFluidColor(model, fs);
	}
}