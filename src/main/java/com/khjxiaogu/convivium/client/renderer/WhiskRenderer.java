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

import com.khjxiaogu.convivium.CVMain;
import com.khjxiaogu.convivium.blocks.whisk.WhiskBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.teammoeg.caupona.client.util.DynamicBlockModelReference;
import com.teammoeg.caupona.client.util.FluidRenderHelper;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ARGB;

public class WhiskRenderer implements BlockEntityRenderer<WhiskBlockEntity,WhiskRenderState> {
	public static final DynamicBlockModelReference cog=DynamicBlockModelReference.getModel(CVMain.rl("whisk_rotor"));
	ItemModelResolver render;
	/**
	 * @param rendererDispatcherIn  
	 */
	
	public WhiskRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
		render=rendererDispatcherIn.itemModelResolver();
	}
	/*@SuppressWarnings({ "deprecation", "resource" })
	@Override
	public void render(WhiskBlockEntity blockEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer,
			int combinedLightIn, int combinedOverlayIn) {
		if (!blockEntity.getLevel().hasChunkAt(blockEntity.getBlockPos()))
			return;
		BlockState state = blockEntity.getBlockState();
		DynamicBlockModelReference model=cog;
		if(model==null)return;
		matrixStack.pushPose();
		boolean active=state.getValue(KineticBasedBlock.ACTIVE);
		if(active) 
			matrixStack.rotateAround(RotationUtils.getYRotation(partialTicks,blockEntity.getBlockPos()),0.5f,0.5f,0.5f);
		if(!blockEntity.tank.isEmpty())
			for(int i=0;i<4;i++) {
				ItemStack is=blockEntity.inv.getStackInSlot(i);
				if(!is.isEmpty()) {
					matrixStack.pushPose();
					matrixStack.rotateAround(new Quaternionf().rotateAxis((float) (Math.PI*(i+0.35)/2),0,1,0),0.5f,0.5f,0.5f);
					matrixStack.translate(6/16f,(6/16f),6/16f);
					matrixStack.mulPose(new Quaternionf().rotateXYZ(0,-30,0));
					matrixStack.scale(0.5f, 0.5f, 0.5f);
					render.render(is, ItemDisplayContext.GROUND, false,
							matrixStack, buffer,combinedLightIn, OverlayTexture.NO_OVERLAY,render.getModel(is, blockEntity.getLevel(),null,(int) blockEntity.getBlockPos().asLong()));
					matrixStack.popPose();
					
				}
			}
		if(active) 
			ModelUtils.tesellateModel(blockEntity,model,buffer.getBuffer(RenderType.cutout()), matrixStack, combinedOverlayIn);
		matrixStack.popPose();
		FluidStack fs = blockEntity.tank.getFluid();
		if(!fs.isEmpty()) {
			matrixStack.pushPose();
			matrixStack.translate(0, 7/16f, 0);
			matrixStack.mulPose(GuiUtils.rotate90);

			VertexConsumer builder = buffer.getBuffer(RenderType.translucent());
			IClientFluidTypeExtensions attr=IClientFluidTypeExtensions.of(fs.getFluid());
			TextureAtlasSprite sprite = Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS)
					.getSprite(attr.getStillTexture(fs));
			int col = attr.getTintColor(fs);
			Vector3f clr;
			float alp = 1f;
			clr = clr(col);
			if(blockEntity.target==null||blockEntity.processMax==0) {
				GuiUtils.drawTexturedColoredRect(builder, matrixStack, .125f, .125f, .75f, .75f, clr.x(), clr.y(),
						clr.z(), alp, sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1(), combinedLightIn,
						combinedOverlayIn);
			}else {
				IClientFluidTypeExtensions tattr=IClientFluidTypeExtensions.of(blockEntity.target.getFluid());
				alp=blockEntity.process*1f/blockEntity.processMax;
				clr=clr(tattr.getTintColor(blockEntity.target));
				GuiUtils.drawTexturedColoredRect(builder, matrixStack, .125f, .125f, .75f, .75f, clr.x(), clr.y(),
						clr.z(), alp, sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1(), combinedLightIn,
						combinedOverlayIn);
				TextureAtlasSprite sprite2 = Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS)
						.getSprite(tattr.getStillTexture());
				GuiUtils.drawTexturedColoredRect(builder, matrixStack, .125f, .125f, .75f, .75f, clr.x(), clr.y(),
						clr.z(), 1-alp, sprite2.getU0(), sprite2.getU1(), sprite2.getV0(), sprite2.getV1(), combinedLightIn,
						combinedOverlayIn);
			}
			matrixStack.popPose();
			
		}
	}

*/


	@Override
	public WhiskRenderState createRenderState() {
		return new WhiskRenderState();
	}
	private final static Quaternionf rotationAxis=new Quaternionf().rotateAxis((float) (Math.PI*(0.35)/2),0,1,0);
	private final static Quaternionf rotationHori=new Quaternionf().rotateXYZ(0,-30,0);
	@Override
	public void submit(WhiskRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
		poseStack.pushPose();
		if(state.rotation!=null) 
			poseStack.rotateAround(state.rotation,0.5f,0.5f,0.5f);
		if(state.spite1!=null){		
			poseStack.pushPose();
			poseStack.rotateAround(rotationAxis,0.5f,0.5f,0.5f);
			poseStack.translate(6/16f,(6/16f),6/16f);
			poseStack.mulPose(rotationHori);
			poseStack.scale(0.5f, 0.5f, 0.5f);
			state.irs.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
			poseStack.popPose();
		}
		final QuadInstance quadInstance = new QuadInstance();
		quadInstance.setLightCoords(state.lightCoords);
		if(state.rotation!=null) 
			cog.submit(submitNodeCollector, poseStack, RenderTypes.translucentMovingBlock(), quadInstance);
		poseStack.popPose();
		if(state.spite1!=null) {
			poseStack.pushPose();
			poseStack.translate(0, 7/16f, 0);
			poseStack.mulPose(FluidRenderHelper.rotate90);

			if(state.spite2==null) {
				FluidRenderHelper.submitColoredTexturedRect(submitNodeCollector, poseStack, state.spite1,
					.125f, .125f, .75f, .75f, 
					state.color1, state.lightCoords, OverlayTexture.NO_OVERLAY);
			}else {
				float alp=state.progress;
				FluidRenderHelper.submitColoredTexturedRect(submitNodeCollector, poseStack, state.spite1,
					.125f, .125f, .75f, .75f, 
					ARGB.multiplyAlpha(state.color1, alp), state.lightCoords, OverlayTexture.NO_OVERLAY);
				
				FluidRenderHelper.submitColoredTexturedRect(submitNodeCollector, poseStack, state.spite2,
					.125f, .125f, .75f, .75f, 
					ARGB.multiplyAlpha(state.color2, 1-alp), state.lightCoords, OverlayTexture.NO_OVERLAY);
			}
			poseStack.popPose();
			
		}
	}

}