package com.khjxiaogu.convivium.client.renderer;

import org.joml.Quaternionf;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class WhiskRenderState extends BlockEntityRenderState {
	TextureAtlasSprite spite1;
	int color1;
	TextureAtlasSprite spite2;
	int color2;
	ItemStackRenderState irs;
	Quaternionf rotation;
	float progress;
}
