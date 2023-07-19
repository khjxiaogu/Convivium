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

import com.khjxiaogu.convivium.CVTags;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.teammoeg.caupona.client.util.GuiUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;

public class AqueductRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {
	/**
	 * @param rendererDispatcherIn  
	 */
	public AqueductRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
	}
	private FluidStack water=new FluidStack(Fluids.WATER,1000);
	@SuppressWarnings({ "deprecation", "resource" })
	@Override
	public void render(T blockEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer,
			int combinedLightIn, int combinedOverlayIn) {
		if (!blockEntity.getLevel().hasChunkAt(blockEntity.getBlockPos()))
			return;
		BlockState state = blockEntity.getBlockState();
		if(!state.is(CVTags.Blocks.aqueduct))
			return;
		matrixStack.pushPose();
		matrixStack.translate(0, 15/16f, 0);
		matrixStack.mulPose(GuiUtils.rotate90);
		VertexConsumer builder = buffer.getBuffer(RenderType.translucent());
		IClientFluidTypeExtensions attr0 = IClientFluidTypeExtensions.of(Fluids.WATER);
		TextureAtlas atlas = Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS);
		TextureAtlasSprite sprite = atlas.getSprite(attr0.getStillTexture(water));
		int col = attr0.getTintColor(water);
		float alp = 1f;
		GuiUtils.drawTexturedColoredRect(builder, matrixStack, 0, 0, 1, 1,(col >> 16 & 255) / 255.0f, (col >> 8 & 255) / 255.0f, (col & 255) / 255.0f, alp, sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1(), combinedLightIn,combinedOverlayIn);
		matrixStack.popPose();
	}
}