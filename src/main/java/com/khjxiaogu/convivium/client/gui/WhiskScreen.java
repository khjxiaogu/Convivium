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
import com.khjxiaogu.convivium.blocks.whisk.WhiskBlockEntity;
import com.khjxiaogu.convivium.blocks.whisk.WhiskContainer;
import com.khjxiaogu.convivium.data.recipes.RelishFluidRecipe;
import com.khjxiaogu.convivium.data.recipes.RelishRecipe;
import com.khjxiaogu.convivium.util.Constants;
import com.khjxiaogu.convivium.util.CurrentSwayInfo;
import com.khjxiaogu.convivium.util.RotationUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import com.teammoeg.caupona.client.gui.ImageButton;
import com.teammoeg.caupona.client.util.GuiUtils;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.material.Fluid;

public class WhiskScreen extends AbstractContainerScreen<WhiskContainer> {
	private ResourceLocation TEXTURE = new ResourceLocation(CVMain.MODID, "textures/gui/whisk.png");

	WhiskBlockEntity blockEntity;

	public WhiskScreen(WhiskContainer container, Inventory inv, Component titleIn) {
		super(container, inv, titleIn);

		this.titleLabelY = 4;
		this.titleLabelX = 5;
		this.inventoryLabelX = 6;
		blockEntity = container.getBlock();

		this.imageHeight = 222;
		this.inventoryLabelY = this.imageHeight - 91;
	}

	private ArrayList<Component> tooltip = new ArrayList<>(2);
	ImageButton btn1;
	ImageButton btn2;
	public static MutableComponent rs = Utils.translate("gui." + CVMain.MODID + ".whisk.redstone");
	public static MutableComponent nors = Utils.translate("gui." + CVMain.MODID + ".whisk.manual");
	public static MutableComponent hon = Utils.translate("gui." + CVMain.MODID + ".whisk.heat_on");
	public static MutableComponent hoff = Utils.translate("gui." + CVMain.MODID + ".whisk.heat_off");
	public static MutableComponent hrs = Utils.translate("gui." + CVMain.MODID + ".whisk.heat_redstone");
	@Override
	public void init() {
		super.init();
		this.addRenderableWidget(btn1 = new ImageButton(
				Button.builder(hon, btn -> {if(btn1.state!=2)
			blockEntity.sendMessage((short) 1,btn1.state);}).pos(leftPos + 119, topPos + 117).size(20, 20)
				, 176, 72, 256, 256, TEXTURE,
				() -> btn1.state == 2 ? Tooltip.create(hrs) :(btn1.state==1 ? Tooltip.create(hoff):Tooltip.create(hon))));
		
		this.addRenderableWidget(btn2 = new ImageButton(
				Button.builder(hon, btn -> {
			blockEntity.sendMessage((short) 0,btn2.state);}).pos(leftPos + 141, topPos + 117).size(20, 20)
				, 176, 132, 256, 256, TEXTURE,
				() -> (btn2.state==0 ? Tooltip.create(rs):Tooltip.create(nors))));
	}

	@Override
	public void render(GuiGraphics transform, int mouseX, int mouseY, float partial) {
		tooltip.clear();
		btn2.state=blockEntity.rs?0:1;
		btn1.state=btn2.state==0?2:(blockEntity.isHeating?0:1);
		super.render(transform, mouseX, mouseY, partial);
		if (blockEntity.processMax == 0) {
			if (!blockEntity.tank.isEmpty()) {
				if (isMouseIn(mouseX, mouseY, 132, 45, 16, 46)) {
					tooltip.add(blockEntity.tank.getFluid().getDisplayName());
					if(blockEntity.info!=null)
						blockEntity.info.appendTooltip(tooltip);
				}
				GuiUtils.handleGuiTank(transform.pose(), blockEntity.tank, leftPos + 132, topPos + 45, 16, 46);
			}
			
			if(blockEntity.info!=null) {
				for(int i=4;i>=0;i--) {
					Fluid f=blockEntity.info.relishes[i];
					if(f!=null) {
						RelishFluidRecipe rr=RelishFluidRecipe.recipes.get(f);
						
						if(rr!=null) {
							RelishRecipe r=RelishRecipe.recipes.get(rr.relish);
							if(isMouseIn(mouseX, mouseY, 152,45+9*(4-i), 19, 9)) {
								tooltip.add(r.getText());
							}
						}
					}
				}
			}else if(!blockEntity.tank.isEmpty()){
				RelishFluidRecipe rr=RelishFluidRecipe.recipes.get(blockEntity.tank.getFluid().getFluid());
				if(rr!=null&&blockEntity.tank.getFluidAmount()>250) {
					RelishRecipe r=RelishRecipe.recipes.get(rr.relish);
					if(r!=null) {
						int i=blockEntity.tank.getFluidAmount()/250;
						if(isMouseIn(mouseX, mouseY, 152,45+9*(5-i), 19, 9*i)) {
							tooltip.add(r.getText());
						}
					}
				}
			}
		}
		if(!blockEntity.swayhint.isEmpty()) {
			int n1=0;
			int n2=0;
			for(CurrentSwayInfo swh:blockEntity.swayhint) {
				if(swh.active>0) {
					if(isMouseIn(mouseX,mouseY,18+20*(n2++),65,18,18))
						tooltip.add(Utils.translate(swh.icon.toLanguageKey("sway","name")));
				}else {
					if(isMouseIn(mouseX,mouseY,9+26*(n1++),90,24,42)) {
						tooltip.add(Utils.translate(swh.icon.toLanguageKey("sway","name")));
						tooltip.add(Utils.translate("gui.convivium.whisk.requires_taste"));
						for(String sway:Constants.TASTES) {
							int sn=swh.getTasteDelta(sway);
							
							tooltip.add(Utils.translate("taste.convivium."+sway,sn==0?Utils.string("~").withStyle(ChatFormatting.AQUA):(sn>0?Utils.string("+".repeat(sn)).withStyle(ChatFormatting.GREEN):Utils.string("-".repeat(-sn)).withStyle(ChatFormatting.GOLD))));
						}
					}
				}
			}
		}
		if (!tooltip.isEmpty())
			transform.renderTooltip(this.font, tooltip, Optional.empty(), mouseX, mouseY);
		else
			super.renderTooltip(transform, mouseX, mouseY);

	}

	protected void renderLabels(GuiGraphics matrixStack, int x, int y) {
		matrixStack.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);

		Component name = this.playerInventoryTitle;
		matrixStack.drawString(this.font, name, this.inventoryLabelX , this.inventoryLabelY, 4210752, false);
	}

	@Override
	protected void renderBg(GuiGraphics transform, float partial, int x, int y) {
		this.renderBackground(transform);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

		transform.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		if (blockEntity.getSpeed() > 0) {
			transform.blit(TEXTURE, leftPos + 128, topPos + 8, 176, 0, 24, 24);
		}
		if (blockEntity.isLastHeating) {
			transform.blit(TEXTURE, leftPos + 130, topPos + 96, 176, 24, 19, 19);
		}
		if (blockEntity.processMax > 0) {
			transform.blit(TEXTURE, leftPos + 111, topPos + 42, 176, 43,
					(int) (17 * (blockEntity.processMax - blockEntity.process) * 1f / blockEntity.processMax), 13);
			int idx=0;
			if(blockEntity.getSpeed()>0)
				idx=(RotationUtils.getTicks()/5)%4;
			transform.blit(TEXTURE, leftPos+129,topPos+42, 234, 52*idx, 22,52);
		}else {
			if(blockEntity.info!=null) {
				for(int i=4;i>=0;i--) {
					Fluid f=blockEntity.info.relishes[i];
					if(f!=null) {
						RelishFluidRecipe rr=RelishFluidRecipe.recipes.get(f);
						//System.out.println(f);
						if(rr!=null) {
							transform.blit(new ResourceLocation(CVMain.MODID,"textures/gui/relishes/"+rr.relish+".png")
							, leftPos + 152, topPos + 45+9*(4-i), 0, 0,
							19, 11,32,32);
						}
					}
				}
			}else if(!blockEntity.tank.isEmpty()){
				RelishFluidRecipe rr=RelishFluidRecipe.recipes.get(blockEntity.tank.getFluid().getFluid());
				if(rr!=null&&blockEntity.tank.getFluidAmount()>250)
					for(int i=blockEntity.tank.getFluidAmount()/250-1;i>=0;i--) {
						transform.blit(new ResourceLocation(CVMain.MODID,"textures/gui/relishes/"+rr.relish+".png")
								, leftPos + 152, topPos + 45+9*(4-i), 0, 0,
								19, 11,32,32);
					}
			}
			if(!blockEntity.swayhint.isEmpty()) {
				int n1=0;
				int n2=0;
				for(CurrentSwayInfo swh:blockEntity.swayhint) {
					if(swh.active>0) {
						drawActiveSway(transform,18+20*(n2++),65,swh);
					}else {
						drawSway(transform,12+26*(n1++),92,swh);
					}
				}
			}
		}
	}
	public void drawActiveSway(GuiGraphics transform,int x,int y,CurrentSwayInfo info) {
		transform.blit(info.image, leftPos + x, topPos + y, 0, 0, 18, 18,18,18);
	}
	public void drawSway(GuiGraphics transform,int x,int y,CurrentSwayInfo info) {
		transform.blit(info.image, leftPos + x, topPos + y, 0, 0, 18, 18,18,18);
		
		drawDistMarker(transform,leftPos+x-3,topPos+y+21,info.dsweet);
		drawDistMarker(transform,leftPos+x+5,topPos+y+21,info.dpungent);
		drawDistMarker(transform,leftPos+x+13,topPos+y+21,info.drousing);
		drawDistMarker(transform,leftPos+x+1,topPos+y+29,info.dastringent);
		drawDistMarker(transform,leftPos+x+9,topPos+y+29,info.dthick);
	}
	//-3,21 5,21 13,21
	//  1,29   9,29
	public void drawDistMarker(GuiGraphics transform,int x,int y,int num) {
		int n=7;
		switch(num) {
		case 0:n=6;break;
		case 1:n=0;break;
		case 2:n=1;break;
		case 3:n=2;break;
		case -1:n=3;break;
		case -2:n=4;break;
		case -3:n=5;break;
		}
		transform.blit(TEXTURE, x, y, 176+n*8, 56, 8, 8);
	}
	public boolean isMouseIn(int mouseX, int mouseY, int x, int y, int w, int h) {
		return mouseX >= leftPos + x && mouseY >= topPos + y && mouseX < leftPos + x + w && mouseY < topPos + y + h;
	}

}
