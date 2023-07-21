package com.khjxiaogu.convivium.data.recipes.relishcondition;

import java.util.function.Predicate;

import com.khjxiaogu.convivium.util.BeveragePendingContext;
import com.teammoeg.caupona.data.ITranlatable;
import com.teammoeg.caupona.data.Writeable;

public interface RelishCondition extends Predicate<BeveragePendingContext>, Writeable, ITranlatable  {
	public abstract String getType();
}
