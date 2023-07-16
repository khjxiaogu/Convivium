/*
 * Copyright (c) 2022 TeamMoeg
 *
 * This file is part of Caupona.
 *
 * Caupona is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Caupona is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * Specially, we allow this software to be used alongside with closed source software Minecraft(R) and Forge or other modloader.
 * Any mods or plugins can also use apis provided by forge or com.teammoeg.caupona.api without using GPL or open source.
 *
 * You should have received a copy of the GNU General Public License
 * along with Caupona. If not, see <https://www.gnu.org/licenses/>.
 */

package com.khjxiaogu.convivium.client.renderer;

import com.khjxiaogu.convivium.CVBlocks;
import com.khjxiaogu.convivium.CVMain;
import com.khjxiaogu.convivium.blocks.kinetics.AeolipileBlockEntity;
import com.khjxiaogu.convivium.blocks.kinetics.CogCageBlock;
import com.khjxiaogu.convivium.blocks.kinetics.CogeCageBlockEntity;
import com.khjxiaogu.convivium.blocks.kinetics.KineticBasedBlock;
import com.khjxiaogu.convivium.util.RotationUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.teammoeg.caupona.client.util.DynamicBlockModelReference;
import com.teammoeg.caupona.client.util.ModelUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class AeolipileRenderer implements BlockEntityRenderer<AeolipileBlockEntity> {
	public static final DynamicBlockModelReference aeolipile=ModelUtils.getModel(CVMain.MODID,"aeolipile_rotor");
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
		Direction pos=state.getValue(KineticBasedBlock.FACING);
		//if(aeolipile.get()==Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getModelManager().getMissingModel())
		//	throw new RuntimeException("Missing model aeolipile");
		if(state.getValue(KineticBasedBlock.ACTIVE)) {
			matrixStack.pushPose();
			matrixStack.rotateAround(RotationUtils.getRotation(partialTicks,pos.getStepX(),0f,pos.getStepZ(),blockEntity.getBlockPos()),0.5f,0.5f,0.5f);
			//System.out.println("render");
			ModelUtils.renderModel(aeolipile,buffer.getBuffer(RenderType.solid()), matrixStack, combinedLightIn, combinedOverlayIn);
			matrixStack.popPose();
			ModelUtils.renderModel(aeolipile,buffer.getBuffer(RenderType.solid()), matrixStack, combinedLightIn, combinedOverlayIn);
		}


	}

}