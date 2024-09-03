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
import com.khjxiaogu.convivium.blocks.vending.BeverageVendingBlockEntity;
import com.khjxiaogu.convivium.blocks.vending.BeverageVendingContainer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.teammoeg.caupona.client.gui.ImageButton;
import com.teammoeg.caupona.client.util.GuiUtils;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class BeverageVendingScreen extends AbstractContainerScreen<BeverageVendingContainer> {
	static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CVMain.MODID, "textures/gui/beverage_vending_machine.png");

	BeverageVendingBlockEntity blockEntity;

	public BeverageVendingScreen(BeverageVendingContainer container, Inventory inv, Component titleIn) {
		super(container, inv, titleIn);
		this.titleLabelY = 4;
		this.titleLabelX = 5;
		this.inventoryLabelY = this.imageHeight - 99;
		this.inventoryLabelX = 7;
		blockEntity = container.getBlock();
	}
	private ArrayList<Component> tooltip = new ArrayList<>(2);
	public static MutableComponent p8 = Utils.string("+8");
	public static MutableComponent p1 = Utils.string("+1");
	public static MutableComponent m1 = Utils.string("-1");
	public static MutableComponent m8 = Utils.string("-8");
	ImageButton btnsl1;
	ImageButton btnsl2;
	ImageButton btnsl3;
	ImageButton btnsl4;
	@Override
	public void init() {
		super.init();
		this.clearWidgets();
		this.addRenderableWidget(btnsl1 = new ImageButton(
				Button.builder(p8, btn -> blockEntity.sendMessage((short) 0,0)).pos(leftPos + 101, topPos + 16).size(14, 14)
				, 176, 14, 256, 256, TEXTURE,
				() -> Tooltip.create(p8)));
		this.addRenderableWidget(btnsl2 = new ImageButton(
				Button.builder(p1, btn -> blockEntity.sendMessage((short) 1,0)).pos(leftPos + 86, topPos + 16).size(14, 14)
				, 176, 0, 256, 256, TEXTURE,
				() -> Tooltip.create(p1)));
		this.addRenderableWidget(btnsl3 = new ImageButton(
				Button.builder(m1, btn -> blockEntity.sendMessage((short) 2,0)).pos(leftPos + 27, topPos + 16).size(14, 14)
				, 176, 28, 256, 256, TEXTURE,
				() -> Tooltip.create(m1)));
		this.addRenderableWidget(btnsl4 = new ImageButton(
				Button.builder(m8, btn -> blockEntity.sendMessage((short) 3,0)).pos(leftPos + 12, topPos + 16).size(14, 14)
				, 176, 42, 256, 256, TEXTURE,
				() -> Tooltip.create(m8)));
	}

	@Override
	public void render(GuiGraphics transform, int mouseX, int mouseY, float partial) {
		tooltip.clear();
		super.render(transform, mouseX, mouseY, partial);
		transform.drawCenteredString(this.font,""+blockEntity.amt,leftPos + 57,topPos + 19, 0xffffff);
		if(!blockEntity.tank.isEmpty()) {
			if (isMouseIn(mouseX, mouseY, 123, 25, 32, 46)) {
				tooltip.add(blockEntity.tank.getFluid().getHoverName());
			}
			GuiUtils.handleGuiTank(transform, blockEntity.tank, leftPos + 123, topPos + 25, 32, 46);
		}
		if (!tooltip.isEmpty())
			transform.renderTooltip(this.font,tooltip,Optional.empty(), mouseX, mouseY);
		else
			super.renderTooltip(transform, mouseX, mouseY);

	}

	protected void renderLabels(GuiGraphics matrixStack, int x, int y) {
		matrixStack.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);

		Component name = this.playerInventoryTitle;
		matrixStack.drawString(this.font, name, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
	}

	@Override
	protected void renderBg(GuiGraphics transform, float partial, int x, int y) {
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		
		transform.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

	}

	public boolean isMouseIn(int mouseX, int mouseY, int x, int y, int w, int h) {
		return mouseX >= leftPos + x && mouseY >= topPos + y && mouseX < leftPos + x + w && mouseY < topPos + y + h;
	}

}
