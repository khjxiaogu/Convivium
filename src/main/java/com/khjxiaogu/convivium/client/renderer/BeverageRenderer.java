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

import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.khjxiaogu.convivium.blocks.foods.BeverageBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.teammoeg.caupona.client.util.GuiUtils;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;

public class BeverageRenderer implements BlockEntityRenderer<BeverageBlockEntity> {
	public static final Pair<Vec3,Quaternionf>[] rots=new Pair[] {
		Pair.of(Vec3.ZERO.add(0,0,0), new Quaternionf()),//side
		Pair.of(Vec3.ZERO.add(0,0,6/16f), new Quaternionf(new AxisAngle4f((float) (Math.PI/2),0,1,0))),//side
		Pair.of(Vec3.ZERO.add(6/16f,0,6/16f), new Quaternionf(new AxisAngle4f((float) (Math.PI),0,1,0))),//side
		Pair.of(Vec3.ZERO.add(6/16f,0,0), new Quaternionf(new AxisAngle4f(-(float) (Math.PI/2),0,1,0))), //side
		Pair.of(Vec3.ZERO.add(0, 6/16f, 0), new Quaternionf(new AxisAngle4f((float) (Math.PI/2),1,0,0))),
		Pair.of(Vec3.ZERO.add(0,0,6/16f), new Quaternionf(new AxisAngle4f(-(float) (Math.PI/2),1,0,0)))
	};
	/**
	 * @param rendererDispatcherIn  
	 */
	public BeverageRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
	}


	@Override
	public void render(BeverageBlockEntity pBlockEntity, float pPartialTick, PoseStack matrixStack,
			MultiBufferSource pBufferSource, int combinedLightIn, int combinedOverlayIn) {
		TextureAtlasSprite sprite;
		Vector3f clr;
		if(pBlockEntity.internal.is(Items.POTION)) {
			IClientFluidTypeExtensions attr=IClientFluidTypeExtensions.of(Fluids.WATER);
			sprite = Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS)
					.getSprite(attr.getStillTexture());
			clr=clr(PotionUtils.getColor(pBlockEntity.internal));
		}else {
			FluidStack fs = Utils.extractFluid(pBlockEntity.internal);

			if(fs.isEmpty())return;
			IClientFluidTypeExtensions attr=IClientFluidTypeExtensions.of(fs.getFluid());
			sprite = Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS)
					.getSprite(attr.getStillTexture(fs));
			clr =clr( attr.getTintColor(fs));
		}
		
		matrixStack.pushPose();
		matrixStack.translate(5/16f, 3/16f, 5/16f);
		
		VertexConsumer builder = pBufferSource.getBuffer(RenderType.translucent());
		
		
		float alp = 1f;
		for(Pair<Vec3, Quaternionf> p:rots) {
			matrixStack.pushPose();
			matrixStack.translate(p.getFirst().x, p.getFirst().y, p.getFirst().z);
			matrixStack.mulPose(p.getSecond());
			
			GuiUtils.drawTexturedColoredRect(builder, matrixStack, 0, 0, 3/8f, 3/8f, clr.x(), clr.y(),
					clr.z(), alp, sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1(), combinedLightIn,
					combinedOverlayIn);
			matrixStack.popPose();
		}
		matrixStack.popPose();	
		
	}





	private static Vector3f clr(int col) {
		return new Vector3f((col >> 16 & 255) / 255.0f, (col >> 8 & 255) / 255.0f, (col & 255) / 255.0f);
	}

}