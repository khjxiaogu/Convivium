package com.khjxiaogu.convivium.client.renderer;

import com.khjxiaogu.convivium.blocks.wolf_fountain.WolfFountainProjectile;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class WolfFountainProjectileRenderer extends EntityRenderer<WolfFountainProjectile> {

	public WolfFountainProjectileRenderer(Context context) {
		super(context);
	}

	@Override
	public ResourceLocation getTextureLocation(WolfFountainProjectile entity) {
		return null;
	}


}
