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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class FruitPlatterRenderer implements BlockEntityRenderer<PlatterBlockEntity> {
	public static final Map<Item,FruitModel> models=new HashMap<>();
	public static class FruitPlatterRenderingContext{
		private static final Quaternionf[] piled_rotations=new Quaternionf[] {
			new Quaternionf(new AxisAngle4f((float) (Math.PI/2*2/4),0,1,0)),
			new Quaternionf(new AxisAngle4f((float) (Math.PI/2*3/4),0,1,0)),
			new Quaternionf(new AxisAngle4f((float) (Math.PI/2*4/4),0,1,0)),
			new Quaternionf(new AxisAngle4f((float) (Math.PI/2*5/4),0,1,0))
		};
		private static final Quaternionf[] grided_rotations=new Quaternionf[] {
			new Quaternionf().rotateXYZ((float) ((90+10)/180f*Math.PI),-(float) (10/180f*Math.PI),-(float) (15/180f*Math.PI)),
			new Quaternionf().rotateXYZ((float) ((90+10)/180f*Math.PI),+(float) (15/180f*Math.PI),-(float) (15/180f*Math.PI)),
			new Quaternionf().rotateXYZ((float) ((90-15)/180f*Math.PI),-(float) (15/180f*Math.PI),+(float) (15/180f*Math.PI)),
			new Quaternionf().rotateXYZ((float) ((90-15)/180f*Math.PI),+(float) (15/180f*Math.PI),+(float) (15/180f*Math.PI))
		};
		@SuppressWarnings("unchecked")
		private static final ImmutableSet<String>[] model_names=new ImmutableSet[] {
			ImmutableSet.of("FruitUnit1"),
			ImmutableSet.of("FruitUnit2"),
			ImmutableSet.of("FruitUnit3"),
			ImmutableSet.of("FruitUnit4")
		}; 
		public static class FruitPlatterRenderingPart{
			int type;
			int modelIndex;
			FruitModel model;
			ItemStack stack;
			public FruitPlatterRenderingPart(FruitModel model,boolean isGrided) {
				super();
				type=isGrided?5:1;
				this.model = model;
			}
			public FruitPlatterRenderingPart(int modelIndex, FruitModel model) {
				super();
				type=2;
				this.modelIndex = modelIndex;
				this.model = model;
			}
			public FruitPlatterRenderingPart(ItemStack stack,boolean isGrided) {
				super();
				type=isGrided?4:3;
				this.stack = stack;
			}
		}
		public void setPart(int position,FruitModel model,boolean isGrided) {
			parts[position-1]=new FruitPlatterRenderingPart(model,isGrided);
		}
		public void setPart(int position,int modelIndex, FruitModel model) {
			parts[position-1]=new FruitPlatterRenderingPart(modelIndex,model);
		}
		public void setPart(int position,ItemStack stack,boolean isGrided) {
			parts[position-1]=new FruitPlatterRenderingPart(stack,isGrided);
		}
		FruitPlatterRenderingPart[] parts=new FruitPlatterRenderingPart[4];
		public void render(ItemRenderer render,BlockEntity blockEntity,MultiBufferSource buffer,PoseStack matrixStack,int combinedLightIn, int combinedOverlayIn) {
			
			for(int i=1;i<=4;i++) {
				FruitPlatterRenderingPart cpart=parts[i-1];
				if(cpart!=null) {
					switch(cpart.type) {
					case 1:renderPartPiledAllFruit(i,cpart.model,blockEntity,buffer,matrixStack,combinedLightIn,combinedOverlayIn);break;
					case 2:renderPartPiledSingleFruit(i,cpart.modelIndex,cpart.model,blockEntity,buffer,matrixStack,combinedLightIn,combinedOverlayIn);break;
					case 3:renderPartPiledItem(i,cpart.stack,render,blockEntity,buffer,matrixStack,combinedLightIn,combinedOverlayIn);break;
					case 4:renderPartGridedItem(i,cpart.stack,render,blockEntity,buffer,matrixStack,combinedLightIn,combinedOverlayIn);break;
					case 5:renderGridedFruit(i,cpart.model,blockEntity,buffer,matrixStack,combinedLightIn,combinedOverlayIn);break;
					}
				}
				
			}
			
		}
		public static void renderPartPiledAllFruit(int position,FruitModel rss,BlockEntity blockEntity,MultiBufferSource buffer,PoseStack matrixStack,int combinedLightIn, int combinedOverlayIn) {
			ModelUtils.tesellateModel(blockEntity,rss.getPiled(position-1), rss.getBuffer(buffer),matrixStack, combinedOverlayIn);
		}
		public static void renderPartPiledSingleFruit(int position,int modelIndex,FruitModel rss,BlockEntity blockEntity,MultiBufferSource buffer,PoseStack matrixStack,int combinedLightIn, int combinedOverlayIn) {
			ModelUtils.tesellateModelGroups(blockEntity,rss.getPiled(modelIndex), rss.getBuffer(buffer),model_names[position-1],matrixStack,  combinedOverlayIn);
		}
		public static void renderGridedFruit(int position,FruitModel rss,BlockEntity blockEntity,MultiBufferSource buffer,PoseStack matrixStack,int combinedLightIn, int combinedOverlayIn) {
			ModelUtils.renderModelGroups(rss.getGrid(), rss.getBuffer(buffer),model_names[position-1],
				matrixStack, combinedLightIn, combinedOverlayIn);
		}
		public static void renderPartPiledItem(int position,ItemStack is,ItemRenderer render,BlockEntity blockEntity,MultiBufferSource buffer,PoseStack matrixStack,int combinedLightIn, int combinedOverlayIn) {
			matrixStack.pushPose();
			matrixStack.translate(0.375, 3/16f, 0.5f);
			matrixStack.scale(1.5f,1, 1.5f);
			matrixStack.mulPose(piled_rotations[position-1]);
			matrixStack.mulPose(GuiUtils.rotate90);
			matrixStack.translate(0,0,-(position-1)/32f);
			render.render(is, ItemDisplayContext.GROUND, false,
					matrixStack, buffer,combinedLightIn, OverlayTexture.NO_OVERLAY,render.getModel(is, blockEntity.getLevel(),null,(int) blockEntity.getBlockPos().asLong()));
			matrixStack.popPose();
		}
		public static void renderPartGridedItem(int position,ItemStack is,ItemRenderer render,BlockEntity blockEntity,MultiBufferSource buffer,PoseStack matrixStack,int combinedLightIn, int combinedOverlayIn) {

			
			matrixStack.pushPose();
			matrixStack.translate((((position&1)==0)?11/16f:5/16f),3/16f,(position<=2?4/16f:11/16f));
			
			matrixStack.mulPose(grided_rotations[position-1]);
			//matrixStack.scale(.85f, .85f, .85f);
			render.render(is, ItemDisplayContext.GROUND, false,
					matrixStack, buffer,combinedLightIn, OverlayTexture.NO_OVERLAY,render.getModel(is, blockEntity.getLevel(),null,(int) blockEntity.getBlockPos().asLong()));
			matrixStack.popPose();
		}
		
		

	}
	private final ItemRenderer render;
	/**
	 * @param rendererDispatcherIn
	 */
	public FruitPlatterRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
		render=rendererDispatcherIn.getItemRenderer();
	}


	public void fillContext(PlatterBlockEntity blockEntity,FruitPlatterRenderingContext ctx) {
		System.out.println("updated rendering info");
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
					ctx.setPart(items.get(is), rss,false);
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
						ctx.setPart(++j,total-1,model[i]);
					}
					return;
				}
				
			}
		}
		if(blockEntity.config==GlobalConfig.PILED) {
			for(int i=1;i<=4;i++) {
				ItemStack is=blockEntity.storage.getStackInSlot(i-1);
				if(is.isEmpty())continue;
				ctx.setPart(i, is, false);
			}
			return;
		}
		
		for(int i=1;i<=4;i++) {
			ItemStack is=blockEntity.storage.getStackInSlot(i-1);
			if(!is.isEmpty()) {
				if(model[i-1]!=null) {
					ctx.setPart(i,model[i-1],true);
				}else {
					ctx.setPart(i, is, true);
				}
			}
		}
	}
	public FruitPlatterRenderingContext getOrCreateContext(PlatterBlockEntity blockEntity) {
		if(blockEntity.renderingContext==null) {
			synchronized(blockEntity.renderingContextLock) {
				if(blockEntity.renderingContext==null) {
					FruitPlatterRenderingContext ctx=new FruitPlatterRenderingContext();
					fillContext(blockEntity,ctx);
					blockEntity.renderingContext=ctx;
				}
			}
		}
		return (FruitPlatterRenderingContext) blockEntity.renderingContext;
		
	}
	@SuppressWarnings({ "resource", "deprecation" })
	@Override
	public void render(PlatterBlockEntity blockEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer,
			int combinedLightIn, int combinedOverlayIn) {
		if (!blockEntity.getLevel().hasChunkAt(blockEntity.getBlockPos()))
			return;
		BlockState state = blockEntity.getBlockState();
		if (!state.is(CVBlocks.platter.get()))
			return;
		getOrCreateContext(blockEntity).render(render, blockEntity, buffer, matrixStack, combinedLightIn, combinedOverlayIn);
		//System.out.println("render");
		
		
	}

}