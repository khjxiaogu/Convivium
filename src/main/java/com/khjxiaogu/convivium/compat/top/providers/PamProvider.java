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
import java.util.Optional;

import com.khjxiaogu.convivium.blocks.pestle_and_mortar.PamBlockEntity;
import com.khjxiaogu.convivium.compat.top.TOPRegister;
import com.khjxiaogu.convivium.data.recipes.GrindingRecipe;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

public class PamProvider  implements IProbeInfoProvider {

	@Override
	public void addProbeInfo(ProbeMode mode, IProbeInfo info, Player player, Level level, BlockState state, IProbeHitData hitResult) {
		if(level.getBlockEntity(hitResult.getPos()) instanceof PamBlockEntity entity) {
			info.progress(entity.recipeHandler.getFinishedProgress(), entity.recipeHandler.getProcessMax());
			if(player.isShiftKeyDown()) {
				ItemStackHandler inventory=entity.inv;
				List<ItemStack> inputs=new ArrayList<>();
				for(int i=0;i<inventory.getSlots();i++) {
					if(!inventory.getStackInSlot(i).isEmpty())
					inputs.add(inventory.getStackInSlot(i));
				}
				info.tank(entity.tankin);
				info.tank(entity.tankout);
				if(!inputs.isEmpty()) {
					IProbeInfo layout=info.horizontal(info.defaultLayoutStyle().borderColor(0x88ffffff).spacing(0));
					inputs.forEach(layout::item);
				}
				if(player.isShiftKeyDown()&&entity.recipeHandler.getLastRecipe()!=null) {
					level.getRecipeManager().byKey(entity.recipeHandler.getLastRecipe()).flatMap(t->t.value() instanceof GrindingRecipe?Optional.of((GrindingRecipe)t.value()):Optional.empty())
					.ifPresent(r->{
						IProbeInfo layout=info.horizontal(info.defaultLayoutStyle().borderColor(0x88ffffff).spacing(0));
						for(ItemStack stack:r.output)
							layout.item(stack);
						layout.tankSimple(r.out.getAmount(), r.out,info.defaultProgressStyle().showText(false).height(16).width(16));
						
					});
				}
			}
		}
	}
	@Override
	public ResourceLocation getID() {
		return TOPRegister.idForBlock("pam");
	}

}
