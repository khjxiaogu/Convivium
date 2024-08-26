package com.khjxiaogu.convivium;

import com.khjxiaogu.convivium.effect.CVMobEffect;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CVMobEffects {
	public static final DeferredRegister<MobEffect> EFFECTS=DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, CVMain.MODID);
	public static final DeferredHolder<MobEffect, CVMobEffect> DELICACY=EFFECTS.register("delicacy",()->new CVMobEffect(MobEffectCategory.HARMFUL,0x91d4ca));//
	public static final DeferredHolder<MobEffect, CVMobEffect> IGNITABILITY=EFFECTS.register("ignitability",()->new CVMobEffect(MobEffectCategory.HARMFUL,0xea8765));//
	public static final DeferredHolder<MobEffect, MobEffect> FATIGUE=EFFECTS.register("maritime_fatigue",()->new CVMobEffect(MobEffectCategory.HARMFUL,0x6e6988).addAttributeModifier(NeoForgeMod.SWIM_SPEED,ResourceLocation.fromNamespaceAndPath(CVMain.MODID, "maritime_fatigue"), -0.15f,Operation.ADD_MULTIPLIED_TOTAL));
	public static final DeferredHolder<MobEffect, MobEffect> PLUMMET=EFFECTS.register("plummet",()->new CVMobEffect(MobEffectCategory.HARMFUL,0x18322b).addAttributeModifier(Attributes.GRAVITY,ResourceLocation.fromNamespaceAndPath(CVMain.MODID, "plummet"), 0.2, Operation.ADD_VALUE));
	public static final DeferredHolder<MobEffect, CVMobEffect> RADICATION=EFFECTS.register("radication",()->new CVMobEffect(MobEffectCategory.HARMFUL,0x9a842c));//
	public static final DeferredHolder<MobEffect, MobEffect> THRUSTED=EFFECTS.register("thrusted",()->new CVMobEffect(MobEffectCategory.BENEFICIAL,0x7bbdfb) .addAttributeModifier(NeoForgeMod.SWIM_SPEED,ResourceLocation.fromNamespaceAndPath(CVMain.MODID, "thrusted"), 0.2f,Operation.ADD_MULTIPLIED_TOTAL));
	public static final DeferredHolder<MobEffect, MobEffect> TOSS=EFFECTS.register("toss_power",()->new CVMobEffect(MobEffectCategory.BENEFICIAL,0x9e6945).addAttributeModifier(Attributes.BLOCK_INTERACTION_RANGE, ResourceLocation.fromNamespaceAndPath(CVMain.MODID, "toss"), 0.5, Operation.ADD_VALUE));
}
