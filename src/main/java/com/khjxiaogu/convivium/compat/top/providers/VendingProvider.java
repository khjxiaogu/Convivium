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

package com.khjxiaogu.convivium.compat.top.providers;

import com.khjxiaogu.convivium.CVMain;
import com.khjxiaogu.convivium.CVTags;
import com.khjxiaogu.convivium.blocks.vending.BeverageVendingBlock;
import com.khjxiaogu.convivium.blocks.vending.BeverageVendingBlockEntity;
import com.khjxiaogu.convivium.compat.top.TOPRegister;
import com.teammoeg.caupona.CPMain;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.items.ItemStackHandler;

public class VendingProvider  implements IProbeInfoProvider {
	private Lazy<Item> asses=Lazy.of(()->BuiltInRegistries.ITEM.get(CPMain.rl("asses")));
	private ResourceLocation arrow=CVMain.rl("textures/gui/top/arrow.png");
	private ResourceLocation checkmark=CVMain.rl("textures/gui/top/checkmark.png");
	@Override
	public void addProbeInfo(ProbeMode mode, IProbeInfo info, Player player, Level level, BlockState state, IProbeHitData hitResult) {
		if(level.getBlockEntity(hitResult.getPos()) instanceof BeverageVendingBlockEntity entity) {
			if(player.isShiftKeyDown()&&player.getUUID().equals(entity.owner)) {
				info.tank(entity.tank);
				ItemStackHandler inventory=entity.storage;
				int cnt=0;
				for(int i=0;i<inventory.getSlots();i++) {
					if(inventory.getStackInSlot(i).is(CVTags.Items.ASSES))
						cnt+=inventory.getStackInSlot(i).getCount();
				}
				info.item(new ItemStack(asses.get(),cnt));
			}
			IProbeInfo trade=info.horizontal(info.defaultLayoutStyle().borderColor(0x88ffffff).spacing(5));
			trade.item(new ItemStack(asses.get(),entity.amt));
			trade.icon(arrow, 0, 0, 10, 9,info.defaultIconStyle().bounds(16, 16).textureBounds(10, 9));
			trade.tankSimple(entity.tank.getFluid().getAmount(), entity.tank.getFluid(),info.defaultProgressStyle().bounds(16, 16).showText(false));
			if(state.getValue(BeverageVendingBlock.ACTIVE)) {
				trade.icon(checkmark, 0, 0, 9, 8,info.defaultIconStyle().bounds(16, 16).textureBounds(9, 8));
			}
		}
	}
	@Override
	public ResourceLocation getID() {
		return TOPRegister.idForBlock("vending");
	}

}
