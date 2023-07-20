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

package com.khjxiaogu.convivium.blocks.pestle_and_mortar;

import com.khjxiaogu.convivium.CVGui;
import com.teammoeg.caupona.container.CPBaseContainer;
import com.teammoeg.caupona.container.OutputSlot;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

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
