package com.khjxiaogu.convivium.util;

import java.util.ArrayList;

import com.teammoeg.caupona.data.recipes.IPendingContext;
import com.teammoeg.caupona.util.FloatemStack;
import com.teammoeg.caupona.util.FloatemTagStack;

public class BeveragePendingContext extends IPendingContext {

	public BeveragePendingContext(BeverageInfo info) {
		items = new ArrayList<>(info.stacks.size());
		for (FloatemStack fs : info.stacks) {
			super.items.add(new FloatemTagStack(fs));
			totalItems += fs.getCount();
		}
	}

}
