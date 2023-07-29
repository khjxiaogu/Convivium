package com.khjxiaogu.convivium;

import com.khjxiaogu.convivium.effect.CVMobEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CVMobEffects {
	public static final DeferredRegister<MobEffect> EFFECTS=DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, CVMain.MODID);
	public static final RegistryObject<MobEffect> DELICACY=EFFECTS.register("delicacy",()->new CVMobEffect(MobEffectCategory.BENEFICIAL,0xd05c6f));//
	public static final RegistryObject<MobEffect> IGNITABILITY=EFFECTS.register("ignitability",()->new CVMobEffect(MobEffectCategory.BENEFICIAL,0xd05c6f));//
	public static final RegistryObject<MobEffect> FATIGUE=EFFECTS.register("maritime_fatigue",()->new CVMobEffect(MobEffectCategory.BENEFICIAL,0xd05c6f).addAttributeModifier(ForgeMod.SWIM_SPEED.get(),"2c332e01-3df3-4662-87de-e06a7a57957e", -0.15f,Operation.MULTIPLY_TOTAL));
	public static final RegistryObject<MobEffect> PLUMMET=EFFECTS.register("plummet",()->new CVMobEffect(MobEffectCategory.BENEFICIAL,0xd05c6f).addAttributeModifier(ForgeMod.ENTITY_GRAVITY.get(),"a2db5634-2732-4eff-8487-30d419354b79", 0.2, Operation.ADDITION));
	public static final RegistryObject<MobEffect> RADICATION=EFFECTS.register("radication",()->new CVMobEffect(MobEffectCategory.BENEFICIAL,0xd05c6f));//
	public static final RegistryObject<MobEffect> THRUSTED=EFFECTS.register("thrusted",()->new CVMobEffect(MobEffectCategory.BENEFICIAL,0xd05c6f) .addAttributeModifier(ForgeMod.SWIM_SPEED.get(),"b44e0886-9400-449b-9dd7-c412adc498c9", 0.2f,Operation.MULTIPLY_TOTAL));
	public static final RegistryObject<MobEffect> TOSS=EFFECTS.register("toss_power",()->new CVMobEffect(MobEffectCategory.BENEFICIAL,0xd05c6f).addAttributeModifier(ForgeMod.BLOCK_REACH.get(), "83270cf4-1462-49ad-80e5-5501054f684e", 0.5, Operation.ADDITION));
}
