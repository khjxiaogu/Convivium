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
import java.util.Optional;

import com.khjxiaogu.convivium.CVMain;
import com.khjxiaogu.convivium.blocks.pestle_and_mortar.PamBlockEntity;
import com.khjxiaogu.convivium.blocks.pestle_and_mortar.PamContainer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.teammoeg.caupona.client.util.GuiUtils;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class PamScreen extends AbstractContainerScreen<PamContainer> {
	private ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CVMain.MODID, "textures/gui/pestle_and_mortar.png");

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

	@Override
	public void init() {
		super.init();
	}

	@Override
	public void render(GuiGraphics transform, int mouseX, int mouseY, float partial) {
		tooltip.clear();
		super.render(transform, mouseX, mouseY, partial);
		if(!getBlockEntity().tankin.isEmpty()) {
			if (isMouseIn(mouseX, mouseY, 42, 19, 16, 37)) {
				tooltip.add(getBlockEntity().tankin.getFluid().getHoverName());
			}
			GuiUtils.handleGuiTank(transform, getBlockEntity().tankin, leftPos + 42, topPos + 19, 16, 37);
		}
		if(!getBlockEntity().tankout.isEmpty()) {
			if (isMouseIn(mouseX, mouseY, 133, 34, 16, 37)) {
				tooltip.add(getBlockEntity().tankout.getFluid().getHoverName());
			}
			GuiUtils.handleGuiTank(transform, getBlockEntity().tankout, leftPos + 133, topPos + 34, 16, 37);
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
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

		transform.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		if(getBlockEntity().getSpeed()>0) {
			transform.blit(TEXTURE, leftPos+86, topPos+1, 176, 0, 24, 24);
		}
		if (getBlockEntity().recipeHandler.getProcessMax() > 0) {
			transform.blit(TEXTURE, leftPos + 108, topPos + 23, 176, 24, (int) (22*(getBlockEntity().recipeHandler.getFinishedProgress())*1f/getBlockEntity().recipeHandler.getProcessMax()), 15);
		}
	}

	public boolean isMouseIn(int mouseX, int mouseY, int x, int y, int w, int h) {
		return mouseX >= leftPos + x && mouseY >= topPos + y && mouseX < leftPos + x + w && mouseY < topPos + y + h;
	}

	public PamBlockEntity getBlockEntity() {
		return blockEntity;
	}

}
