package com.khjxiaogu.convivium.datagen;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import com.khjxiaogu.convivium.data.recipes.relishcondition.AndRelishCondition;
import com.khjxiaogu.convivium.data.recipes.relishcondition.HasFluidCondition;
import com.khjxiaogu.convivium.data.recipes.relishcondition.HasRelishCondition;
import com.khjxiaogu.convivium.data.recipes.relishcondition.LogicalRelishCondition;
import com.khjxiaogu.convivium.data.recipes.relishcondition.MajorRelishCondition;
import com.khjxiaogu.convivium.data.recipes.relishcondition.OnlyMajorRelishCondition;
import com.khjxiaogu.convivium.data.recipes.relishcondition.OrRelishCondition;
import com.khjxiaogu.convivium.data.recipes.relishcondition.RelishCondition;
import com.khjxiaogu.convivium.util.BeverageFluidIngredient;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;

public class BeverageIngredientBuilder {
	private List<Ingredient> must=new ArrayList<>();
	private List<Ingredient> optional=new ArrayList<>();
	private List<RelishCondition> relish=new ArrayList<>();
	private List<String> allowedRelish=new ArrayList<>();
	private float density;
	public BeverageIngredientBuilder() {
	}
	public BeverageIngredientBuilder mustContains(Ingredient igd) {
		must.add(igd);
		return this;
	}
	public BeverageIngredientBuilder mustContains(ItemStack igd) {
		return mustContains(Ingredient.of(igd));
	}
	public BeverageIngredientBuilder mustContains(Item igd) {
		return mustContains(Ingredient.of(igd));
	}
	public BeverageIngredientBuilder mustContains(TagKey<Item> igd) {
		return mustContains(Ingredient.of(igd));
	}
	public BeverageIngredientBuilder canContains(Ingredient igd) {
		optional.add(igd);
		return this;
	}
	public BeverageIngredientBuilder canContains(ItemStack igd) {
		return canContains(Ingredient.of(igd));
	}
	public BeverageIngredientBuilder canContains(Item igd) {
		return canContains(Ingredient.of(igd));
	}
	public BeverageIngredientBuilder canContains(TagKey<Item> igd) {
		return canContains(Ingredient.of(igd));
	}
	private RelishCondition temp;
	private BiFunction<RelishCondition,RelishCondition,LogicalRelishCondition> condition;
	public BeverageIngredientBuilder cond(RelishCondition relish) {
		if(temp==null) {
			temp=relish;
		}else if(condition!=null) {
			temp=condition.apply(temp, relish);
			condition=null;
		}else {
			this.relish.add(temp);
			temp=relish;
		}
		
		return this;
	}
	public BeverageIngredientBuilder major(String relish) {
		allow(relish);
		return cond(new MajorRelishCondition(relish));
	}
	public BeverageIngredientBuilder only(String relish) {
		allow(relish);
		return cond(new OnlyMajorRelishCondition(relish));
	}

	public BeverageIngredientBuilder has(String relish) {
		allow(relish);
		return cond(new HasRelishCondition(relish));
	}
	public BeverageIngredientBuilder has(Fluid relish) {
		return cond(new HasFluidCondition(relish));
	}
	public BeverageIngredientBuilder and() {
		condition=AndRelishCondition::new;
		return this;
	}
	public BeverageIngredientBuilder or() {
		condition=OrRelishCondition::new;
		return this;
	}
	public BeverageIngredientBuilder allow(String relish) {
		allowedRelish.add(relish);
		return this;
	}
	public BeverageIngredientBuilder density(float val) {
		density=val;
		return this;
	}
	public BeverageFluidIngredient build() {
		if(temp!=null) {
			relish.add(temp);
		}
		return new BeverageFluidIngredient(must,optional,relish,allowedRelish, density);
	}
}
