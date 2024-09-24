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

package com.khjxiaogu.convivium.data.recipes.relishcondition;

import com.mojang.datafixers.Products.P2;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import com.teammoeg.caupona.data.TranslationProvider;

public abstract class LogicalRelishCondition implements RelishCondition {
	protected RelishCondition r1;
	protected RelishCondition r2;
	public static <P extends LogicalRelishCondition> P2<Mu<P>,RelishCondition,RelishCondition>  codecStart(Instance<P> i) {
		return i.group(RelishConditions.CODEC.fieldOf("cond1").forGetter(o->o.r1),RelishConditions.CODEC.fieldOf("cond2").forGetter(o->o.r2));
	}
	public LogicalRelishCondition(RelishCondition r1, RelishCondition r2) {
		super();
		this.r1 = r1;
		this.r2 = r2;
	}

	@Override
	public String getTranslation(TranslationProvider p) {
		// TODO Auto-generated method stub
		return p.getTranslation("recipe.convivium.relish_cond."+getType(),r1.getTranslation(p),r2.getTranslation(p));
	}
	protected abstract String getType();

}
