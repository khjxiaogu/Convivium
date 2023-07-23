package com.khjxiaogu.convivium.datagen;

import java.util.function.Consumer;

import com.khjxiaogu.convivium.data.recipes.TasteRecipe;
import com.teammoeg.caupona.data.IDataRecipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

public class TasteRecipeBuilder{
	private int priority;
	private Ingredient item;
	private ResourceLocation rl;
	VariantDataBuilder<TasteRecipeBuilder> vars=new VariantDataBuilder<TasteRecipeBuilder>(this);
	public TasteRecipeBuilder(ResourceLocation rl) {
		this.rl = rl;
	}
	public VariantDataBuilder<TasteRecipeBuilder> vars(){
		return vars;
	}
	public TasteRecipeBuilder item(Ingredient igd) {
		item=igd;
		return this;
	}
	public TasteRecipeBuilder priority(int ig) {
		priority=ig;
		return this;
	}
	public void end(Consumer<IDataRecipe> out) {
		out.accept(new TasteRecipe(rl,vars.variantData, priority, item));
	}
}
