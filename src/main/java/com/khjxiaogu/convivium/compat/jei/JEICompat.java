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

package com.khjxiaogu.convivium.compat.jei;

import java.util.ArrayList;

import com.khjxiaogu.convivium.CVBlocks;
import com.khjxiaogu.convivium.CVMain;
import com.khjxiaogu.convivium.client.gui.BasinScreen;
import com.khjxiaogu.convivium.client.gui.BeverageVendingScreen;
import com.khjxiaogu.convivium.client.gui.PamScreen;
import com.khjxiaogu.convivium.client.gui.WhiskScreen;
import com.khjxiaogu.convivium.compat.jei.category.BasinCategory;
import com.khjxiaogu.convivium.compat.jei.category.GrindingCategory;
import com.khjxiaogu.convivium.data.recipes.BasinRecipe;
import com.khjxiaogu.convivium.data.recipes.GrindingRecipe;
import com.teammoeg.caupona.compat.jei.GuiTankHandler;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public class JEICompat implements IModPlugin {
	@Override
	public ResourceLocation getPluginUid() {
		return ResourceLocation.fromNamespaceAndPath(CVMain.MODID, "jei_plugin");
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addRecipeCatalyst(new ItemStack(CVBlocks.pam.get()), GrindingCategory.TYPE);
		registration.addRecipeCatalyst(new ItemStack(CVBlocks.basin.get()), BasinCategory.TYPE);
		registration.addRecipeCatalyst(new ItemStack(CVBlocks.lead_basin.get()), BasinCategory.TYPE);
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		registration.addRecipes(GrindingCategory.TYPE,new ArrayList<>(GrindingRecipe.recipes));
		registration.addRecipes(BasinCategory.TYPE,new ArrayList<>(BasinRecipe.recipes));
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
		registration.addRecipeCategories(new GrindingCategory(guiHelper));
		registration.addRecipeCategories(new BasinCategory(guiHelper));
	}

	@Override
	public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
	}

	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registry) {
		IIngredientManager manager=registry.getJeiHelpers().getIngredientManager();
		registry.addRecipeClickArea(PamScreen.class, 108, 23,22, 15, GrindingCategory.TYPE);
		registry.addRecipeClickArea(BasinScreen.class, 82, 19,16, 43, BasinCategory.TYPE);

		registry.addGuiContainerHandler(PamScreen.class, new GuiTankHandler<PamScreen>(manager)
			.addTank(42, 19, 16, 37, t->t.getBlockEntity().tankin.getFluid())
			.addTank(133, 34, 16, 37, t->t.getBlockEntity().tankout.getFluid()));
		registry.addGuiContainerHandler(BasinScreen.class, new GuiTankHandler<BasinScreen>(manager)
			.addTank(62, 24, 16, 37, t->t.getBlockEntity().tankin.getFluid()));
		registry.addGuiContainerHandler(WhiskScreen.class, new GuiTankHandler<WhiskScreen>(manager)
			.addTank(132, 45, 16, 46, t->t.getBlockEntity().tank.getFluid()));
		registry.addGuiContainerHandler(BeverageVendingScreen.class, new GuiTankHandler<BeverageVendingScreen>(manager)
			.addTank(123, 25, 32, 46, t->t.getBlockEntity().tank.getFluid()));
		
	}

	@Override
	public void registerIngredients(IModIngredientRegistration registration) {

	}

}
