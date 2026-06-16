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

package com.khjxiaogu.convivium.blocks.whisk;

import com.khjxiaogu.convivium.CVGui;
import com.teammoeg.caupona.container.CPBaseContainer;
import com.teammoeg.caupona.container.OutputSlot;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;

public class WhiskContainer extends CPBaseContainer<WhiskBlockEntity> {

	public WhiskContainer(int id, Inventory inv, FriendlyByteBuf buffer) {
		this(id, inv, (WhiskBlockEntity) inv.player.level().getBlockEntity(buffer.readBlockPos()));
	}

	public WhiskContainer(int id, Inventory inv, WhiskBlockEntity blockEntity) {
		super(CVGui.WHISK.get(),blockEntity , id,6);
		this.addSlot(new ResourceHandlerSlot(blockEntity.inv,blockEntity.inv::set, 0, 107, 24));
		this.addSlot(new ResourceHandlerSlot(blockEntity.inv,blockEntity.inv::set, 1, 107, 56));
		this.addSlot(new OutputSlot(blockEntity.inv,blockEntity.inv::set, 2, 92, 76));
		this.addSlot(new OutputSlot(blockEntity.inv,blockEntity.inv::set, 3, 94, 96));
		super.addPlayerInventory(inv, 8, 140, 140+58);
	}
	@Override
	public boolean quickMoveIn(ItemStack slotStack) {
		return this.moveItemStackTo(slotStack, 0, INV_START, false);
	}
}
