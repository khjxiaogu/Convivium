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

package com.khjxiaogu.convivium.client.renderer;

import com.khjxiaogu.convivium.CVMain;
import com.teammoeg.caupona.client.util.DynamicBlockModelReference;

import net.minecraft.client.renderer.rendertype.RenderType;

public class FruitModel {
	public enum ModelType{
		ROUND,
		SLICE,
		MISC
	}
	private DynamicBlockModelReference grid[];
	private DynamicBlockModelReference[][] piled;
	private ModelType type;
	private RenderType rtype;
	public FruitModel(String name,ModelType type,RenderType rtype) {
		super();
		this.type = type;
		for(int i=1;i<=4;i++) {
			grid[i-1]=DynamicBlockModelReference.getModel(CVMain.rl(name+"_components_"+i));
		}
		piled=new DynamicBlockModelReference[4][];
		for(int i=1;i<=4;i++) {
			piled[i-1]=new DynamicBlockModelReference[i];
			for(int j=1;j<=i;j++) {
				piled[i-1][j-1]=DynamicBlockModelReference.getModel(CVMain.rl(name+"_center_"+i+"_"+j));
			}
		}
		this.rtype=rtype;
	}
	public DynamicBlockModelReference getGrid(int i) {
		return grid[i];
	}
	public DynamicBlockModelReference getPiled(int i,int j) {
		return piled[i][j];
	}
	public ModelType getType() {
		return type;
	}
	public RenderType getRenderType() {
		return rtype;
	}
}
