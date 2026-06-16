package com.khjxiaogu.convivium.client.renderer;

import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class PamRenderState extends RotationRenderState {
	TextureAtlasSprite spite;
	int color;
	ItemStackRenderState[] stacks=new ItemStackRenderState[6];
}
