package com.khjxiaogu.convivium.util;

import com.teammoeg.caupona.util.ChancedEffect;

import net.minecraft.world.food.FoodProperties;

public class FoodPropertieHelper {

	private FoodPropertieHelper() {
	}
	public static FoodProperties copyWithPart(FoodProperties cur,int parts) {
        FoodProperties.Builder fpb=new FoodProperties.Builder();
        fpb.nutrition(cur.nutrition()/parts);
        if(cur.nutrition()!=0)
        	fpb.saturationModifier(cur.saturation()/cur.nutrition()/2/parts);
        if(cur.canAlwaysEat())
        	fpb.alwaysEdible();
        cur.usingConvertsTo().ifPresent(t->fpb.usingConvertsTo(t.getItem()));
        cur.effects().stream().map(ChancedEffect::new).forEach(t->{
        	t.adjustParts(parts,1);
        	t.toPossibleEffects(fpb);
        });
		return fpb.build();
		
	}
}
