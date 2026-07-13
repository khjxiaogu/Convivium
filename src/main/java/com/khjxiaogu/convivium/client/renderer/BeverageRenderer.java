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

import com.khjxiaogu.convivium.CVBlocks;
import com.khjxiaogu.convivium.CVItems;
import com.khjxiaogu.convivium.CVMain;
import com.khjxiaogu.convivium.blocks.foods.BeverageBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.teammoeg.caupona.client.util.DynamicBlockModelReference;
import com.teammoeg.caupona.client.util.FluidRenderHelper;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Util;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;

public class BeverageRenderer implements BlockEntityRenderer<BeverageBlockEntity,BeverageRenderState> {
	private static final Map<Block,DynamicBlockModelReference> MODELS=Util.make(()->{
		Map<Block,DynamicBlockModelReference> map=new HashMap<>();
		for(String s:CVItems.BOTTLE_TYPES) {
			map.put(BuiltInRegistries.BLOCK.getValue(CVMain.rl("beverage_"+s)), DynamicBlockModelReference.getModel(CVMain.rl("block/dynamic/beverage_"+s)));
		}
		map.put(CVBlocks.BEVERAGE.get(), DynamicBlockModelReference.getModel(CVMain.rl("block/dynamic/beverage")));
		return map;
	});
	/*@SuppressWarnings("unchecked")
	public static final Pair<Vec3,Quaternionf>[] rots=new Pair[] {
		Pair.of(Vec3.ZERO.add(0,0,0), new Quaternionf()),//side
		Pair.of(Vec3.ZERO.add(0,0,6/16f), new Quaternionf().rotateY((float) (Math.PI/2))),//side
		Pair.of(Vec3.ZERO.add(6/16f,0,6/16f), new Quaternionf().rotateY((float) (Math.PI))),//side
		Pair.of(Vec3.ZERO.add(6/16f,0,0), new Quaternionf().rotateY(-(float) (Math.PI/2))), //side
		Pair.of(Vec3.ZERO.add(0, 6/16f, 0), new Quaternionf().rotateX((float) (Math.PI/2))),
		Pair.of(Vec3.ZERO.add(0,0,6/16f), new Quaternionf().rotateX(-(float) (Math.PI/2)))
	};*/
	/**
	 * @param rendererDispatcherIn  
	 */
	public BeverageRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
	}



	@Override
	public BeverageRenderState createRenderState() {
		return new BeverageRenderState();
	}


	@Override
	public void submit(BeverageRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
		if(state.sprite!=null) {
			if(state.model!=null) {
				float u0=state.sprite.getU0();
				float v0=state.sprite.getV0();
				float u1=state.sprite.getU1();
				float v1=state.sprite.getV1();
				int light=state.lightCoords;
				int overlay=OverlayTexture.NO_OVERLAY;
				int color=state.clr;
				submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.translucentMovingBlock(), (pose,buffer)->{
					for(BakedQuad quads:state.model.get().getAll()) {
						buffer.addVertex(pose, quads.position0()).setColor(color).setUv(u0, v0).setOverlay(overlay).setLight(light);
						buffer.addVertex(pose, quads.position1()).setColor(color).setUv(u0, v1).setOverlay(overlay).setLight(light);
						buffer.addVertex(pose, quads.position2()).setColor(color).setUv(u1, v1).setOverlay(overlay).setLight(light);
						buffer.addVertex(pose, quads.position3()).setColor(color).setUv(u1, v0).setOverlay(overlay).setLight(light);
					}
				});
				
			}
			/*poseStack.pushPose();
			poseStack.translate(5/16f, 3/16f, 5/16f);
			for(Pair<Vec3, Quaternionf> p:rots) {
				poseStack.pushPose();
				poseStack.translate(p.getFirst().x, p.getFirst().y, p.getFirst().z);
				poseStack.mulPose(p.getSecond());
				FluidRenderHelper.submitColoredTexturedRect(submitNodeCollector, poseStack, state.sprite,0, 0, 3/8f, 3/8f, state.clr, state.lightCoords, OverlayTexture.NO_OVERLAY);
				poseStack.popPose();
			}
			poseStack.popPose();*/	
		}
	}


	@Override
	public void extractRenderState(BeverageBlockEntity blockEntity, BeverageRenderState state, float partialTicks, Vec3 cameraPosition, @Nullable CrumblingOverlay breakProgress) {
		BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
		state.clr=0xff3333aa;
		state.sprite=null;
		state.model=MODELS.get(blockEntity.getBlockState().getBlock());
		ItemResource item=blockEntity.getInternal().getResource(0);
		if(item.is(Items.POTION)) {
			FluidModel model=FluidRenderHelper.getFluidModel(new FluidStack(Fluids.WATER,1000));
			PotionContents comp=item.get(DataComponents.POTION_CONTENTS);
			if(comp!=null)
				state.clr=comp.getColor();
			state.sprite = model.stillMaterial().sprite();

		}else {
			FluidStack fs = FluidUtil.getFirstStackContained(blockEntity.getInternal().getResource(0).toStack());
			if(fs.isEmpty())return;
			FluidModel model=FluidRenderHelper.getFluidModel(fs);
			state.clr=FluidRenderHelper.getFluidColor(model, fs);
			state.sprite = model.stillMaterial().sprite();
		}
	}

}