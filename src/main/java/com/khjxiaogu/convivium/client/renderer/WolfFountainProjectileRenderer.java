package com.khjxiaogu.convivium.client.renderer;

import com.khjxiaogu.convivium.blocks.wolf_fountain.WolfFountainProjecttile;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class WolfFountainProjectileRenderer extends EntityRenderer<WolfFountainProjecttile> {

	public WolfFountainProjectileRenderer(Context context) {
		super(context);
	}

	@Override
	public ResourceLocation getTextureLocation(WolfFountainProjecttile entity) {
		return null;
	}

	@Override
	public boolean shouldRender(WolfFountainProjecttile livingEntity, Frustum camera, double camX, double camY, double camZ) {
		return false;
	}

}
