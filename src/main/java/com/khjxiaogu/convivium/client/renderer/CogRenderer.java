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
import com.khjxiaogu.convivium.blocks.kinetics.CogeCageBlockEntity;
import com.khjxiaogu.convivium.client.util.CachedBakedModel;
import com.khjxiaogu.convivium.client.util.TransformedBakedModel;
import com.khjxiaogu.convivium.util.RotationUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.teammoeg.caupona.client.util.DynamicBlockModelReference;
import com.teammoeg.caupona.client.util.ModelUtils;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class CogRenderer extends RotationRenderer<CogeCageBlockEntity> {
	public static final DynamicBlockModelReference cog=ModelUtils.getModel(CVMain.MODID,"cog");
	public static final DynamicBlockModelReference cage=ModelUtils.getModel(CVMain.MODID,"cage_wheel");
	private final CachedBakedModel[] caches=new CachedBakedModel[] { new CachedBakedModel(facing->{
		PoseStack matrixStack=new PoseStack();
		matrixStack.rotateAround(RotationUtils.getYRotation(0,true),0.5f,0.5f,0.5f);
		return new TransformedBakedModel(cog.get(),matrixStack);
	}),new CachedBakedModel(facing->{
		PoseStack matrixStack=new PoseStack();
		matrixStack.rotateAround(RotationUtils.getYRotation(0,false),0.5f,0.5f,0.5f);
		return new TransformedBakedModel(cog.get(),matrixStack);
	}),new CachedBakedModel(facing->{
		PoseStack matrixStack=new PoseStack();
		matrixStack.rotateAround(RotationUtils.getYRotation(0,true),0.5f,0.5f,0.5f);
		return new TransformedBakedModel(cage.get(),matrixStack);
	}),new CachedBakedModel(facing->{
		PoseStack matrixStack=new PoseStack();
		matrixStack.rotateAround(RotationUtils.getYRotation(0,false),0.5f,0.5f,0.5f);
		return new TransformedBakedModel(cage.get(),matrixStack);
	})};
	/**
	 * @param rendererDispatcherIn  
	 */
	public CogRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
	}

	@Override
	public BakedModel getMainRotor(BlockState state, CogeCageBlockEntity be,boolean black,boolean active) {
		if(active) {
			int idx=black?0:1;
			CachedBakedModel cbm=null;
			if(state.is(CVBlocks.cage.get())) {
				cbm=caches[idx+2];
			}
			if(state.is(CVBlocks.cog.get()))
				cbm=caches[idx];
			if(cbm==null)
				return null;
			
			return cbm.getModel(Direction.NORTH,RotationUtils.getTicks());
		}
		if(state.is(CVBlocks.cage.get())) {
			return cage.get();
		}
		if(state.is(CVBlocks.cog.get()))
			return cog.get();
		return null;
	}


}