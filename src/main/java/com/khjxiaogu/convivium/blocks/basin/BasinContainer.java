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

package com.khjxiaogu.convivium.blocks.basin;

import com.khjxiaogu.convivium.CVGui;
import com.teammoeg.caupona.container.CPBaseContainer;
import com.teammoeg.caupona.container.OutputSlot;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class BasinContainer extends CPBaseContainer<BasinBlockEntity> {

	public BasinContainer(int id, Inventory inv, FriendlyByteBuf buffer) {
		this(id, inv, (BasinBlockEntity) inv.player.level().getBlockEntity(buffer.readBlockPos()));
	}

	public BasinContainer(int id, Inventory inv, BasinBlockEntity blockEntity) {
		super(CVGui.BASIN.get(),blockEntity , id,5);
		this.addSlot(new SlotItemHandler(blockEntity.inv, 0,98, 13));
		this.addSlot(new OutputSlot(blockEntity.inv, 1, 98, 39));
		this.addSlot(new OutputSlot(blockEntity.inv, 2, 98, 57));
		this.addSlot(new OutputSlot(blockEntity.inv, 3, 116, 39));
		this.addSlot(new OutputSlot(blockEntity.inv, 4, 116, 57));
		super.addPlayerInventory(inv, 8, 84, 142);
	}
	@Override
	public boolean quickMoveIn(ItemStack slotStack) {
		return this.moveItemStackTo(slotStack, 0, INV_START, false);
	}
}
