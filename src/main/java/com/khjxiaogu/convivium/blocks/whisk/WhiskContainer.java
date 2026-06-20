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
import com.khjxiaogu.convivium.blocks.whisk.WhiskBlockEntity.HeatingStatus;
import com.teammoeg.caupona.container.CPBaseContainer;
import com.teammoeg.caupona.container.OutputSlot;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;

public class WhiskContainer extends CPBaseContainer<WhiskBlockEntity> {

	public WhiskContainer(int id, Inventory inv, FriendlyByteBuf buffer) {
		this(id, inv, (WhiskBlockEntity) inv.player.level().getBlockEntity(buffer.readBlockPos()));
	}

	public WhiskContainer(int id, Inventory inv, WhiskBlockEntity blockEntity) {
		super(CVGui.WHISK.get(),blockEntity , id,2);
		this.addSlot(new ResourceHandlerSlot(blockEntity.inv,blockEntity.inv::set, 0, 134, 14));
		this.addSlot(new OutputSlot(blockEntity.inv,blockEntity.inv::set, 1, 153, 34));
		this.addDataSlots(blockEntity.convertion);
		this.addDataSlot(new DataSlot() {

			@Override
			public int get() {
				return blockEntity.processMax;
			}

			@Override
			public void set(int value) {
				blockEntity.processMax=value;
			}
			
		});
		this.addDataSlot(new DataSlot() {

			@Override
			public int get() {
				return blockEntity.process;
			}

			@Override
			public void set(int value) {
				blockEntity.process=value;
			}
			
		});
		this.addDataSlot(new DataSlot() {

			@Override
			public int get() {
				return blockEntity.heating.ordinal();
			}

			@Override
			public void set(int value) {
				blockEntity.heating=HeatingStatus.values()[value];
			}
			
		});
		super.addPlayerInventory(inv, 8, 140, 140+58);
	}
	@Override
	public boolean quickMoveIn(ItemStack slotStack) {
		return this.moveItemStackTo(slotStack, 0, INV_START, false);
	}
}
