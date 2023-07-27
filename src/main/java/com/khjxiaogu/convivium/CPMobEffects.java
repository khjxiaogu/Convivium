package com.khjxiaogu.convivium;

import com.khjxiaogu.convivium.effect.CVMobEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CPMobEffects {
	public static final DeferredRegister<MobEffect> EFFECTS=DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, CVMain.MODID);
	public static final RegistryObject<MobEffect> DELICACY=EFFECTS.register("delicacy",()->new CVMobEffect(MobEffectCategory.BENEFICIAL,0xd05c6f));
	public static final RegistryObject<MobEffect> IGNITABILITY=EFFECTS.register("ignitability",()->new CVMobEffect(MobEffectCategory.BENEFICIAL,0xd05c6f));
	public static final RegistryObject<MobEffect> FATIGUE=EFFECTS.register("maritime_fatigue",()->new CVMobEffect(MobEffectCategory.BENEFICIAL,0xd05c6f));
	public static final RegistryObject<MobEffect> PLUMMET=EFFECTS.register("plummet",()->new CVMobEffect(MobEffectCategory.BENEFICIAL,0xd05c6f));
	public static final RegistryObject<MobEffect> READICATION=EFFECTS.register("radication",()->new CVMobEffect(MobEffectCategory.BENEFICIAL,0xd05c6f));
	public static final RegistryObject<MobEffect> THRUSTED=EFFECTS.register("thrusted",()->new CVMobEffect(MobEffectCategory.BENEFICIAL,0xd05c6f));
	public static final RegistryObject<MobEffect> TOSS=EFFECTS.register("toss_power",()->new CVMobEffect(MobEffectCategory.BENEFICIAL,0xd05c6f));
}
