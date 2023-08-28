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

package com.khjxiaogu.convivium.client.renderer;

import java.util.HashMap;
import java.util.Map;

import org.joml.AxisAngle4f;
import org.joml.Quaternionf;

import com.google.common.collect.ImmutableSet;
import com.khjxiaogu.convivium.CVBlocks;
import com.khjxiaogu.convivium.blocks.platter.GlobalConfig;
import com.khjxiaogu.convivium.blocks.platter.PlatterBlockEntity;
import com.khjxiaogu.convivium.blocks.platter.SlotConfig;
import com.khjxiaogu.convivium.client.renderer.FruitModel.ModelType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.teammoeg.caupona.client.util.GuiUtils;
import com.teammoeg.caupona.client.util.ModelUtils;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class FruitPlatterRenderer implements BlockEntityRenderer<PlatterBlockEntity> {
	public static final Map<Item,FruitModel> models=new HashMap<>();
	private final ItemRenderer render;
	/**
	 * @param rendererDispatcherIn
	 */
	public FruitPlatterRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
		render=rendererDispatcherIn.getItemRenderer();
	}

	private static Quaternionf[] rotations=new Quaternionf[] {
			new Quaternionf(new AxisAngle4f((float) (Math.PI/2*2/4),0,1,0)),
			new Quaternionf(new AxisAngle4f((float) (Math.PI/2*3/4),0,1,0)),
			new Quaternionf(new AxisAngle4f((float) (Math.PI/2*4/4),0,1,0)),
			new Quaternionf(new AxisAngle4f((float) (Math.PI/2*5/4),0,1,0))
	};
	@SuppressWarnings({ "resource", "deprecation" })
	@Override
	public void render(PlatterBlockEntity blockEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer,
			int combinedLightIn, int combinedOverlayIn) {
		if (!blockEntity.getLevel().hasChunkAt(blockEntity.getBlockPos()))
			return;
		BlockState state = blockEntity.getBlockState();
		if (!state.is(CVBlocks.platter.get()))
			return;
		//System.out.println("render");
		Map<Item,Integer> items=new HashMap<>();
		boolean canFull=blockEntity.config==GlobalConfig.PILED;
		FruitModel[] model=new FruitModel[4];
		for(int i=0;i<4;i++) {
			ItemStack is=blockEntity.storage.getStackInSlot(i);
			if(!is.isEmpty()) {
				Item it=is.getItem();
				items.compute(it, (k,v)->v==null?1:v+1);
				if(blockEntity.config!=GlobalConfig.SEPERATE||blockEntity.slotconfig[i]==SlotConfig.MODEL) {
					model[i]=models.get(it);
					if(model[i]!=null)continue;
				}
				canFull=false;
			}
		}
		if(canFull) {
			if(items.size()==1) {
				Item is=items.keySet().stream().findFirst().orElse(null);
				FruitModel rss=models.get(is);
				if(rss!=null) {
					ModelUtils.tesellateModel(blockEntity,rss.getPiled(items.get(is)-1), rss.getBuffer(buffer),matrixStack, combinedOverlayIn);
					return;
				}
			}else {
				ModelType mt=null;
				int total=0;
				for(int i=0;i<4;i++) {
					if(model[i]==null)continue;
					ModelType crn=model[i].getType();
					total++;
					if(crn==ModelType.MISC) {
						canFull=false;
						break;
					}else if(mt==null) {
						mt=crn;
					}else if(model[i].getType()!=mt) {
						canFull=false;
						break;
					}
				}
				if(canFull) {
					int j=0;
					for(int i=0;i<4;i++) {
						if(model[i]==null)continue;
						j++;
						ModelUtils.tesellateModelGroups(blockEntity,model[i].getPiled(total-1), model[i].getBuffer(buffer),ImmutableSet.of("FruitUnit"+j),
								matrixStack,  combinedOverlayIn);
					}
					return;
				}
				
			}
		}
		if(blockEntity.config==GlobalConfig.PILED) {
			int j=0;
			matrixStack.pushPose();
			matrixStack.translate(0.375, 3/16f, 0.5f);
			matrixStack.scale(1.5f,1, 1.5f);
			
			
			for(int i=1;i<=4;i++) {
				ItemStack is=blockEntity.storage.getStackInSlot(i-1);
				if(is.isEmpty())continue;
				matrixStack.pushPose();
				matrixStack.mulPose(rotations[i-1]);
				matrixStack.mulPose(GuiUtils.rotate90);
				matrixStack.translate(0,0,-(j++)/32f);
				
				
				render.render(is, ItemDisplayContext.GROUND, false,
						matrixStack, buffer,combinedLightIn, OverlayTexture.NO_OVERLAY,render.getModel(is, blockEntity.getLevel(),null,(int) blockEntity.getBlockPos().asLong()));
				matrixStack.popPose();
			}
			matrixStack.popPose();
			return;
		}
		
		for(int i=1;i<=4;i++) {
			ItemStack is=blockEntity.storage.getStackInSlot(i-1);
			if(!is.isEmpty()) {
				if(model[i-1]!=null) {
				ModelUtils.renderModelGroups(model[i-1].getGrid(), model[i-1].getBuffer(buffer),ImmutableSet.of("FruitUnit"+i),
						matrixStack, combinedLightIn, combinedOverlayIn);
				}else {
					float rx=0;
					float ry=0;
					float rz=0;
					switch(i) {
					case 1:rx=+(float) (90/180f*Math.PI);rz=+(float) (90/180f*Math.PI);ry=+(float) (90/180f*Math.PI);break;
					case 2:rx=+(float) (90/180f*Math.PI);rz=+(float) (90/180f*Math.PI);ry=-(float) (90/180f*Math.PI);break;
					case 3:rx=-(float) (90/180f*Math.PI);rz=-(float) (90/180f*Math.PI);ry=-(float) (90/180f*Math.PI);break;
					case 4:rx=-(float) (90/180f*Math.PI);rz=-(float) (90/180f*Math.PI);ry=+(float) (90/180f*Math.PI);break;
					}
					matrixStack.pushPose();
					//matrixStack.translate(, combinedLightIn, combinedOverlayIn);
					matrixStack.translate((((i&1)==0)?11:5)/16f,3/16f,(i<=2?5:11)/16f);
					matrixStack.scale(.5f, .5f, .5f);
					matrixStack.mulPose(new Quaternionf().rotateXYZ(rx,ry,rz));
					
					render.render(is, ItemDisplayContext.GROUND, false,
							matrixStack, buffer,combinedLightIn, OverlayTexture.NO_OVERLAY,render.getModel(is, blockEntity.getLevel(),null,(int) blockEntity.getBlockPos().asLong()));
					matrixStack.popPose();
				}
			}
		}
		
	}

}