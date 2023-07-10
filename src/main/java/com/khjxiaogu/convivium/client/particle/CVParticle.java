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

package com.khjxiaogu.convivium.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.util.Mth;

public class CVParticle extends TextureSheetParticle {
	protected float originalScale = 1.3F;

	protected CVParticle(ClientLevel world, double x, double y, double z) {
		super(world, x, y, z);
	}

	public CVParticle(ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ) {
		super(world, x, y, z, motionX, motionY, motionZ);
	}

	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public void render(VertexConsumer worldRendererIn, Camera entityIn, float pt) {
		float age = (this.age + pt) / lifetime * 32.0F;

		age = Mth.clamp(age, 0.0F, 1.0F);

		this.quadSize = originalScale * age;
		super.render(worldRendererIn, entityIn, pt);
	}

	public void tick() {
		this.xo = x;
		this.yo = y;
		this.zo = z;
		if (age >= lifetime)
			remove();
		this.age++;

		this.yd -= 0.04D * gravity;
		move(xd, yd, zd);

		if (y == yo) {
			this.xd *= 1.1D;
			this.zd *= 1.1D;
		}

		this.xd *= 0.96D;
		this.yd *= 0.96D;
		this.zd *= 0.96D;

		if (onGround) {
			this.xd *= 0.67D;
			this.zd *= 0.67D;
		}
	}

}
