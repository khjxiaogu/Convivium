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

package com.khjxiaogu.convivium.blocks.platter;

import com.khjxiaogu.convivium.CVGui;
import com.teammoeg.caupona.container.CPBaseContainer;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class PlatterContainer extends CPBaseContainer {
	public final PlatterBlockEntity tile;

	public PlatterBlockEntity getBlock() {
		return tile;
	}

	public PlatterContainer(int id, Inventory inv, FriendlyByteBuf buffer) {
		this(id, inv, (PlatterBlockEntity) inv.player.level().getBlockEntity(buffer.readBlockPos()));
	}

	public PlatterContainer(int id, Inventory inv, PlatterBlockEntity blockEntity) {
		super(CVGui.PLATTER.get(), id,4);
		tile = blockEntity;
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
