package com.khjxiaogu.convivium.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;
import com.khjxiaogu.convivium.CVComponents;
import com.khjxiaogu.convivium.CVFluids;
import com.khjxiaogu.convivium.CVIngredients;
import com.khjxiaogu.convivium.data.recipes.RelishRecipe;
import com.khjxiaogu.convivium.data.recipes.relishcondition.RelishCondition;
import com.khjxiaogu.convivium.data.recipes.relishcondition.RelishConditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teammoeg.caupona.api.GameTranslation;
import com.teammoeg.caupona.util.FloatemTagStack;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.FluidIngredientType;

public class BeverageFluidIngredient extends FluidIngredient {
	public static final MapCodec<BeverageFluidIngredient> CODEC=RecordCodecBuilder.mapCodec(t->t.group(
		Codec.list(Ingredient.CODEC).optionalFieldOf("required",ImmutableList.of()).forGetter(o->o.must),
		Codec.list(Ingredient.CODEC).optionalFieldOf("optional",ImmutableList.of()).forGetter(o->o.optional),
		Codec.list(RelishConditions.CODEC).optionalFieldOf("relish",ImmutableList.of()).forGetter(o->o.relish),
		Codec.list(Codec.STRING).optionalFieldOf("allowedRelish",ImmutableList.of()).forGetter(o->o.allowedRelish),
		Codec.FLOAT.fieldOf("density").forGetter(o->o.density)
		).apply(t, BeverageFluidIngredient::new));
	public List<Ingredient> must;
	public List<Ingredient> optional;
	public List<RelishCondition> relish;
	public List<String> allowedRelish;
	public float density;


	public BeverageFluidIngredient(List<Ingredient> must, List<Ingredient> optional, List<RelishCondition> relish, List<String> allowedRelish, float density) {
		super();
		this.must = must;
		this.optional = optional;
		this.relish = relish;
		this.allowedRelish = allowedRelish;
		this.density = density;
	}

	@Override
	public boolean test(FluidStack fluidStack) {
		BeverageInfo info=fluidStack.get(CVComponents.BEVERAGE_INFO);
		if(info==null)return false;
		BeveragePendingContext ctx=new BeveragePendingContext(info);
		if (ctx.getTotalItems() < density)
			return false;
		
		if(!relish.isEmpty())
			if(!relish.stream().anyMatch(t->t.test(ctx)))
				return false;
		if(!allowedRelish.isEmpty()) {
			if(ctx.relishes.keySet().stream().anyMatch(t->!allowedRelish.contains(t)))
				return false;
		}
		List<FloatemTagStack> filtered=new ArrayList<>(ctx.getItems());
		if(!must.isEmpty()) {
			for(Ingredient i:must) {
				if(!filtered.removeIf(t->i.test(t.getStack())))
					return false;
			}
		}
		if(!optional.isEmpty())
			for(FloatemTagStack is:filtered) {
				if(!optional.stream().anyMatch(e->e.test(is.getStack())))
					return false;
			}

		return true;
	}

	@Override
	protected Stream<FluidStack> generateStacks() {
		FluidStack generated=new FluidStack(CVFluids.mixedf.get(),1000);
		List<Component> components=new ArrayList<>();
		if(relish!=null) 
			for(RelishCondition rl:relish)
				components.add(Utils.string(rl.getTranslation(GameTranslation.get())));
		if(allowedRelish!=null) 
			for(String s:allowedRelish)
				components.add(RelishRecipe.recipes.get(s).value().getText());
		generated.set(DataComponents.LORE, new ItemLore(components));
		return Stream.of(generated);
	}

	@Override
	public boolean isSimple() {
		return false;
	}

	@Override
	public FluidIngredientType<?> getType() {
		return CVIngredients.BEVERAGE_FLUID_INGREDIENT.get();
	}

	@Override
	public int hashCode() {
		return Objects.hash(allowedRelish, density, must, optional, relish);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		BeverageFluidIngredient other = (BeverageFluidIngredient) obj;
		return Objects.equals(allowedRelish, other.allowedRelish) && Float.floatToIntBits(density) == Float.floatToIntBits(other.density) && Objects.equals(must, other.must)
			&& Objects.equals(optional, other.optional) && Objects.equals(relish, other.relish);
	}

}
