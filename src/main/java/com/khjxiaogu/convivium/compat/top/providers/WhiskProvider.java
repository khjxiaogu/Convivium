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

import java.util.ArrayList;
import java.util.List;

import com.khjxiaogu.convivium.blocks.whisk.WhiskBlockEntity;
import com.teammoeg.caupona.compat.top.TOPRegister;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.ItemStackHandler;

public class WhiskProvider  implements IProbeInfoProvider {

	@Override
	public void addProbeInfo(ProbeMode mode, IProbeInfo info, Player player, Level level, BlockState state, IProbeHitData hitResult) {
		if(level.getBlockEntity(hitResult.getPos()) instanceof WhiskBlockEntity entity) {
			//info.tankSimple(1250, .getFluid());
			info.tankHandler(entity.tank);
			if(player.isShiftKeyDown()) {
				ItemStackHandler inventory=entity.inv;
				List<ItemStack> inputs=new ArrayList<>();
				for(int i=0;i<inventory.getSlots();i++) {
					if(!inventory.getStackInSlot(i).isEmpty())
					inputs.add(inventory.getStackInSlot(i));
				}
				if(!inputs.isEmpty()) {
					IProbeInfo layout=info.horizontal(info.defaultLayoutStyle().borderColor(0x88ffffff).spacing(0));
					inputs.forEach(layout::item);
				}
			}
			if(entity.rs||entity.isHeating) {
				IProbeInfo layout=info.horizontal(info.defaultLayoutStyle().spacing(0));
				if(entity.rs)
					layout.item(new ItemStack(Items.REDSTONE_TORCH));
				if(entity.isHeating)
					layout.tankSimple(1000, new FluidStack(Fluids.LAVA,1000),info.defaultProgressStyle().bounds(16, 16).showText(false));
			}
			info.progress(entity.processMax-entity.process, entity.processMax);
			if(entity.processMax>0) {
				
				
				if(player.isShiftKeyDown()){
					if(entity.target!=null) {
						info.tankSimple(entity.target.getAmount(),entity.target, info.defaultProgressStyle().showText(false).height(16).width(16));
					}
				}
			}

		}
	}


	@Override
	public ResourceLocation getID() {
		return TOPRegister.idForBlock("whisk");
	}

}
