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

package com.khjxiaogu.convivium.compat.jei.category;

import java.util.ArrayList;
import java.util.List;

import com.khjxiaogu.convivium.CVBlocks;
import com.khjxiaogu.convivium.CVMain;
import com.khjxiaogu.convivium.data.recipes.GrindingRecipe;
import com.mojang.datafixers.util.Pair;
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
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.items.ItemHandlerHelper;

public class GrindingCategory implements IRecipeCategory<GrindingRecipe> {
	public static RecipeType<GrindingRecipe> TYPE=RecipeType.create(CVMain.MODID, "grinding",GrindingRecipe.class);
	private IDrawable BACKGROUND;
	private IDrawable ICON;

	public GrindingCategory(IGuiHelper guiHelper) {
		this.ICON = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(CVBlocks.pam.get()));
		ResourceLocation guiMain = new ResourceLocation(CVMain.MODID, "textures/gui/jei/pestle_and_mortar.png");
		this.BACKGROUND = guiHelper.createDrawable(guiMain, 0, 0, 127, 63);
	}


	public Component getTitle() {
		return Utils.translate("gui.jei.category." + CVMain.MODID + "."+TYPE.getUid().getPath()+".title");
	}

	@SuppressWarnings("resource")
	@Override
	public void draw(GrindingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics stack, double mouseX,
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

	private static List<ItemStack> unpack(Pair<Ingredient, Integer> ps) {
		List<ItemStack> sl = new ArrayList<>();
		for (ItemStack is : ps.getFirst().getItems())
			sl.add(ItemHandlerHelper.copyStackWithSize(is, ps.getSecond() > 0 ? ps.getSecond() : 1));
		return sl;
	}
	private static RecipeIngredientRole type(Pair<Ingredient, Integer> ps) {
		return ps.getSecond() == 0 ? RecipeIngredientRole.CATALYST : RecipeIngredientRole.INPUT;
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

	private static CatalistCallback cb(Pair<Ingredient, Integer> ps) {
		return new CatalistCallback(ps.getSecond());
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, GrindingRecipe recipe, IFocusGroup focuses) {
		if (recipe.items.size() > 0) {
			builder.addSlot(type(recipe.items.get(0)), 4, 6)
					.addIngredients(VanillaTypes.ITEM_STACK, unpack(recipe.items.get(0)))
					.addTooltipCallback(cb(recipe.items.get(0)));
			if (recipe.items.size() > 1) {
				builder.addSlot(type(recipe.items.get(1)), 4, 24)
						.addIngredients(VanillaTypes.ITEM_STACK, unpack(recipe.items.get(1)))
						.addTooltipCallback(cb(recipe.items.get(1)));
				if (recipe.items.size() > 2) {
					builder.addSlot(type(recipe.items.get(2)), 4, 42)
							.addIngredients(VanillaTypes.ITEM_STACK, unpack(recipe.items.get(2)))
							.addTooltipCallback(cb(recipe.items.get(2)));
				}
			}
		}
		for(int i=0;i<3;i++) {
			if(i>=recipe.output.size())break;
			builder.addSlot(RecipeIngredientRole.OUTPUT, 109, 6+18*i).addIngredient(VanillaTypes.ITEM_STACK, recipe.output.get(i));
		}
		if (!recipe.in.isEmpty())
			builder.addSlot(RecipeIngredientRole.INPUT, 26, 9)
					.addIngredient(ForgeTypes.FLUID_STACK,recipe.in)
					.setFluidRenderer(1000, false, 16, 46)
					.addTooltipCallback(new BaseCallback(recipe.base, recipe.density));
		if(!recipe.out.isEmpty())
			builder.addSlot(RecipeIngredientRole.OUTPUT, 67, 9)
			.addIngredient(ForgeTypes.FLUID_STACK,recipe.out)
			.setFluidRenderer(1000, false, 16, 46);
	}


	@Override
	public RecipeType<GrindingRecipe> getRecipeType() {
		return TYPE;
	}

}
