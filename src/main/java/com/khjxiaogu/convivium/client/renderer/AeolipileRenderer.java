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

import com.khjxiaogu.convivium.CVBlocks;
import com.khjxiaogu.convivium.CVMain;
import com.khjxiaogu.convivium.blocks.kinetics.AeolipileBlockEntity;
import com.khjxiaogu.convivium.blocks.kinetics.KineticBasedBlock;
import com.khjxiaogu.convivium.util.RotationUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.teammoeg.caupona.client.util.DynamicBlockModelReference;
import com.teammoeg.caupona.client.util.ModelUtils;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class AeolipileRenderer implements BlockEntityRenderer<AeolipileBlockEntity> {
	public static final DynamicBlockModelReference aeolipile=ModelUtils.getModel(CVMain.MODID,"aeolipile_rotor");
	public static final DynamicBlockModelReference aeolipile_cw=ModelUtils.getModel(CVMain.MODID,"aeolipile_rotor_clockwise");
	/**
	 * @param rendererDispatcherIn  
	 */
	public AeolipileRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
	}


	@SuppressWarnings({ "deprecation", "resource" })
	@Override
	public void render(AeolipileBlockEntity blockEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer,
			int combinedLightIn, int combinedOverlayIn) {
		if (!blockEntity.getLevel().hasChunkAt(blockEntity.getBlockPos()))
			return;
		BlockState state = blockEntity.getBlockState();
		if(!state.is(CVBlocks.aeolipile.get()))
			return;
		Direction facing=state.getValue(KineticBasedBlock.FACING);
		//if(aeolipile.get()==Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getModelManager().getMissingModel())
		//	throw new RuntimeException("Missing model aeolipile");
		BlockPos facingPos=blockEntity.getFacingPos();
		boolean isBlack=RotationUtils.isBlackGrid(facingPos);
		matrixStack.pushPose();
		matrixStack.rotateAround(new Quaternionf(new AxisAngle4f((float) (facing.toYRot()*Math.PI/180f),0,-1,0)),0.5f,0.5f,0.5f);
		if(state.getValue(KineticBasedBlock.ACTIVE))
			matrixStack.rotateAround(RotationUtils.getRotation(partialTicks,1f,0f,0f,isBlack),0.5f,0.5f,0.5f);
		//System.out.println("render");
		ModelUtils.renderModel(isBlack?aeolipile_cw:aeolipile,buffer.getBuffer(RenderType.cutout()), matrixStack, combinedLightIn,combinedOverlayIn);
		matrixStack.popPose();
		


	}

}