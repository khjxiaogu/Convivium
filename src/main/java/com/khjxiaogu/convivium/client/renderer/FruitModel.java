package com.khjxiaogu.convivium.client.renderer;

import com.khjxiaogu.convivium.CVMain;
import com.teammoeg.caupona.client.util.DynamicBlockModelReference;
import com.teammoeg.caupona.client.util.ModelUtils;

public class FruitModel {
	public enum ModelType{
		ROUND,
		SLICE,
		MISC
	}
	private DynamicBlockModelReference grid;
	private DynamicBlockModelReference[] piled;
	private ModelType type;
	public FruitModel(String name,ModelType type) {
		super();
		this.type = type;
		grid=ModelUtils.getModel(CVMain.MODID, name+"_components");
		piled=new DynamicBlockModelReference[4];
		for(int i=1;i<=4;i++) {
			piled[i-1]=ModelUtils.getModel(CVMain.MODID, name+"_center_"+i);
		}
	}
	public DynamicBlockModelReference getGrid() {
		return grid;
	}
	public DynamicBlockModelReference getPiled(int i) {
		return piled[i];
	}
	public ModelType getType() {
		return type;
	}
	
}
