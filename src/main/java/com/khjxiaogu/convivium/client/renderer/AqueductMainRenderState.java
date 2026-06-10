package com.khjxiaogu.convivium.client.renderer;

import org.joml.Quaternionf;

import com.teammoeg.caupona.client.util.DynamicBlockModelReference;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class AqueductMainRenderState extends BlockEntityRenderState{
	Quaternionf rotation;
	Quaternionf hrotation;
	DynamicBlockModelReference rotor;
	boolean active;
	boolean shouldApart;
	TextureAtlasSprite spite;
	int color;
	public Quaternionf getRotation() {
		return rotation;
	}
	public DynamicBlockModelReference getRotor() {
		return rotor;
	}
}