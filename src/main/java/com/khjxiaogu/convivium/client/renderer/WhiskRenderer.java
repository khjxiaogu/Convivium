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

import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.khjxiaogu.convivium.CVMain;
import com.khjxiaogu.convivium.blocks.kinetics.KineticBasedBlock;
import com.khjxiaogu.convivium.blocks.whisk.WhiskBlockEntity;
import com.khjxiaogu.convivium.util.RotationUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.teammoeg.caupona.client.util.DynamicBlockModelReference;
import com.teammoeg.caupona.client.util.GuiUtils;
import com.teammoeg.caupona.client.util.ModelUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

public class WhiskRenderer implements BlockEntityRenderer<WhiskBlockEntity> {
	public static final DynamicBlockModelReference cog=ModelUtils.getModel(CVMain.MODID,"whisk_rotor");
	ItemRenderer render;
	/**
	 * @param rendererDispatcherIn  
	 */
	
	public WhiskRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
		render=rendererDispatcherIn.getItemRenderer();
	}
	@SuppressWarnings({ "deprecation", "resource" })
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
					matrixStack.rotateAround(new Quaternionf(new AxisAngle4f((float) (Math.PI*(i+0.35)/2),0,1,0)),0.5f,0.5f,0.5f);
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




	private static Vector3f clr(int col) {
		return new Vector3f((col >> 16 & 255) / 255.0f, (col >> 8 & 255) / 255.0f, (col & 255) / 255.0f);
	}

}