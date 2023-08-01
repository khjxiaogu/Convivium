package com.khjxiaogu.convivium.datagen;

import java.util.function.BiFunction;
import java.util.function.Consumer;

import com.khjxiaogu.convivium.data.recipes.BeverageTypeRecipe;
import com.khjxiaogu.convivium.data.recipes.relishcondition.AndRelishCondition;
import com.khjxiaogu.convivium.data.recipes.relishcondition.HasFluidCondition;
import com.khjxiaogu.convivium.data.recipes.relishcondition.HasRelishCondition;
import com.khjxiaogu.convivium.data.recipes.relishcondition.LogicalRelishCondition;
import com.khjxiaogu.convivium.data.recipes.relishcondition.MajorRelishCondition;
import com.khjxiaogu.convivium.data.recipes.relishcondition.OnlyMajorRelishCondition;
import com.khjxiaogu.convivium.data.recipes.relishcondition.OrRelishCondition;
import com.khjxiaogu.convivium.data.recipes.relishcondition.RelishCondition;
import com.teammoeg.caupona.data.IDataRecipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;

public class TypeRecipeBuilder {
	private BeverageTypeRecipe recipe;
	public TypeRecipeBuilder(ResourceLocation rl,Fluid out) {
		recipe=new BeverageTypeRecipe(rl);
		recipe.output=out;
	}
	public TypeRecipeBuilder mustContains(Ingredient igd) {
		recipe.must.add(igd);
		return this;
	}
	public TypeRecipeBuilder mustContains(ItemStack igd) {
		return mustContains(Ingredient.of(igd));
	}
	public TypeRecipeBuilder mustContains(Item igd) {
		return mustContains(Ingredient.of(igd));
	}
	public TypeRecipeBuilder mustContains(TagKey<Item> igd) {
		return mustContains(Ingredient.of(igd));
	}
	public TypeRecipeBuilder canContains(Ingredient igd) {
		recipe.optional.add(igd);
		return this;
	}
	public TypeRecipeBuilder canContains(ItemStack igd) {
		return canContains(Ingredient.of(igd));
	}
	public TypeRecipeBuilder canContains(Item igd) {
		return canContains(Ingredient.of(igd));
	}
	public TypeRecipeBuilder canContains(TagKey<Item> igd) {
		return canContains(Ingredient.of(igd));
	}
	private RelishCondition temp;
	private BiFunction<RelishCondition,RelishCondition,LogicalRelishCondition> condition;
	public TypeRecipeBuilder cond(RelishCondition relish) {
		if(temp==null) {
			temp=relish;
		}else if(condition!=null) {
			temp=condition.apply(temp, relish);
			condition=null;
		}else {
			recipe.relish.add(temp);
			temp=relish;
		}
		
		return this;
	}
	public TypeRecipeBuilder major(String relish) {
		return cond(new MajorRelishCondition(relish));
	}
	public TypeRecipeBuilder only(String relish) {
		return cond(new OnlyMajorRelishCondition(relish));
	}

	public TypeRecipeBuilder has(String relish) {
		return cond(new HasRelishCondition(relish));
	}
	public TypeRecipeBuilder has(Fluid relish) {
		return cond(new HasFluidCondition(relish));
	}
	public TypeRecipeBuilder and() {
		condition=AndRelishCondition::new;
		return this;
	}
	public TypeRecipeBuilder or() {
		condition=OrRelishCondition::new;
		return this;
	}
	public TypeRecipeBuilder allow(String relish) {
		recipe.allowedRelish.add(relish);
		return this;
	}
	public TypeRecipeBuilder density(float val) {
		recipe.density=val;
		return this;
	}
	public TypeRecipeBuilder time(int time) {
		recipe.time=time;
		return this;
	}
	public TypeRecipeBuilder priority(int val) {
		recipe.priority=val;
		return this;
	}
	public TypeRecipeBuilder removeNBT() {
		recipe.removeNBT=true;
		return this;
	}
	public void end(Consumer<IDataRecipe> out) {
		if(temp!=null) {
			recipe.relish.add(temp);
		}
		out.accept(recipe);
	}
}
