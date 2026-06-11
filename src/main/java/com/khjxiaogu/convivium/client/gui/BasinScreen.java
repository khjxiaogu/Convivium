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
import com.khjxiaogu.convivium.blocks.basin.BasinBlockEntity;
import com.khjxiaogu.convivium.blocks.basin.BasinContainer;
import com.teammoeg.caupona.client.util.FluidRenderHelper;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class BasinScreen extends AbstractContainerScreen<BasinContainer> {
	private Identifier TEXTURE = Identifier.fromNamespaceAndPath(CVMain.MODID, "textures/gui/basin.png");

	BasinBlockEntity blockEntity;

	public BasinScreen(BasinContainer container, Inventory inv, Component titleIn) {
		super(container, inv, titleIn);
		this.titleLabelY = 4;
		this.titleLabelX = 5;
		this.inventoryLabelY = this.imageHeight - 99;
		this.inventoryLabelX = 6;
		blockEntity = container.getBlock();
	}


	private ArrayList<Component> tooltip = new ArrayList<>(2);

	@Override
	public void init() {
		super.init();
	}




	public boolean isMouseIn(int mouseX, int mouseY, int x, int y, int w, int h) {
		return mouseX >= leftPos + x && mouseY >= topPos + y && mouseX < leftPos + x + w && mouseY < topPos + y + h;
	}

	public BasinBlockEntity getBlockEntity() {
		return blockEntity;
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		tooltip.clear();
		super.extractRenderState(graphics, mouseX, mouseY, a);
		FluidRenderHelper.handleGuiTank(graphics, getBlockEntity().tankin, leftPos + 62, topPos + 24, 16, 37, mouseX, mouseY, tooltip::add);
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
		graphics.blit(RenderPipelines.GUI_TEXTURED,TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
		if(getBlockEntity().isLastHeating) {
			graphics.blit(RenderPipelines.GUI_TEXTURED,TEXTURE, leftPos+37, topPos+28, 176, 0, 16, 29,256,256);
		}
		if (getBlockEntity().recipeHandler.getProcessMax() > 0) {
			graphics.blit(RenderPipelines.GUI_TEXTURED,TEXTURE, leftPos + 82, topPos + 19, 176, 29, (int) (16*(getBlockEntity().recipeHandler.getProcessMax()-getBlockEntity().recipeHandler.getProcess())*1f/getBlockEntity().recipeHandler.getProcessMax()), 43,256,256);
		}
	}

}
