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

package com.khjxiaogu.convivium.client;

import java.util.List;

import com.mojang.datafixers.util.Either;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

public class FountainSplashParticle extends TextureSheetParticle {
	private final SpriteSet sprites;
	FountainSplashParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet spriteSet, Either<ItemStack, FluidStack> stacks) {
		super(pLevel, pX, pY, pZ);
		this.setSize(0.01F, 0.01F);
		this.sprites = spriteSet;
		this.lifetime = 20;
		stacks.ifLeft(stack -> {
			//System.out.println("left");
			this.setSpriteFromAge(spriteSet);
			this.pickSprite(sprites);
			int tint = 0xfffffff;
			for (int i = 0; i <= 3; i++) {
				int tclr = Minecraft.getInstance().getItemColors().getColor(stack, i);
				if (tint == -1)
					continue;
				tint = tclr;
				break;
			}
			
			this.setColor(FastColor.ARGB32.red(tint) / 255f, FastColor.ARGB32.green(tint) / 255f, FastColor.ARGB32.blue(tint) / 255f);
			float alpha = FastColor.ARGB32.alpha(tint) / 255F;
			this.setAlpha(alpha == 0 ? 1 : alpha);
		});
		this.setColor(0.3F, 0.5F, 1.0F);
		stacks.ifRight(stack -> {
			//System.out.println("right");
			IClientFluidTypeExtensions attr = IClientFluidTypeExtensions.of(stack.getFluid());
			TextureAtlasSprite sprite = Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS)
				.getSprite(attr.getStillTexture(stack));
			int tint = attr.getTintColor(stack);
			this.setColor(FastColor.ARGB32.red(tint) / 255f, FastColor.ARGB32.green(tint) / 255f, FastColor.ARGB32.blue(tint) / 255f);
			float alpha = FastColor.ARGB32.alpha(tint) / 255F;
			this.setAlpha(alpha == 0 ? 1 : alpha);
			this.setSprite(sprite);
			v1=(v1-v0)/8+v0;
			u1=(u1-u0)/8+u0;
			this.quadSize/=8;

		});
		this.gravity = 1.0F;
		this.xd = pXSpeed;
		this.yd = pYSpeed;
		this.zd = pZSpeed;

	}

	public FountainSplashParticle withColor(ColorParticleOption option) {
		this.setColor(option.getRed(), option.getGreen(), option.getBlue());
		this.setAlpha(option.getAlpha());
		return this;
	}
	float u0;
	float u1;
	float v0;
	float v1;
	
    @Override
	protected void setSprite(TextureAtlasSprite sprite) {
		super.setSprite(sprite);
		u0=sprite.getU0();
		u1=sprite.getU1();
		v0=sprite.getV0();
		v1=sprite.getV1();
	}

	@Override
	protected float getU0() {
		return u0;
	}

	@Override
	protected float getU1() {
		return u1;
	}

	@Override
	protected float getV0() {
		return v0;
	}

	@Override
	protected float getV1() {
		return v1;
	}

	@Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.TERRAIN_SHEET;
    }

	private boolean stoppedByCollision;
	private boolean isSplashed;

	public void move(double x, double y, double z) {
		if (!this.stoppedByCollision) {
			// System.out.println("("+x+","+y+","+z+")");
			double d0 = x;
			double d1 = y;
			double d2 = z;

			if (this.hasPhysics
				&& (x != 0.0 || y != 0.0 || z != 0.0)
				&& x * x + y * y + z * z < 100) {

				List<Entity> collides = this.level.getEntities(null, getBoundingBox().expandTowards(x, y, z));
				if (collides.size() > 0) {
					this.remove();
				}
				Vec3 vec3 = Entity.collideBoundingBox(null, new Vec3(x, y, z), this.getBoundingBox(), this.level, List.of());
				x = vec3.x;
				y = vec3.y;
				z = vec3.z;

			}

			if (x != 0.0 || y != 0.0 || z != 0.0) {
				this.setBoundingBox(this.getBoundingBox().move(x, y, z));
				this.setLocationFromBoundingbox();
			}

			/*
			 * if (Math.abs(d1) >= 1.0E-5F && Math.abs(y) < 1.0E-5F) {
			 * this.stoppedByCollision = true; }
			 */

			// this.onGround = d1 != y && d1 < 0.0;
			if (d1 != y || d0 != x || d2 != z) {
				if (!isSplashed) {
					isSplashed = true;
					this.xd = Math.random() * 2 * 0.1 - 0.1;
					this.yd = 0.2;
					this.age = this.lifetime - 10;
					this.zd = Math.random() * 2 * 0.1 - 0.1;
					// System.out.println("collided and change to
					// ("+this.xd+","+this.yd+","+this.zd+")");
					stoppedByCollision = false;
				}
			}
		}
	}

	public static class Provider implements ParticleProvider<InputParticleOption> {
		private final SpriteSet sprites;

		public Provider(SpriteSet pSprites) {
			this.sprites = pSprites;
		}

		public Particle createParticle(InputParticleOption pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
			return new FountainSplashParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, this.sprites, pType.getStack());
		}
	}

}