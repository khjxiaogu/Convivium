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

import com.khjxiaogu.convivium.CVBlocks;
import com.khjxiaogu.convivium.CVMain;
import com.khjxiaogu.convivium.blocks.pestle_and_mortar.PamBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.teammoeg.caupona.client.util.DynamicBlockModelReference;
import com.teammoeg.caupona.client.util.FluidRenderHelper;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;

public class PamRenderer extends RotationRenderer<PamBlockEntity,PamRenderState> {
	private final ItemModelResolver render;
	/**
	 * @param rendererDispatcherIn  
	 */
	public PamRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
		super(rendererDispatcherIn);
		render=rendererDispatcherIn.itemModelResolver();
	}

	@Override
	public DynamicBlockModelReference getMainRotor(BlockState state, PamBlockEntity be) {
		if(state.is(CVBlocks.pam.get()))
			return DynamicBlockModelReference.getModel(CVMain.rl("block/dynamic/pestle_and_mortar_rotor"));
		return null;
	}
	@Override
	public void extractRenderState(PamBlockEntity blockEntity, PamRenderState state, float partialTicks, Vec3 cameraPosition, @Nullable CrumblingOverlay breakProgress) {
		super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
		FluidResource fs = blockEntity.tanks.getResource(1);
		if(fs.isEmpty())
			fs=blockEntity.tanks.getResource(0);
		state.fluid=fs.toStack(250);
		for(int i=0;i<6;i++) {
			ItemResource is=blockEntity.inv.getResource(i);
			state.stacks[i]=null;
			if(!is.isEmpty()) {
				ItemStackRenderState isrs=new ItemStackRenderState();
				render.appendItemLayers(isrs, is.toStack(),ItemDisplayContext.GROUND, blockEntity.getLevel(), null, i+7);
				state.stacks[i]=isrs;
			}
			
		}
		
		
	}
	@Override
	public PamRenderState createRenderState() {
		return new PamRenderState();
	}

	@Override
	public void customRender(PamRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera, QuadInstance instance) {
		super.customRender(state, poseStack, submitNodeCollector, camera, instance);
		boolean type=true;
		FluidStack fs=state.fluid;
		if(!fs.isEmpty()) {
			type=false;
			poseStack.pushPose();
			poseStack.translate(0, 7/16f, 0);
			poseStack.mulPose(FluidRenderHelper.rotate90);
			FluidModel model=FluidRenderHelper.getFluidModel(fs);
			int col=FluidRenderHelper.getFluidColor(model, fs);
			TextureAtlasSprite spite=model.stillMaterial().sprite();
			FluidRenderHelper.submitColoredTexturedRect(submitNodeCollector, poseStack, spite, .125f, .125f, .75f, .75f, col, state.lightCoords, OverlayTexture.NO_OVERLAY);
			poseStack.popPose();
			
		}
		for(int i=0;i<6;i++) {
			ItemStackRenderState is=state.stacks[i];
			
			if(is!=null) {
				poseStack.pushPose();
				poseStack.rotateAround(new Quaternionf().rotateAxis((float) (Math.PI*(i-2)/4),0,1,0),0.5f,0.5f,0.5f);
				poseStack.translate(5/16f,type?(4/16f):(8/16f),5/16f);
				poseStack.mulPose(new Quaternionf().rotateXYZ(type?30:-10,0,30));
				poseStack.scale(0.5f, 0.5f, 0.5f);
				is.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0xff000000);
				poseStack.popPose();
				
			}
		}
	}



}