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

package com.khjxiaogu.convivium.client.util;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;

import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;

public class CVRenderType extends RenderType {
	public static final RenderType GLINT = create("convivium:glint",
			DefaultVertexFormat.BLOCK,
			VertexFormat.Mode.QUADS, 131072, true, false,
			RenderType.CompositeState.builder()
			.setLightmapState(LIGHTMAP)
			.setShaderState(RenderType.RENDERTYPE_GLINT_SHADER)
			.setTextureState(BLOCK_SHEET)
			.setTextureState(new RenderStateShard.TextureStateShard(ItemRenderer.ENCHANTED_GLINT_ITEM, true, false))
			.setWriteMaskState(RenderType.COLOR_WRITE)
			.setCullState(RenderType.NO_CULL)
			.setDepthTestState(RenderType.EQUAL_DEPTH_TEST)
			.setTransparencyState(RenderType.GLINT_TRANSPARENCY)
			.setTexturingState(RenderType.GLINT_TEXTURING)
			.createCompositeState(true));


	public CVRenderType(String pName, VertexFormat pFormat, Mode pMode, int pBufferSize, boolean pAffectsCrumbling,
			boolean pSortOnUpload, Runnable pSetupState, Runnable pClearState) {
		super(pName, pFormat, pMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, pSetupState, pClearState);
	}

}
