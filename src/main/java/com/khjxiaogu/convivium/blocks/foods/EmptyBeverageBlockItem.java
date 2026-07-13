package com.khjxiaogu.convivium.blocks.foods;
import com.teammoeg.caupona.util.CreativeTabItemHelper;
import com.teammoeg.caupona.util.ICreativeModeTabItem;
import com.teammoeg.caupona.util.TabType;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class EmptyBeverageBlockItem extends BlockItem implements ICreativeModeTabItem{
	public TabType tab;
	public EmptyBeverageBlockItem(Block block, Item.Properties props,TabType tab) {
		super(block, props);
		this.tab=tab;
	}
	public EmptyBeverageBlockItem(Block block, Item.Properties props) {
		super(block,props);
	}

	@Override
	public void fillItemCategory(CreativeTabItemHelper helper) {
		if(super.getBlock() instanceof ICreativeModeTabItem item) {
			item.fillItemCategory(helper);
		}else if(helper.isType(tab))
			helper.accept(this);
	}

}