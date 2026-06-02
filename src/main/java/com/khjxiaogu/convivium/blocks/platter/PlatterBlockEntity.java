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

package com.khjxiaogu.convivium.blocks.platter;

import com.khjxiaogu.convivium.CVBlockEntityTypes;
import com.khjxiaogu.convivium.CVMain;
import com.teammoeg.caupona.network.CPBaseBlockEntity;
import com.teammoeg.caupona.util.IInfinitable;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

public class PlatterBlockEntity extends CPBaseBlockEntity implements IInfinitable,MenuProvider {
	public ItemStacksResourceHandler storage=new ItemStacksResourceHandler(4) {

		@Override
		protected int getCapacity(int index, ItemResource resource) {
			return 1;
		}

		@Override
		protected void onContentsChanged(int index, ItemStack previousContents) {
			super.onContentsChanged(index, previousContents);
			syncData();
		}

	};
	boolean isInfinite = false;
	public GlobalConfig config=GlobalConfig.PILED;
	public Object renderingContext;
	public Object renderingContextLock=new Object();
	public SlotConfig[] slotconfig=new SlotConfig[] {SlotConfig.MODEL,SlotConfig.MODEL,SlotConfig.MODEL,SlotConfig.MODEL};
	public PlatterBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
		super(CVBlockEntityTypes.PLATTER.get(), pWorldPosition, pBlockState);
	}
	@Override
	public void handleMessage(short type, int data) {
		switch(type) {
		case 0:config=GlobalConfig.values()[data];break;
		case 2:slotconfig[0]=SlotConfig.values()[data];break;
		case 3:slotconfig[1]=SlotConfig.values()[data];break;
		case 4:slotconfig[2]=SlotConfig.values()[data];break;
		case 5:slotconfig[3]=SlotConfig.values()[data];break;
		}
		this.syncData();
	}
	@Override
	public void readCustomNBT(ValueInput nbt, boolean isClient) {
		nbt.readChild("storage", storage);
		config=GlobalConfig.values()[nbt.getIntOr("config",0)];
		int[] its=nbt.getIntArray("slot_config").orElseGet(()->new int[4]);
		for(int i=0;i<4;i++) {
			slotconfig[i]=SlotConfig.values()[its[i]];
		}
		isInfinite = nbt.getBooleanOr("inf",false);
		renderingContext=null;
	}
	@Override
	public void writeCustomNBT(ValueOutput nbt, boolean isClient) {
		nbt.putChild("storage",storage);
		nbt.putInt("config", config.ordinal());
		int[] its=new int[4];
		for(int i=0;i<4;i++) {
			its[i]=slotconfig[i].ordinal();
		}
		nbt.putIntArray("slot_config", its);
		nbt.putBoolean("inf", isInfinite);
	}
	@Override
	public void tick() {
		
	}
	@Override
	public boolean setInfinity() {
		return isInfinite = !isInfinite;
	}
	@Override
	public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
		return new PlatterContainer(pContainerId, pInventory, this);
	}

	@Override
	public Component getDisplayName() {
		return Utils.translate("container." + CVMain.MODID + ".fruit_platter.title");
	}

	@Override
	public boolean isInfinite() {
		return isInfinite;
	}
}
