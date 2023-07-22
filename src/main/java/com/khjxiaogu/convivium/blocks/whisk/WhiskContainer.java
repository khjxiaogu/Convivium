/*
 * Copyright (c) 2022 TeamMoeg
 *
 * This file is part of Caupona.
 *
 * Caupona is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Caupona is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * Specially, we allow this software to be used alongside with closed source software Minecraft(R) and Forge or other modloader.
 * Any mods or plugins can also use apis provided by forge or com.teammoeg.caupona.api without using GPL or open source.
 *
 * You should have received a copy of the GNU General Public License
 * along with Caupona. If not, see <https://www.gnu.org/licenses/>.
 */

package com.khjxiaogu.convivium.blocks.whisk;

import com.khjxiaogu.convivium.CVGui;
import com.teammoeg.caupona.container.CPBaseContainer;
import com.teammoeg.caupona.container.OutputSlot;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class WhiskContainer extends CPBaseContainer<WhiskBlockEntity> {

	public WhiskContainer(int id, Inventory inv, FriendlyByteBuf buffer) {
		this(id, inv, (WhiskBlockEntity) inv.player.level().getBlockEntity(buffer.readBlockPos()));
	}

	public WhiskContainer(int id, Inventory inv, WhiskBlockEntity blockEntity) {
		super(CVGui.PLATTER.get(),blockEntity , id,6);
		this.addSlot(new SlotItemHandler(blockEntity.inv, 0, 107, 24));
		this.addSlot(new SlotItemHandler(blockEntity.inv, 1, 91, 32));
		this.addSlot(new SlotItemHandler(blockEntity.inv, 2, 91, 48));
		this.addSlot(new SlotItemHandler(blockEntity.inv, 3, 107, 56));
		this.addSlot(new SlotItemHandler(blockEntity.inv, 4, 92, 76));
		this.addSlot(new OutputSlot(blockEntity.inv, 5, 94, 96));
		super.addPlayerInventory(inv, 8, 140, 140+58);
	}
	@Override
	public boolean quickMoveIn(ItemStack slotStack) {
		return this.moveItemStackTo(slotStack, 0, INV_START, false);
	}
}
