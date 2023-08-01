package com.khjxiaogu.convivium.item;

import com.khjxiaogu.convivium.CVMain;
import com.teammoeg.caupona.item.CPItem;
import com.teammoeg.caupona.util.CreativeTabItemHelper;

public class CVMaterialItem extends CPItem {

	public CVMaterialItem(Properties properties) {
		super(properties,CVMain.MAIN_TAB);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void fillItemCategory(CreativeTabItemHelper helper) {
		if(helper.isType(CVMain.MAIN_TAB))
			helper.accept(this,2);
	}
}
