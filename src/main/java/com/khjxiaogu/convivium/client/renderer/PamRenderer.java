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
import com.khjxiaogu.convivium.blocks.pestle_and_mortar.PamBlockEntity;
import com.teammoeg.caupona.client.util.DynamicBlockModelReference;
import com.teammoeg.caupona.client.util.ModelUtils;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

public class PamRenderer extends RotationRenderer<PamBlockEntity> {
	public static final DynamicBlockModelReference cog=ModelUtils.getModel(CVMain.MODID,"pestle_and_mortar_rotor");
	/**
	 * @param rendererDispatcherIn  
	 */
	public PamRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
	}

	@Override
	public DynamicBlockModelReference getMainRotor(BlockState state, PamBlockEntity be) {
		if(state.is(CVBlocks.pam.get()))
			return cog;
		return null;
	}


}