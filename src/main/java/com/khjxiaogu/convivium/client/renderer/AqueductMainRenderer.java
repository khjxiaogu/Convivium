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

import com.google.common.collect.ImmutableSet;
import com.khjxiaogu.convivium.CVMain;
import com.khjxiaogu.convivium.blocks.aqueduct.AqueductControllerBlock;
import com.khjxiaogu.convivium.blocks.aqueduct.AqueductControllerBlockEntity;
import com.khjxiaogu.convivium.blocks.kinetics.KineticBasedBlock;
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
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;

public class AqueductMainRenderer implements BlockEntityRenderer<AqueductControllerBlockEntity> {
	public static final DynamicBlockModelReference rotor=ModelUtils.getModel(CVMain.MODID,"aqueduct_wavemaker_rotor");

	/**
	 * @param rendererDispatcherIn  
	 */
	public AqueductMainRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
	}

	private FluidStack water=new FluidStack(Fluids.WATER,1000);
	@SuppressWarnings({ "deprecation", "resource" })
	@Override
	public void render(AqueductControllerBlockEntity blockEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer,
			int combinedLightIn, int combinedOverlayIn) {
		if (!blockEntity.getLevel().hasChunkAt(blockEntity.getBlockPos()))
			return;
		BlockState state = blockEntity.getBlockState();
		Direction facing=state.getValue(AqueductControllerBlock.FACING);

		boolean isBlack=RotationUtils.isBlackGrid(blockEntity.getBlockPos());
		
		matrixStack.pushPose();
		matrixStack.rotateAround(new Quaternionf(new AxisAngle4f((float) (facing.toYRot()*Math.PI/180f),0,-1,0)),0.5f,0.5f,0.5f);
		boolean shouldApart=state.getValue(KineticBasedBlock.ACTIVE)&&state.getValue(KineticBasedBlock.LOCKED);
		if(shouldApart)
			ModelUtils.renderModelGroups(rotor,buffer.getBuffer(RenderType.cutout()),ImmutableSet.of("Wheels"),matrixStack, combinedLightIn, combinedOverlayIn);
		if(state.getValue(KineticBasedBlock.ACTIVE))
			matrixStack.rotateAround(RotationUtils.getRotation(partialTicks,0f,0f,1f,isBlack),0.5f,0.5f,0.5f);
		if(shouldApart)
			ModelUtils.renderModelGroups(rotor,buffer.getBuffer(RenderType.cutout()),ImmutableSet.of("Cogs"),matrixStack, combinedLightIn, combinedOverlayIn);
		else
			ModelUtils.renderModel(rotor,buffer.getBuffer(RenderType.cutout()), matrixStack, combinedLightIn, combinedOverlayIn);
		matrixStack.popPose();
		
		matrixStack.pushPose();
		matrixStack.translate(0, 15/16f, 0);
		matrixStack.mulPose(GuiUtils.rotate90);
		VertexConsumer builder = buffer.getBuffer(RenderType.translucent());
		IClientFluidTypeExtensions attr0 = IClientFluidTypeExtensions.of(Fluids.WATER);
		TextureAtlas atlas = Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS);
		TextureAtlasSprite sprite = atlas.getSprite(attr0.getStillTexture(water));
		int col = attr0.getTintColor(water);
		float alp = 1f;
		GuiUtils.drawTexturedColoredRect(builder, matrixStack, 0, 0, 1, 1,(col >> 16 & 255) / 255.0f, (col >> 8 & 255) / 255.0f, (col & 255) / 255.0f, alp, sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1(), combinedLightIn,combinedOverlayIn);
		matrixStack.popPose();
		
		


	}

}