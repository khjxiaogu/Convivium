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

package com.khjxiaogu.convivium.blocks.platter;

import com.khjxiaogu.convivium.CVGui;
import com.teammoeg.caupona.container.CPBaseContainer;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;

public class PlatterContainer extends CPBaseContainer<PlatterBlockEntity> {

	public PlatterContainer(int id, Inventory inv, FriendlyByteBuf buffer) {
		this(id, inv, (PlatterBlockEntity) inv.player.level().getBlockEntity(buffer.readBlockPos()));
	}

	public PlatterContainer(int id, Inventory inv, PlatterBlockEntity blockEntity) {
		super(CVGui.PLATTER.get(),blockEntity , id,4);
		this.addSlot(new SlotItemHandler(blockEntity.storage, 0, 80, 22));
		this.addSlot(new SlotItemHandler(blockEntity.storage, 1, 98, 30));
		this.addSlot(new SlotItemHandler(blockEntity.storage, 2, 80, 38));
		this.addSlot(new SlotItemHandler(blockEntity.storage, 3, 62, 30));
		super.addPlayerInventory(inv, 8, 82, 140);
	}
	@Override
	public boolean quickMoveIn(ItemStack slotStack) {
		return this.moveItemStackTo(slotStack, 0, INV_START, false);
	}
}
