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
import com.khjxiaogu.convivium.compat.jei.LoreCallback;
import com.khjxiaogu.convivium.data.recipes.GrindingRecipe;
import com.teammoeg.caupona.compat.jei.category.BaseCallback;
import com.teammoeg.caupona.util.SizedOrCatalystIngredient;
import com.teammoeg.caupona.util.Utils;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotRichTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

public class GrindingCategory implements IRecipeCategory<RecipeHolder<GrindingRecipe>> {
	@SuppressWarnings("rawtypes")
	public static RecipeType<RecipeHolder> TYPE=RecipeType.create(CVMain.MODID, "grinding",RecipeHolder.class);
	private IDrawable BACKGROUND;
	private IDrawable ICON;

	public GrindingCategory(IGuiHelper guiHelper) {
		this.ICON = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(CVBlocks.pam.get()));
		ResourceLocation guiMain = ResourceLocation.fromNamespaceAndPath(CVMain.MODID, "textures/gui/jei/pestle_and_mortar.png");
		this.BACKGROUND = guiHelper.createDrawable(guiMain, 0, 0, 127, 63);
	}


	public Component getTitle() {
		return Utils.translate("gui.jei.category." + CVMain.MODID + "."+TYPE.getUid().getPath()+".title");
	}

	@SuppressWarnings("resource")
	@Override
	public void draw(RecipeHolder<GrindingRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics stack, double mouseX,
			double mouseY) {
		String burnTime = String.valueOf(recipe.value().processTime / 20f) + "s";
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

	private static List<ItemStack> unpack(SizedOrCatalystIngredient sizedOrCatalystIngredient) {

		return Arrays.asList(sizedOrCatalystIngredient.getItems());
	}
	private static RecipeIngredientRole type(SizedOrCatalystIngredient sizedOrCatalystIngredient) {
		return sizedOrCatalystIngredient.count() == 0 ? RecipeIngredientRole.CATALYST : RecipeIngredientRole.INPUT;
	}

	private static class CatalistCallback implements IRecipeSlotRichTooltipCallback {
		int cnt;

		public CatalistCallback(int cnt) {
			super();
			this.cnt = cnt;
		}

		@Override
		public void onRichTooltip(IRecipeSlotView recipeSlotView, ITooltipBuilder tooltip) {
			if (cnt == 0)
				tooltip.add(Utils.translate("gui.jei.category.caupona.catalyst"));
		}

	};

	private static CatalistCallback cb(SizedOrCatalystIngredient sizedOrCatalystIngredient) {
		return new CatalistCallback(sizedOrCatalystIngredient.count());
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<GrindingRecipe> recipeh, IFocusGroup focuses) {
		GrindingRecipe recipe=recipeh.value();
		if (recipe.items.size() > 0) {
			builder.addSlot(type(recipe.items.get(0)), 7, 6)
					.addIngredients(VanillaTypes.ITEM_STACK, unpack(recipe.items.get(0)))
					.addRichTooltipCallback(cb(recipe.items.get(0)));
			if (recipe.items.size() > 1) {
				builder.addSlot(type(recipe.items.get(1)), 7, 24)
						.addIngredients(VanillaTypes.ITEM_STACK, unpack(recipe.items.get(1)))
						.addRichTooltipCallback(cb(recipe.items.get(1)));
				if (recipe.items.size() > 2) {
					builder.addSlot(type(recipe.items.get(2)), 7, 42)
							.addIngredients(VanillaTypes.ITEM_STACK, unpack(recipe.items.get(2)))
							.addRichTooltipCallback(cb(recipe.items.get(2)));
				}
			}
		}
		for(int i=0;i<3;i++) {
			if(i>=recipe.output.size())break;
			builder.addSlot(RecipeIngredientRole.OUTPUT, 103, 6+18*i).addIngredient(VanillaTypes.ITEM_STACK, recipe.output.get(i));
		}
		if (recipe.in!=null)
			builder.addSlot(RecipeIngredientRole.INPUT, 29, 14)
					.addIngredients(NeoForgeTypes.FLUID_STACK,Arrays.asList(recipe.in.getFluids()))
					.setFluidRenderer(1000, false, 16, 37)
					.addRichTooltipCallback(new BaseCallback(recipe.base, recipe.density))
					.addRichTooltipCallback(new LoreCallback());
		if(!recipe.out.isEmpty())
			builder.addSlot(RecipeIngredientRole.OUTPUT, 81, 14)
			.addIngredient(NeoForgeTypes.FLUID_STACK,recipe.out)
			.setFluidRenderer(1000, false, 16, 37);
	}


	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public RecipeType<RecipeHolder<GrindingRecipe>> getRecipeType() {
		return (RecipeType)TYPE;
	}

}
