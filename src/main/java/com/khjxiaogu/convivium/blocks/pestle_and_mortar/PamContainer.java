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

package com.khjxiaogu.convivium.blocks.pestle_and_mortar;

import com.khjxiaogu.convivium.CVGui;
import com.teammoeg.caupona.container.CPBaseContainer;
import com.teammoeg.caupona.container.OutputSlot;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;

public class PamContainer extends CPBaseContainer<PamBlockEntity> {

	public PamContainer(int id, Inventory inv, FriendlyByteBuf buffer) {
		this(id, inv, (PamBlockEntity) inv.player.level().getBlockEntity(buffer.readBlockPos()));
	}

	public PamContainer(int id, Inventory inv, PamBlockEntity blockEntity) {
		super(CVGui.PAM.get(),blockEntity , id,4);
		this.addSlot(new SlotItemHandler(blockEntity.inv, 0, 13, 26));
		this.addSlot(new SlotItemHandler(blockEntity.inv, 1, 5, 42));
		this.addSlot(new SlotItemHandler(blockEntity.inv, 2, 21, 42));
		this.addSlot(new OutputSlot(blockEntity.inv, 3, 155, 21));
		this.addSlot(new OutputSlot(blockEntity.inv, 4, 155, 39));
		this.addSlot(new OutputSlot(blockEntity.inv, 5, 155, 57));
		super.addPlayerInventory(inv, 8, 82, 140);
	}
	@Override
	public boolean quickMoveIn(ItemStack slotStack) {
		return this.moveItemStackTo(slotStack, 0, INV_START, false);
	}
}
