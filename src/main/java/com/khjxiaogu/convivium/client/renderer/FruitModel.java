/*
 * Copyright (c) 2023 IEEM Trivium Society/khjxiaogu
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
