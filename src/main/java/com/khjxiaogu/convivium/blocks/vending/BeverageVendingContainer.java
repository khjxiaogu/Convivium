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

package com.khjxiaogu.convivium.blocks.vending;

import com.khjxiaogu.convivium.CVGui;
import com.teammoeg.caupona.container.CPBaseContainer;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;

public class BeverageVendingContainer extends CPBaseContainer<BeverageVendingBlockEntity> {

	public BeverageVendingContainer(int id, Inventory inv, FriendlyByteBuf buffer) {
		this(id, inv, (BeverageVendingBlockEntity) inv.player.level().getBlockEntity(buffer.readBlockPos()));
	}

	public BeverageVendingContainer(int id, Inventory inv, BeverageVendingBlockEntity blockEntity) {
		super(CVGui.VENDING.get(),blockEntity , id,6);
		for(int i=0;i<6;i++)
			this.addSlot(new SlotItemHandler(blockEntity.storage, i, 64+17*(i%3), 38+17*(i/3)));
		super.addPlayerInventory(inv, 8, 82, 140);
	}
	@Override
	public boolean quickMoveIn(ItemStack slotStack) {
		return this.moveItemStackTo(slotStack, 0, INV_START, false);
	}
}
