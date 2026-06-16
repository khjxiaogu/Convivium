package com.khjxiaogu.convivium.client.renderer;

import org.joml.Quaternionf;

import com.teammoeg.caupona.client.util.DynamicBlockModelReference;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;

public class RotationRenderState extends BlockEntityRenderState{
	Quaternionf rotation;
	DynamicBlockModelReference rotor;
	public Quaternionf getRotation() {
		return rotation;
	}
	public DynamicBlockModelReference getRotor() {
		return rotor;
	}
}