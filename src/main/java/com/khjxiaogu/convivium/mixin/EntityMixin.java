package com.khjxiaogu.convivium.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.khjxiaogu.convivium.CVMobEffects;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

@Mixin(Entity.class)
public class EntityMixin {
	@ModifyVariable(at = @At("HEAD"),method="setRemainingFireTicks",index=1,argsOnly=true,require=1)
	public int modifyFireTick(int pRemainingFireTicks) {
		if(getThis() instanceof LivingEntity le)
			if(le.hasEffect(CVMobEffects.IGNITABILITY.get())) {
				return pRemainingFireTicks*(le.getEffect(CVMobEffects.IGNITABILITY.get()).getAmplifier()+2);
			}
		return pRemainingFireTicks;
	}
	private Entity getThis() {
		return (Entity)(Object)this;
	}
}
