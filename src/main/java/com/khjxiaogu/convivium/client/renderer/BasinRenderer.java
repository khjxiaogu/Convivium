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

import com.khjxiaogu.convivium.blocks.basin.BasinBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.teammoeg.caupona.client.util.FluidRenderHelper;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;


public class BasinRenderer implements BlockEntityRenderer<BasinBlockEntity,BasinRenderState> {
	/**
	 * @param rendererDispatcherIn  
	 */
	public BasinRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
	}


	@Override
	public BasinRenderState createRenderState() {
		return new BasinRenderState();
	}



	@Override
	public void submit(BasinRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
		if(state.spite!=null) {
			poseStack.pushPose();
			poseStack.translate(0, state.level/16f, 0);
			poseStack.mulPose(FluidRenderHelper.rotate90);
			FluidRenderHelper.submitColoredTexturedRect(submitNodeCollector, poseStack, state.spite,
				3/16f,3/16f, 10/16f, 10/16f, 
				state.color, state.lightCoords, OverlayTexture.NO_OVERLAY);
			poseStack.popPose();
			
		}
	}



	@Override
	public void extractRenderState(BasinBlockEntity blockEntity, BasinRenderState state, float partialTicks, Vec3 cameraPosition, @Nullable CrumblingOverlay breakProgress) {
		BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
		FluidStack fs=FluidUtil.getStack(blockEntity.tankin, 0);
		state.spite=null;
		if(!fs.isEmpty()) {
			FluidModel model=FluidRenderHelper.getFluidModel(fs);
			state.spite=model.stillMaterial().sprite();
			state.color=FluidRenderHelper.getFluidColor(model, fs);
			state.level=fs.getAmount()/250+1;
			if(blockEntity.recipeHandler.getProcessMax()!=0)
				state.level+=blockEntity.recipeHandler.getProcess()*1f/blockEntity.recipeHandler.getProcessMax();
			else
				state.level+=1;
		}
	}

}