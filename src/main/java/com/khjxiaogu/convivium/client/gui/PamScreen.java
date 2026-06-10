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

package com.khjxiaogu.convivium.client.gui;

import java.util.ArrayList;
import com.khjxiaogu.convivium.CVMain;
import com.khjxiaogu.convivium.blocks.pestle_and_mortar.PamBlockEntity;
import com.khjxiaogu.convivium.blocks.pestle_and_mortar.PamContainer;
import com.teammoeg.caupona.client.util.FluidRenderHelper;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class PamScreen extends AbstractContainerScreen<PamContainer> {
	private Identifier TEXTURE = Identifier.fromNamespaceAndPath(CVMain.MODID, "textures/gui/pestle_and_mortar.png");

	PamBlockEntity blockEntity;

	public PamScreen(PamContainer container, Inventory inv, Component titleIn) {
		super(container, inv, titleIn);
		this.titleLabelY = 4;
		this.titleLabelX = 5;
		this.inventoryLabelY = this.imageHeight - 99;
		this.inventoryLabelX = 6;
		blockEntity = container.getBlock();
	}


	private ArrayList<Component> tooltip = new ArrayList<>(2);

	public boolean isMouseIn(int mouseX, int mouseY, int x, int y, int w, int h) {
		return mouseX >= leftPos + x && mouseY >= topPos + y && mouseX < leftPos + x + w && mouseY < topPos + y + h;
	}

	public PamBlockEntity getBlockEntity() {
		return blockEntity;
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		tooltip.clear();
		FluidRenderHelper.handleGuiTank(graphics, getBlockEntity().tanks,0, leftPos + 42, topPos + 19, 16, 37,mouseX,mouseY,tooltip::add);
		FluidRenderHelper.handleGuiTank(graphics, getBlockEntity().tanks,0, leftPos + 133, topPos + 34, 16, 37,mouseX,mouseY,tooltip::add);
		super.extractRenderState(graphics, mouseX, mouseY, a);
		if (!tooltip.isEmpty()) {
			graphics.setComponentTooltipForNextFrame(this.font, tooltip, mouseX, mouseY);
		}
	}

	@Override
	protected void extractLabels(GuiGraphicsExtractor graphics, int xm, int ym) {
		graphics.text(this.font, this.title, this.titleLabelX, this.titleLabelY,4210752, false);

		Component name = this.playerInventoryTitle;
		graphics.text(this.font, name, this.inventoryLabelX, this.inventoryLabelY,4210752, false);
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		super.extractBackground(graphics, mouseX, mouseY, a);
		graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
		if(getBlockEntity().getSpeed()>0) {
			graphics.blit(TEXTURE, leftPos+86, topPos+1, 176, 0, 24, 24, 256, 256);
		}
		if (getBlockEntity().recipeHandler.getProcessMax() > 0) {
			graphics.blit(TEXTURE, leftPos + 108, topPos + 23, 176, 24, (int) (22*(getBlockEntity().recipeHandler.getFinishedProgress())*1f/getBlockEntity().recipeHandler.getProcessMax()), 15, 256, 256);
		}
	}

}
