package com.khjxiaogu.convivium.compat.jei;

import java.util.Optional;

import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotRichTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.neoforge.NeoForgeTypes;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.ItemLore;
import net.neoforged.neoforge.fluids.FluidStack;

public class LoreCallback implements IRecipeSlotRichTooltipCallback {

	public LoreCallback() {
		
	}

	@Override
	public void onRichTooltip(IRecipeSlotView recipeSlotView, ITooltipBuilder tooltip) {

		Optional<FluidStack> lorestack=recipeSlotView.getDisplayedIngredient(NeoForgeTypes.FLUID_STACK);
		Optional<ItemLore> lore=lorestack.flatMap(t->Optional.ofNullable(t.get(DataComponents.LORE)));
		lore.ifPresent(t->{
			t.addToTooltip(null, tooltip::add, null);
		});
	}

}
