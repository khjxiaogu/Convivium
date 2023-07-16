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

package com.khjxiaogu.convivium.client.renderer;

import java.util.HashMap;
import java.util.Map;

import org.joml.Quaternionf;

import com.google.common.collect.ImmutableSet;
import com.khjxiaogu.convivium.CVBlocks;
import com.khjxiaogu.convivium.blocks.platter.GlobalConfig;
import com.khjxiaogu.convivium.blocks.platter.PlatterBlockEntity;
import com.khjxiaogu.convivium.blocks.platter.SlotConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import com.teammoeg.caupona.client.util.DynamicBlockModelReference;
import com.teammoeg.caupona.client.util.ModelUtils;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class FruitPlatterRenderer implements BlockEntityRenderer<PlatterBlockEntity> {
	public static final Map<Item,DynamicBlockModelReference> models=new HashMap<>();
	public static final Map<Item,DynamicBlockModelReference[]> cmodels=new HashMap<>();
	private final ItemRenderer render;
	/**
	 * @param rendererDispatcherIn
	 */
	public FruitPlatterRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
		render=rendererDispatcherIn.getItemRenderer();
	}


	@SuppressWarnings({ "resource" })
	@Override
	public void render(PlatterBlockEntity blockEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer,
			int combinedLightIn, int combinedOverlayIn) {
		if (!blockEntity.getLevel().hasChunkAt(blockEntity.getBlockPos()))
			return;
		BlockState state = blockEntity.getBlockState();
		if (!state.is(CVBlocks.platter.get()))
			return;
		//System.out.println("render");
		Item crn=null;
		int icnt=0;
		boolean canFull=blockEntity.config==GlobalConfig.PILED;
		DynamicBlockModelReference[] model=new DynamicBlockModelReference[4];
		for(int i=0;i<4;i++) {
			ItemStack is=blockEntity.storage.getStackInSlot(i);
			
			if(!is.isEmpty()) {
				Item it=is.getItem();
				if(canFull) {
					if(crn==null) {
						crn=is.getItem();
					}else if(crn!=it) {
						crn=null;
						canFull=false;
					}
					icnt++;
				}
				if(blockEntity.config!=GlobalConfig.SEPERATE||blockEntity.slotconfig[i]==SlotConfig.MODEL)
					model[i]=models.get(it);
			}
		}
		if(crn!=null) {
			DynamicBlockModelReference[] rss=cmodels.get(crn);
			if(rss!=null) {
				ModelUtils.renderModel(rss[icnt-1], buffer.getBuffer(RenderType.cutout()),matrixStack, combinedLightIn, combinedOverlayIn);
				return;
			}
		}
		
		for(int i=1;i<=4;i++) {
			ItemStack is=blockEntity.storage.getStackInSlot(i-1);
			if(!is.isEmpty()) {
				if(model[i-1]!=null) {
				ModelUtils.renderModelGroups(model[i-1], buffer.getBuffer(RenderType.cutout()),ImmutableSet.of("FruitUnit"+i),
						matrixStack, combinedLightIn, combinedOverlayIn);
				}else {
					float rx=0;
					float ry=0;
					float rz=0;
					switch(i) {
					case 1:rx=+(float) (30/180f*Math.PI);rz=+(float) (15/180f*Math.PI);ry=+(float) (30/180f*Math.PI);break;
					case 2:rx=+(float) (30/180f*Math.PI);rz=+(float) (15/180f*Math.PI);ry=-(float) (30/180f*Math.PI);break;
					case 3:rx=-(float) (30/180f*Math.PI);rz=-(float) (15/180f*Math.PI);ry=-(float) (30/180f*Math.PI);break;
					case 4:rx=-(float) (30/180f*Math.PI);rz=-(float) (15/180f*Math.PI);ry=+(float) (30/180f*Math.PI);break;
					}
					matrixStack.pushPose();
					//matrixStack.translate(, combinedLightIn, combinedOverlayIn);
					matrixStack.translate((((i&1)==0)?11:5)/16f,3/16f,(i<=2?5:11)/16f);
					matrixStack.mulPose(new Quaternionf().rotateXYZ(rx,ry,rz));
					
					render.render(is, ItemDisplayContext.GROUND, false,
							matrixStack, buffer,combinedLightIn, OverlayTexture.NO_OVERLAY,render.getModel(is, blockEntity.getLevel(),null,(int) blockEntity.getBlockPos().asLong()));
					matrixStack.popPose();
				}
			}
		}
		
	}

}