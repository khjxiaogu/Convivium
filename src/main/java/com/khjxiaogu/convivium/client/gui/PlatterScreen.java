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

package com.khjxiaogu.convivium.client.gui;

import java.util.ArrayList;
import java.util.Optional;

import com.khjxiaogu.convivium.CVMain;
import com.khjxiaogu.convivium.blocks.platter.PlatterBlockEntity;
import com.khjxiaogu.convivium.blocks.platter.PlatterContainer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.teammoeg.caupona.CPMain;
import com.teammoeg.caupona.client.gui.ImageButton;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class PlatterScreen extends AbstractContainerScreen<PlatterContainer> {
	static final ResourceLocation TEXTURE = new ResourceLocation(CVMain.MODID, "textures/gui/fruit_platter.png");

	PlatterBlockEntity blockEntity;

	public PlatterScreen(PlatterContainer container, Inventory inv, Component titleIn) {
		super(container, inv, titleIn);
		this.titleLabelY = 4;
		this.titleLabelX = 7;
		this.inventoryLabelY = this.imageHeight - 92;
		this.inventoryLabelX = 4;
		blockEntity = container.tile;
	}
	public static MutableComponent cpile = Utils.translate("gui." + CVMain.MODID + ".fruit_platter.piled");
	public static MutableComponent cgrid = Utils.translate("gui." + CVMain.MODID + ".fruit_platter.grided");
	public static MutableComponent csep = Utils.translate("gui." + CVMain.MODID + ".fruit_platter.seperated");
	public static MutableComponent cnorth = Utils.translate("gui." + CVMain.MODID + ".fruit_platter.north");
	public static MutableComponent cmodel = Utils.translate("gui." + CVMain.MODID + ".fruit_platter.model");
	public static MutableComponent citem = Utils.translate("gui." + CVMain.MODID + ".fruit_platter.item");
	private ArrayList<Component> tooltip = new ArrayList<>(2);
	ImageButton btn1;
	ImageButton btn2;
	ImageButton btnsl1;
	ImageButton btnsl2;
	ImageButton btnsl3;
	ImageButton btnsl4;
	@Override
	public void init() {
		super.init();
		this.clearWidgets();
		this.addRenderableWidget(btn1 = new ImageButton(
				Button.builder(cpile, btn -> 
			blockEntity.sendMessage((short) 0,2-btn1.state)).pos(leftPos + 154, topPos + 2).size(20, 20)
				, 176, 40, 256, 256, TEXTURE,
				() -> btn1.state == 2 ? Tooltip.create(cpile) :(btn1.state==1 ? Tooltip.create(cgrid):Tooltip.create(csep))));
		
	}

	@Override
	public void render(GuiGraphics transform, int mouseX, int mouseY, float partial) {
		tooltip.clear();
		super.render(transform, mouseX, mouseY, partial);
		if (!tooltip.isEmpty())
			transform.renderTooltip(this.font,tooltip,Optional.empty(), mouseX, mouseY);
		else
			super.renderTooltip(transform, mouseX, mouseY);

	}

	protected void renderLabels(GuiGraphics matrixStack, int x, int y) {
		matrixStack.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);

		Component name = this.playerInventoryTitle;
		int w = this.font.width(name.getString());
		matrixStack.drawString(this.font, name, this.imageWidth - w - this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
	}

	@Override
	protected void renderBg(GuiGraphics transform, float partial, int x, int y) {
		this.renderBackground(transform);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

		transform.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

	}

	public boolean isMouseIn(int mouseX, int mouseY, int x, int y, int w, int h) {
		return mouseX >= leftPos + x && mouseY >= topPos + y && mouseX < leftPos + x + w && mouseY < topPos + y + h;
	}

}
