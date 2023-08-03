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

package com.khjxiaogu.convivium.compat.jei.category;

import java.util.Arrays;
import java.util.List;

import com.khjxiaogu.convivium.CVBlocks;
import com.khjxiaogu.convivium.CVMain;
import com.khjxiaogu.convivium.data.recipes.BasinRecipe;
import com.teammoeg.caupona.compat.jei.category.BaseCallback;
import com.teammoeg.caupona.util.Utils;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class BasinCategory implements IRecipeCategory<BasinRecipe> {
	public static RecipeType<BasinRecipe> TYPE=RecipeType.create(CVMain.MODID, "basin",BasinRecipe.class);
	private IDrawable BACKGROUND;
	private IDrawable ICON;

	public BasinCategory(IGuiHelper guiHelper) {
		this.ICON = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(CVBlocks.lead_basin.get()));
		ResourceLocation guiMain = new ResourceLocation(CVMain.MODID, "textures/gui/jei/basin.png");
		this.BACKGROUND = guiHelper.createDrawable(guiMain, 0, 0, 127, 63);
	}


	public Component getTitle() {
		return Utils.translate("gui.jei.category." + CVMain.MODID + "."+TYPE.getUid().getPath()+".title");
	}

	@SuppressWarnings("resource")
	@Override
	public void draw(BasinRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics stack, double mouseX,
			double mouseY) {
		String burnTime = String.valueOf(recipe.processTime / 20f) + "s";
		stack.drawString(Minecraft.getInstance().font,  burnTime, 100, 55, 0xFFFFFF);
	}

	@Override
	public IDrawable getBackground() {
		return BACKGROUND;
	}

	@Override
	public IDrawable getIcon() {
		return ICON;
	}

	private static RecipeIngredientRole type(BasinRecipe ps) {
		return ps.inputCount == 0 ? RecipeIngredientRole.CATALYST : RecipeIngredientRole.INPUT;
	}

	private static class CatalistCallback implements IRecipeSlotTooltipCallback {
		int cnt;

		public CatalistCallback(int cnt) {
			super();
			this.cnt = cnt;
		}

		@Override
		public void onTooltip(IRecipeSlotView recipeSlotView, List<Component> tooltip) {
			if (cnt == 0)
				tooltip.add(Utils.translate("gui.jei.category.caupona.catalyst"));
		}

	};

	private static CatalistCallback cb(BasinRecipe ps) {
		return new CatalistCallback(ps.inputCount);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, BasinRecipe recipe, IFocusGroup focuses) {
		if (recipe.item != null) {
			builder.addSlot(type(recipe), 49, 43)
			.addIngredients(VanillaTypes.ITEM_STACK,Arrays.asList( recipe.item.getItems()))
			.addTooltipCallback(cb(recipe));
		}
		builder.addSlot(RecipeIngredientRole.INPUT, 23, 14)
		.addIngredient(ForgeTypes.FLUID_STACK,new FluidStack(recipe.in, recipe.in.getAmount()))
		.setFluidRenderer(1000, false, 16, 37)
		.addTooltipCallback(new BaseCallback(recipe.base, recipe.density));
		if(recipe.requireBasin) {
			builder.addSlot(RecipeIngredientRole.CATALYST , 3, 22)
			.addIngredients(VanillaTypes.ITEM_STACK,Arrays.asList(new ItemStack(CVBlocks.lead_basin.get())));
		}
		
		for(int i=0;i<4;i++) {
			if(i>=recipe.output.size())break;
			builder.addSlot(RecipeIngredientRole.OUTPUT, 18*(i%2)+73, i>1?33:15).addIngredient(VanillaTypes.ITEM_STACK, recipe.output.get(i));
		}

	}


	@Override
	public RecipeType<BasinRecipe> getRecipeType() {
		return TYPE;
	}

}
