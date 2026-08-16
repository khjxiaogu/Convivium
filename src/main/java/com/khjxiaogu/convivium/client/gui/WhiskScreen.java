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
import java.util.Objects;

import com.khjxiaogu.convivium.CVMain;
import com.khjxiaogu.convivium.blocks.whisk.WhiskBlockEntity;
import com.khjxiaogu.convivium.blocks.whisk.WhiskContainer;
import com.khjxiaogu.convivium.data.recipes.RelishFluidRecipe;
import com.khjxiaogu.convivium.data.recipes.RelishRecipe;
import com.khjxiaogu.convivium.util.BeverageInfo;
import com.khjxiaogu.convivium.util.Constants;
import com.khjxiaogu.convivium.util.CurrentSwayInfo;
import com.khjxiaogu.convivium.util.RotationUtils;
import com.teammoeg.caupona.client.gui.ImageButton;
import com.teammoeg.caupona.client.util.FluidRenderHelper;
import com.teammoeg.caupona.util.Utils;

import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

public class WhiskScreen extends AbstractContainerScreen<WhiskContainer> {
	private Identifier TEXTURE = Identifier.fromNamespaceAndPath(CVMain.MODID, "textures/gui/whisk.png");

	WhiskBlockEntity blockEntity;

	public WhiskScreen(WhiskContainer container, Inventory inv, Component titleIn) {
		super(container, inv, titleIn, 176, 222);

		this.titleLabelY = 4;
		this.titleLabelX = 5;
		this.inventoryLabelX = 6;
		blockEntity = container.getBlock();

		this.inventoryLabelY = this.imageHeight - 91;
	}

	private ArrayList<Component> tooltip = new ArrayList<>(2);
	ImageButton btn1;
	ImageButton btn2;

	@Override
	public void init() {
		super.init();
		this.addRenderableWidget(btn1 = new ImageButton(
				Button.builder(getBlockEntity().heating.text, _ -> {menu.sendMessage((short) 1,btn1.state);}).pos(leftPos + 150, topPos + 111).size(20, 20)
				, 176, 30, 256, 256, TEXTURE,
				() -> Tooltip.create(getBlockEntity().heating.text)));
	}

	public void drawActiveSway(GuiGraphicsExtractor transform,int x,int y,CurrentSwayInfo info) {
		transform.blit(RenderPipelines.GUI_TEXTURED,info.image, leftPos + x, topPos + y, 0, 0, 9, 9,9,9);
	}
	public void drawTaste(GuiGraphicsExtractor transform,int barIdx,float value) {
		if(value>0)
			transform.blit(RenderPipelines.GUI_TEXTURED,TEXTURE, leftPos + 20+21*barIdx,topPos + 33+Mth.ceil(37*(1-(value/10))), 176+7*barIdx,122+Mth.ceil(37*(1-(value/10))), 7, Mth.floor(37*(value/10)),256,256);
		else
			transform.blit(RenderPipelines.GUI_TEXTURED,TEXTURE, leftPos + 20+21*barIdx,topPos + 91, 176+7*barIdx,122+37, 7, Mth.ceil(37*Mth.abs(value/10)),256,256);
	}
	public void drawSwayBubble(GuiGraphicsExtractor transform,int barIdx,float value,Identifier image,Identifier icon,int mouseX,int mouseY) {
		
		int y=0;
		if(value>=0) {
			y=(int)(37*(1-(value/10)))+33-4;
		}else {
			y=(int)(37*Mth.abs(value/10))+91-4;
		}
		transform.blit(RenderPipelines.GUI_TEXTURED,TEXTURE, leftPos + 27+21*barIdx,topPos + y, 176,110, 14, 12,256,256);
		if(image!=null)
		transform.blit(RenderPipelines.GUI_TEXTURED,image, leftPos + 27+21*barIdx+4, topPos + y, 0, 0, 9, 9, 9, 9);	
		if(isMouseIn(mouseX,mouseY,27+21*barIdx,y,14,12)) {
			tooltip.add(Utils.translate(icon.toLanguageKey("sway","name")));

			tooltip.add(Component.literal((value>=0?"+":"")+value));
		}
	}
	public void drawSwayBubble(GuiGraphicsExtractor transform,int barIdx,CurrentSwayInfo info,Object2FloatOpenHashMap<String> variants,int mouseX,int mouseY) {
		
		String taste=Constants.TASTES[barIdx];
		int v=info.getTasteDelta(taste);
		if(v!=0) {
			drawSwayBubble(transform,barIdx,Mth.floor((v+variants.getFloat(taste))*10f)/10f,info.image,info.icon,mouseX,mouseY);
		}

	}
	/*public void drawSway(GuiGraphicsExtractor transform,int x,int y,CurrentSwayInfo info) {
		transform.blit(info.image, leftPos + x, topPos + y, 0, 0, 18, 18,18,18);
		
		drawDistMarker(transform,leftPos+x-3,topPos+y+21,info.dsweet);
		drawDistMarker(transform,leftPos+x+5,topPos+y+21,info.dpungent);
		drawDistMarker(transform,leftPos+x+13,topPos+y+21,info.drousing);
		drawDistMarker(transform,leftPos+x+1,topPos+y+29,info.dastringent);
		drawDistMarker(transform,leftPos+x+9,topPos+y+29,info.dthick);
	}*/
	//-3,21 5,21 13,21
	//  1,29   9,29
	public void drawDistMarker(GuiGraphicsExtractor transform,int x,int y,int num) {
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
		transform.blit(TEXTURE, x, y, 176+n*8, 56, 8, 8,256,256);
	}
	public boolean isMouseIn(int mouseX, int mouseY, int x, int y, int w, int h) {
		return mouseX >= leftPos + x && mouseY >= topPos + y && mouseX < leftPos + x + w && mouseY < topPos + y + h;
	}

	public WhiskBlockEntity getBlockEntity() {
		return blockEntity;
	}
	private BeverageInfo info;
	private FluidResource fr;
	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		tooltip.clear();
		btn1.state=getBlockEntity().heating.ordinal();
		super.extractRenderState(graphics, mouseX, mouseY, a);
		if (getBlockEntity().processMax == 0) {
			if (getBlockEntity().tank.getAmountAsInt(0)>0) {
				FluidRenderHelper.handleGuiTank(graphics, getBlockEntity().tank, leftPos + 134, topPos + 58, 16, 46, mouseX, mouseY, tooltip::add);
				FluidResource fluid=getBlockEntity().tank.getResource(0);
				if(!Objects.equals(fr, fluid))
					info=WhiskBlockEntity.getOrCreate(fluid);
				/*if (isMouseIn(mouseX, mouseY, 134, 58, 16, 46)) {
					if(info!=null)
						info.addToTooltip(TooltipContext.EMPTY,tooltip::add, TooltipFlag.NORMAL, fluid);

				}*/
				
				int i=0;
				for(Entry<Holder<Fluid>> f:info.relishes.object2IntEntrySet()) {
					
					RecipeHolder<RelishFluidRecipe> rr=RelishFluidRecipe.recipes.get(f.getKey());
					for(int j=0;j<f.getIntValue();j++) {
						if(rr!=null) {
							RecipeHolder<RelishRecipe> r=RelishRecipe.recipes.get(rr.value().relish);
							graphics.blit(RenderPipelines.GUI_TEXTURED,Identifier.fromNamespaceAndPath(CVMain.MODID,"textures/gui/relishes/"+rr.value().relish+".png")
							, leftPos + 158, topPos + 58+9*(4-i), 0, 0,9, 9,9,9);

							if(isMouseIn(mouseX, mouseY, 158,58+9*(4-i++), 9, 9)) {
								tooltip.add(r.value().getText());
							}
						}
					}
					
				}

				drawTaste(graphics,0,info.variants.getFloat("sweetness"));
				drawTaste(graphics,1,info.variants.getFloat("astringency"));
				drawTaste(graphics,2,info.variants.getFloat("pungency"));
				drawTaste(graphics,3,info.variants.getFloat("thickness"));
				drawTaste(graphics,4,info.variants.getFloat("soothingness"));
				if(!getBlockEntity().swayhint.isEmpty()) {
					int n2=0;
					for(CurrentSwayInfo swh:getBlockEntity().swayhint) {
						if(swh.getActive()>0) {
							
							drawActiveSway(graphics,21+9*(n2++),17,swh);
						}else {
							for(int n=0;n<Constants.TASTES.length;n++) {
								drawSwayBubble(graphics,n,swh,info.variants,mouseX,mouseY);
							}
						}
					}
					
				}
			}
			
		}
		if (!tooltip.isEmpty()) {
			graphics.setComponentTooltipForNextFrame(this.font, tooltip, mouseX, mouseY);
		}
	}

	@Override
	protected void extractLabels(GuiGraphicsExtractor graphics, int xm, int ym) {
		graphics.text(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);

		Component name = this.playerInventoryTitle;
		graphics.text(this.font, name, this.inventoryLabelX , this.inventoryLabelY, 4210752, false);
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		super.extractBackground(graphics, mouseX, mouseY, a);
		graphics.blit(RenderPipelines.GUI_TEXTURED,TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
		/*if (getBlockEntity().getSpeed() > 0) {
			graphics.blit(RenderPipelines.GUI_TEXTURED,TEXTURE, leftPos + 128, topPos + 8, 176, 0, 24, 24, 256, 256);
		}*/
		if (getBlockEntity().isHeating) {
			graphics.blit(RenderPipelines.GUI_TEXTURED,TEXTURE, leftPos + 129, topPos + 111, 176, 11, 19, 19, 256, 256);
		}
		if (getBlockEntity().processMax > 0) {
			graphics.blit(RenderPipelines.GUI_TEXTURED,TEXTURE, leftPos + 135, topPos + 36, 176, 0,
					(int) (14 * (getBlockEntity().processMax - getBlockEntity().process) * 1f / getBlockEntity().processMax), 11, 256, 256);
		}
		if (getBlockEntity().convertion.getFinishedProgress()>0) {
			graphics.blit(RenderPipelines.GUI_TEXTURED,TEXTURE, leftPos + 135, topPos + 36, 176, 0,
					(int) (14 * getBlockEntity().convertion.getFinishedProgress() * 1f / getBlockEntity().convertion.getProcessMax()), 11, 256, 256);
		}
		if (getBlockEntity().processMax > 0) {
			int idx=0;
			if(getBlockEntity().getSpeed()>0)
				idx=(RotationUtils.getTicks()/5)%4;
			graphics.blit(RenderPipelines.GUI_TEXTURED,TEXTURE, leftPos+131,topPos+55, 234, 52*idx, 22,52, 256, 256);
		}
		
	}

}
