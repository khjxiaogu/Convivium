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

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class FountainSplashParticle extends TextureSheetParticle {
   private final SpriteSet sprites;

   FountainSplashParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet pSprites) {
      super(pLevel, pX, pY, pZ);
      this.sprites = pSprites;
      this.setSize(0.01F, 0.01F);
      this.lifetime = 20;
      this.setSpriteFromAge(pSprites);
      this.gravity = 1.0F;
      this.xd = pXSpeed;
      this.yd = pYSpeed;
      this.zd = pZSpeed;
      this.pickSprite(sprites);
      this.setColor(0.3F, 0.5F, 1.0F);
   }
   public FountainSplashParticle withColor(ColorParticleOption option) {
	   this.setColor(option.getRed(), option.getGreen(), option.getBlue());
	   this.setAlpha(option.getAlpha());
	   return this;
   }
   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }
   private boolean stoppedByCollision;
   private boolean isSplashed;
   public void move(double x, double y, double z) {
       if (!this.stoppedByCollision) {
    	 //  System.out.println("("+x+","+y+","+z+")");
           double d0 = x;
           double d1 = y;
           double d2 = z;

           if (this.hasPhysics
               && (x != 0.0 || y != 0.0 || z != 0.0)
               && x * x + y * y + z * z < 100) {
        	   
               List<Entity> collides=this.level.getEntities(null, getBoundingBox().expandTowards(x, y, z));
               if(collides.size()>0) {
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

          /* if (Math.abs(d1) >= 1.0E-5F && Math.abs(y) < 1.0E-5F) {
               this.stoppedByCollision = true;
           }*/

           //this.onGround = d1 != y && d1 < 0.0;
           if(d1!=y||d0 != x||d2!=z) {
        	   if(!isSplashed) {
        		   isSplashed=true;
        		   this.xd=Math.random()*2*0.1-0.1;
        		   this.yd=0.2;
        		   this.age=this.lifetime-10;
        		   this.zd=Math.random()*2*0.1-0.1;
        		   //System.out.println("collided and change to ("+this.xd+","+this.yd+","+this.zd+")");
        		   stoppedByCollision=false;
        	   }
           }
       }
   }
   public static class Provider implements ParticleProvider<ColorParticleOption> {
      private final SpriteSet sprites;

      public Provider(SpriteSet pSprites) {
         this.sprites = pSprites;
      }

      public Particle createParticle(ColorParticleOption pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
    	  return new FountainSplashParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, this.sprites).withColor(pType);
      }
   }
   
}