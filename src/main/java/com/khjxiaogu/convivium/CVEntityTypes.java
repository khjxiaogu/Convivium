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

package com.khjxiaogu.convivium;

import com.khjxiaogu.convivium.blocks.wolf_fountain.WolfFountainProjectile;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CVEntityTypes {
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE,
			CVMain.MODID);
	public static final DeferredHolder<EntityType<?>, EntityType<WolfFountainProjectile>> WOLF_FOUNTAIN_DROP = ENTITY_TYPES.register("wolf_fountain_drop",
		t->EntityType.Builder.of(WolfFountainProjectile::new, MobCategory.MISC)
		.sized(0.25F, 0.25F).clientTrackingRange(1).updateInterval(1).noSummon().canSpawnFarFromPlayer().build(t.toString()));

	private CVEntityTypes() {
	}

}