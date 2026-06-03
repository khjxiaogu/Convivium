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

import com.khjxiaogu.convivium.CVMain;
import com.khjxiaogu.convivium.blocks.wolf_fountain.WolfFountainBlockEntity;
import com.teammoeg.caupona.client.util.DynamicBlockModelReference;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

public class WolfFountainRenderer extends RotationRenderer<WolfFountainBlockEntity,RotationRenderState> {
	/**
	 * @param rendererDispatcherIn  
	 */
	public WolfFountainRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	@Override
	public DynamicBlockModelReference getMainRotor(BlockState state, WolfFountainBlockEntity be) {
		return DynamicBlockModelReference.getModel(CVMain.rl("block/dynamic/wolf_fountain_rotor"));
	}

	@Override
	public RotationRenderState createRenderState() {
		return new RotationRenderState();
	}


}