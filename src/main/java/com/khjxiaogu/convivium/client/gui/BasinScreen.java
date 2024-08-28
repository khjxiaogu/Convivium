/*
 * Copyright (c) 2023 IEEM Trivium Society/khjxiaogu
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
import java.util.Optional;

import com.khjxiaogu.convivium.CVMain;
import com.khjxiaogu.convivium.blocks.basin.BasinBlockEntity;
import com.khjxiaogu.convivium.blocks.basin.BasinContainer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.teammoeg.caupona.client.util.GuiUtils;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class BasinScreen extends AbstractContainerScreen<BasinContainer> {
	private ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CVMain.MODID, "textures/gui/basin.png");

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

	@Override
	public void render(GuiGraphics transform, int mouseX, int mouseY, float partial) {
		tooltip.clear();
		super.render(transform, mouseX, mouseY, partial);
		if(!blockEntity.tankin.isEmpty()) {
			if (isMouseIn(mouseX, mouseY, 62, 24, 16, 37)) {
				tooltip.add(blockEntity.tankin.getFluid().getDisplayName());
			}
			GuiUtils.handleGuiTank(transform, blockEntity.tankin, leftPos + 62, topPos + 24, 16, 37);
		}

		if (!tooltip.isEmpty())
			transform.renderTooltip(this.font,tooltip,Optional.empty(), mouseX, mouseY);
		else
			super.renderTooltip(transform, mouseX, mouseY);

	}

	protected void renderLabels(GuiGraphics matrixStack, int x, int y) {
		matrixStack.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY,4210752, false);

		Component name = this.playerInventoryTitle;
		matrixStack.drawString(this.font, name, this.inventoryLabelX, this.inventoryLabelY,4210752, false);
	}

	@Override
	protected void renderBg(GuiGraphics transform, float partial, int x, int y) {
		this.renderBackground(transform, x, y, partial);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

		transform.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		if(blockEntity.isLastHeating) {
			transform.blit(TEXTURE, leftPos+37, topPos+28, 176, 0, 16, 29);
		}
		if (blockEntity.processMax > 0) {
			transform.blit(TEXTURE, leftPos + 82, topPos + 19, 176, 29, (int) (16*(blockEntity.processMax-blockEntity.process)*1f/blockEntity.processMax), 43);
		}
	}

	public boolean isMouseIn(int mouseX, int mouseY, int x, int y, int w, int h) {
		return mouseX >= leftPos + x && mouseY >= topPos + y && mouseX < leftPos + x + w && mouseY < topPos + y + h;
	}

}
