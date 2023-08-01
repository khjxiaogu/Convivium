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

import org.joml.Vector3f;

import com.khjxiaogu.convivium.blocks.basin.BasinBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.teammoeg.caupona.client.util.GuiUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;

public class BasinRenderer implements BlockEntityRenderer<BasinBlockEntity> {
	/**
	 * @param rendererDispatcherIn  
	 */
	public BasinRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
	}



	private static Vector3f clr(int col) {
		return new Vector3f((col >> 16 & 255) / 255.0f, (col >> 8 & 255) / 255.0f, (col & 255) / 255.0f);
	}

	@Override
	public void render(BasinBlockEntity blockEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer,
			int combinedLightIn, int combinedOverlayIn) {
		// TODO Auto-generated method stub
		FluidStack fs=blockEntity.tankin.getFluid();
		float tr=1;
		if(fs.isEmpty()) {
			fs=blockEntity.fs;
			tr=0;
		}
		if(!fs.isEmpty()) {
			matrixStack.pushPose();
			
			tr=fs.getAmount()/250+1;
			if(blockEntity.processMax!=0)
				tr+=blockEntity.process*1f/blockEntity.processMax;
			else
				tr+=1;
			matrixStack.translate(0, tr/16f, 0);
			matrixStack.mulPose(GuiUtils.rotate90);

			VertexConsumer builder = buffer.getBuffer(RenderType.translucent());
			IClientFluidTypeExtensions attr=IClientFluidTypeExtensions.of(fs.getFluid());
			TextureAtlasSprite sprite = Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS)
					.getSprite(attr.getStillTexture(fs));
			int col = attr.getTintColor(fs);
			Vector3f clr;
			float alp = 1f;
			clr = clr(col);
			GuiUtils.drawTexturedColoredRect(builder, matrixStack, 3/16f,3/16f, 10/16f, 10/16f, clr.x(), clr.y(),
					clr.z(), alp, sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1(), combinedLightIn,
					combinedOverlayIn);
			matrixStack.popPose();
			
		}
	}

}