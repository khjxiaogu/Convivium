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

import com.khjxiaogu.convivium.CVBlocks;
import com.khjxiaogu.convivium.CVMain;
import com.khjxiaogu.convivium.blocks.pestle_and_mortar.PamBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.teammoeg.caupona.client.util.DynamicBlockModelReference;
import com.teammoeg.caupona.client.util.GuiUtils;
import com.teammoeg.caupona.client.util.ModelUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;

public class PamRenderer extends RotationRenderer<PamBlockEntity> {
	public static final DynamicBlockModelReference cog=ModelUtils.getModel(CVMain.MODID,"pestle_and_mortar_rotor");
	private final ItemRenderer render;
	/**
	 * @param rendererDispatcherIn  
	 */
	public PamRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
		render=rendererDispatcherIn.getItemRenderer();
	}

	@Override
	public DynamicBlockModelReference getMainRotor(BlockState state, PamBlockEntity be) {
		if(state.is(CVBlocks.pam.get()))
			return cog;
		return null;
	}

	@Override
	public void customRender(PamBlockEntity blockEntity, float partialTicks, PoseStack matrixStack,
			MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
		// TODO Auto-generated method stub
		FluidStack fs = blockEntity.tankout.getFluid();
		if(fs.isEmpty())
			fs=blockEntity.tankin.getFluid();
		boolean type=true;
		if(!fs.isEmpty()) {
			type=false;
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
			GuiUtils.drawTexturedColoredRect(builder, matrixStack, .125f, .125f, .75f, .75f, clr.x(), clr.y(),
					clr.z(), alp, sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1(), combinedLightIn,
					combinedOverlayIn);
			matrixStack.popPose();
			
		}
		for(int i=0;i<6;i++) {
			ItemStack is=blockEntity.inv.getStackInSlot(i);
			
			if(!is.isEmpty()) {
				matrixStack.pushPose();
				matrixStack.rotateAround(new Quaternionf(new AxisAngle4f((float) (Math.PI*(i-2)/4),0,1,0)),0.5f,0.5f,0.5f);
				matrixStack.translate(5/16f,type?(4/16f):(8/16f),5/16f);
				matrixStack.mulPose(new Quaternionf().rotateXYZ(type?30:-10,0,30));
				matrixStack.scale(0.5f, 0.5f, 0.5f);
				
				render.render(is, ItemDisplayContext.GROUND, false,
						matrixStack, buffer,combinedLightIn, OverlayTexture.NO_OVERLAY,render.getModel(is, blockEntity.getLevel(),null,(int) blockEntity.getBlockPos().asLong()));
				matrixStack.popPose();
				
			}
		}
	}


	private static Vector3f clr(int col) {
		return new Vector3f((col >> 16 & 255) / 255.0f, (col >> 8 & 255) / 255.0f, (col & 255) / 255.0f);
	}

}