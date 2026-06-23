package com.khjxiaogu.convivium.client.renderer;


import com.teammoeg.caupona.client.util.DynamicBlockModelReference;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class BeverageRenderState extends BlockEntityRenderState {
	TextureAtlasSprite sprite;
	int clr;
	DynamicBlockModelReference model;
}
