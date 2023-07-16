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

package com.khjxiaogu.convivium.datagen;

import com.khjxiaogu.convivium.CVMain;
import com.teammoeg.caupona.datagen.JsonGenerator;
import com.teammoeg.caupona.datagen.JsonStorage;

import net.minecraft.data.PackOutput;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.common.data.ExistingFileHelper;

public class FluidAnimationGenerator extends JsonGenerator {

	public FluidAnimationGenerator(PackOutput output, ExistingFileHelper helper) {
		super(PackType.CLIENT_RESOURCES, output, helper,CVMain.MODNAME+" Fluid Animation");
	}

	@Override
	protected void gather(JsonStorage reciver) {

	}

}
