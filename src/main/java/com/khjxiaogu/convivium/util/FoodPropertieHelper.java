/*
 * Copyright (c) 2024 IEEM Trivium Society/khjxiaogu
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
