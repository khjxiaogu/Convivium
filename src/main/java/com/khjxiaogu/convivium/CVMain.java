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

package com.khjxiaogu.convivium;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.khjxiaogu.convivium.client.CVParticles;
import com.teammoeg.caupona.CPMain;
import com.teammoeg.caupona.util.TabType;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(CVMain.MODID)
public class CVMain {

	public static final String MODID = "convivium";
	public static final String MODNAME = "Convivium";
	public static final Logger logger = LogManager.getLogger(MODNAME);
	public static final String BOOK_NBT_TAG = CVMain.MODID + ":book_given";
	public static DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CVMain.MODID);
	public static DeferredHolder<CreativeModeTab, CreativeModeTab> main = TABS.register("aaa_caupona_9v8_main", () -> CreativeModeTab.builder().withTabsBefore(CPMain.main.getKey())
		.withTabsAfter(CPMain.foods.getKey()).icon(() -> new ItemStack(CVBlocks.aeolipile.get())).title(Utils.translate("itemGroup.convivium")).build());
	public static final TabType MAIN_TAB = new TabType(v -> main.getKey().equals(v));

	public static ResourceLocation rl(String path) {
		return ResourceLocation.fromNamespaceAndPath(MODID, path);
	}

	public CVMain(IEventBus mod) {
		NeoForgeMod.enableMilkFluid();
		// mod.addListener(this::enqueueIMC);
		CVBlockEntityTypes.REGISTER.register(mod);
		CVGui.CONTAINERS.register(mod);
		CVParticles.REGISTER.register(mod);
		CVFluids.FLUIDS.register(mod);
		CVFluids.FLUID_TYPES.register(mod);
		CVBlocks.BLOCKS.register(mod);
		CVItems.ITEMS.register(mod);
		CVMain.TABS.register(mod);
		CVRecipes.RECIPE_SERIALIZERS.register(mod);
		CVRecipes.RECIPE_TYPES.register(mod);
		CVMobEffects.EFFECTS.register(mod);
		CVConfig.register();
		CVComponents.REGISTRY.register(mod);
		

	}

	/*
	 * @SuppressWarnings("unused") public void enqueueIMC(InterModEnqueueEvent
	 * event) { // InterModComms.sendTo("treechop", "getTreeChopAPI", () ->
	 * (Consumer)TreechopCompat::new); }
	 */
}
