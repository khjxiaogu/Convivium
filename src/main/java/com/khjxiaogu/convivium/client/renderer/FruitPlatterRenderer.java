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

package com.khjxiaogu.convivium.client.renderer;

import java.util.HashMap;
import java.util.Map;

import org.jspecify.annotations.Nullable;

import com.khjxiaogu.convivium.blocks.platter.GlobalConfig;
import com.khjxiaogu.convivium.blocks.platter.PlatterBlockEntity;
import com.khjxiaogu.convivium.blocks.platter.SlotConfig;
import com.khjxiaogu.convivium.client.renderer.FruitModel.ModelType;
import com.khjxiaogu.convivium.client.renderer.FruitPlatterRenderState.FruitPlatterRenderingContext;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class FruitPlatterRenderer implements BlockEntityRenderer<PlatterBlockEntity,FruitPlatterRenderState> {
	public static final Map<Item,FruitModel> models=new HashMap<>();
	
	private final ItemModelResolver render;
	/**
	 * @param rendererDispatcherIn
	 */
	public FruitPlatterRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
		render=rendererDispatcherIn.itemModelResolver();
	}


	public void fillContext(PlatterBlockEntity blockEntity,FruitPlatterRenderingContext ctx) {
		//System.out.println("updated rendering info");
		Map<Item,Integer> items=new HashMap<>();
		boolean canFull=blockEntity.config==GlobalConfig.PILED;
		FruitModel[] model=new FruitModel[4];
		for(int i=0;i<4;i++) {
			ItemStack is=blockEntity.storage.getResource(i).toStack(blockEntity.storage.getAmountAsInt(i));
			if(!is.isEmpty()) {
				Item it=is.getItem();
				items.compute(it, (_,v)->v==null?1:v+1);
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
				ItemStack is=blockEntity.storage.getResource(i-1).toStack(blockEntity.storage.getAmountAsInt(i-1));
				if(is.isEmpty())continue;
				ctx.setPart(i,render,blockEntity.getLevel(), is, false);
			}
			return;
		}
		
		for(int i=1;i<=4;i++) {
			ItemStack is=blockEntity.storage.getResource(i-1).toStack(blockEntity.storage.getAmountAsInt(i-1));
			if(!is.isEmpty()) {
				if(model[i-1]!=null) {
					ctx.setPart(i,model[i-1],true);
				}else {
					ctx.setPart(i,render,blockEntity.getLevel(), is, true);
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



	@Override
	public FruitPlatterRenderState createRenderState() {
		return new FruitPlatterRenderState();
	}


	@Override
	public void submit(FruitPlatterRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
		QuadInstance qi=new QuadInstance();
		qi.setLightCoords(state.lightCoords);
		state.ctx.submit(poseStack, submitNodeCollector, camera, qi);
	}


	@Override
	public void extractRenderState(PlatterBlockEntity blockEntity, FruitPlatterRenderState state, float partialTicks, Vec3 cameraPosition, @Nullable CrumblingOverlay breakProgress) {
		state.ctx=getOrCreateContext(blockEntity);
	}

}