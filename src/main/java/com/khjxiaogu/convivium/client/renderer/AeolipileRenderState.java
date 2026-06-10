package com.khjxiaogu.convivium.client.renderer;

import org.joml.Quaternionf;

import com.teammoeg.caupona.client.util.DynamicBlockModelReference;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;

public class AeolipileRenderState extends BlockEntityRenderState{
	Quaternionf rotation;
	DynamicBlockModelReference rotor;
	public boolean active;
	public Quaternionf getRotation() {
		return rotation;
	}
	public DynamicBlockModelReference getRotor() {
		return rotor;
	}
}