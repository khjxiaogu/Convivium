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

import java.util.stream.Stream;

import com.teammoeg.caupona.util.ChancedEffect;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;

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
		return fpb.build();
		
	}
	public static Consumable copyWithPart(Consumable cur,int parts) {
		Consumable.Builder fpb=Consumable.builder();
		fpb.animation(cur.animation());
		fpb.consumeSeconds(cur.consumeSeconds());
		fpb.hasConsumeParticles(cur.hasConsumeParticles());
		fpb.sound(cur.sound());
		fpb.soundAfterConsume(cur.sound());
		cur.onConsumeEffects().stream().<ChancedEffect>flatMap(t->{
			if(t instanceof ApplyStatusEffectsConsumeEffect eff) {
				float chance=eff.probability();
				return eff.effects().stream().map(o->new ChancedEffect(o,chance));
			}
			return Stream.empty();
		}).forEach(eff->{
			eff.adjustParts(parts,1);
			eff.toPossibleEffects(fpb);
		});
		return fpb.build();
		
	}
}
