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

package com.khjxiaogu.convivium.client;

import com.khjxiaogu.convivium.CVMain;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CVParticles {
	public static final DeferredRegister<ParticleType<?>> REGISTER = DeferredRegister
			.create(ForgeRegistries.PARTICLE_TYPES, CVMain.MODID);

	public static final RegistryObject<SimpleParticleType> STEAM = REGISTER.register("steam",
			() -> new SimpleParticleType(false));
	public static final RegistryObject<SimpleParticleType> SOOT = REGISTER.register("soot_smoke",
			() -> new SimpleParticleType(false));
}
