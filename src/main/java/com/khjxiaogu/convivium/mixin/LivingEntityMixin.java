/*
 * Copyright (c) 2022 TeamMoeg
 *
 * This file is part of Caupona.
 *
 * Caupona is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Caupona is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * Specially, we allow this software to be used alongside with closed source software Minecraft(R) and Forge or other modloader.
 * Any mods or plugins can also use apis provided by forge or com.teammoeg.caupona.api without using GPL or open source.
 *
 * You should have received a copy of the GNU General Public License
 * along with Caupona. If not, see <https://www.gnu.org/licenses/>.
 */

package com.khjxiaogu.convivium.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.khjxiaogu.convivium.CVMobEffects;
import com.teammoeg.caupona.CPMobEffects;

import net.minecraft.world.entity.LivingEntity;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

	public LivingEntityMixin() {
	}
	@ModifyVariable(at = @At("HEAD"),method="hurt",index=2,argsOnly=true,require=1)
	public float setAmount(float amount) {
		if(getThis().hasEffect(CPMobEffects.HYPERACTIVE.get()))
			return amount*2;
		return amount;
	}
	@Inject(method = "getJumpPower", at = @At("RETURN"), cancellable = true)
	protected void onGetJump(CallbackInfoReturnable<Float> cir) {
		if(getThis().hasEffect(CVMobEffects.READICATION.get()))
	    cir.setReturnValue(cir.getReturnValue()*Math.max(0,(4-getThis().getEffect(CVMobEffects.READICATION.get()).getAmplifier()))*0.2f);
	}
	private LivingEntity getThis() {
		return (LivingEntity)(Object)this;
	}
}
