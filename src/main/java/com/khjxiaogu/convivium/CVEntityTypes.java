package com.khjxiaogu.convivium;

import com.khjxiaogu.convivium.blocks.wolf_fountain.WolfFountainProjectile;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CVEntityTypes {
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE,
			CVMain.MODID);
	public static final DeferredHolder<EntityType<?>, EntityType<WolfFountainProjectile>> WOLF_FOUNTAIN_DROP = ENTITY_TYPES.register("wolf_fountain_drop",
		t->EntityType.Builder.of(WolfFountainProjectile::new, MobCategory.MISC)
		.sized(0.25F, 0.25F).clientTrackingRange(1).updateInterval(1).noSummon().canSpawnFarFromPlayer().build(t.toString()));

	private CVEntityTypes() {
	}

}