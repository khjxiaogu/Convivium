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

package com.khjxiaogu.convivium.blocks.kinetics;

import com.teammoeg.caupona.blocks.CPHorizontalEntityBlock;
import com.teammoeg.caupona.network.CPBaseBlockEntity;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.neoforged.neoforge.registries.DeferredHolder;

public abstract class KineticBasedBlock<V extends CPBaseBlockEntity> extends CPHorizontalEntityBlock<V> {
	public static final BooleanProperty ACTIVE=BooleanProperty.create("active");
	public static final BooleanProperty LOCKED=BooleanProperty.create("locked");
	public KineticBasedBlock(DeferredHolder<BlockEntityType<?>,BlockEntityType<V>> blockEntity, Properties p_54120_) {
		super(blockEntity, p_54120_);
		super.registerDefaultState(this.defaultBlockState().setValue(ACTIVE, false));
	}
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(ACTIVE);
	}

}
