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

package com.khjxiaogu.convivium.compat.top;

import com.khjxiaogu.convivium.CVMain;
import mcjty.theoneprobe.api.ITheOneProbe;
import net.minecraft.resources.ResourceLocation;
import com.khjxiaogu.convivium.compat.top.providers.*;

public class TOPRegister {

	private TOPRegister() {
	}
	public static Object register(ITheOneProbe top) {
		top.registerProvider(new AqueductProvider());
		top.registerProvider(new BasinProvider());
		top.registerProvider(new KineticConnectedProvider());
		top.registerProvider(new PamProvider());
		top.registerProvider(new PlatterProvider());
		top.registerProvider(new VendingProvider());
		top.registerProvider(new WhiskProvider());
		top.registerProvider(new WolfFountainProvider());
		return null;
	}
	public static ResourceLocation idForBlock(String name) {
		return CVMain.rl(name+"_block_info");
	}

}
