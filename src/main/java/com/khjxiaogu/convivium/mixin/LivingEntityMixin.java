/*
 * Copyright (c) 2024 IEEM Trivium Society/khjxiaogu
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

package com.khjxiaogu.convivium.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.khjxiaogu.convivium.CVMobEffects;

import net.minecraft.world.entity.LivingEntity;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

	public LivingEntityMixin() {
	}
	@ModifyVariable(at = @At("HEAD"),method="hurt",index=2,argsOnly=true,require=1)
	public float setAmount(float amount) {
		if(getThis().hasEffect(CVMobEffects.DELICACY))
			return amount*(0.2f*(1+getThis().getEffect(CVMobEffects.DELICACY).getAmplifier()));
		return amount;
	}
	@Inject(method = "getJumpPower", at = @At("RETURN"), cancellable = true)
	protected void onGetJump(CallbackInfoReturnable<Float> cir) {
		if(getThis().hasEffect(CVMobEffects.RADICATION))
	    cir.setReturnValue(cir.getReturnValue()*Math.max(0,(4-getThis().getEffect(CVMobEffects.RADICATION).getAmplifier()))*0.2f);
	}
	private LivingEntity getThis() {
		return (LivingEntity)(Object)this;
	}
}
