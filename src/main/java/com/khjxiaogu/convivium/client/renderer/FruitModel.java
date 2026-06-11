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
	private DynamicBlockModelReference[] grid;
	private DynamicBlockModelReference[][] piled;
	private DynamicBlockModelReference[] piledAll;
	private boolean hasFoil;
	private ModelType type;
	private RenderType rtype;
	private String name;
	public FruitModel(String name,ModelType type,RenderType rtype,boolean hasFoil) {
		super();
		this.name=name;
		this.type = type;
		this.hasFoil=hasFoil;
		this.grid=new DynamicBlockModelReference[4];
		for(int i=1;i<=4;i++) {
			grid[i-1]=DynamicBlockModelReference.getModel(CVMain.rl("block/dynamic/"+name+"_components_"+i));
		}
		this.piled=new DynamicBlockModelReference[4][];
		this.piledAll=new DynamicBlockModelReference[4];
		for(int i=1;i<=4;i++) {
			piled[i-1]=new DynamicBlockModelReference[i];
			boolean hasModel=true;
			for(int j=1;j<=i;j++) {
				String path="block/dynamic/"+name+"_center_"+i+"_"+j;
				DynamicBlockModelReference model=DynamicBlockModelReference.getModel(CVMain.rl(path));
				if(type!=ModelType.MISC) {
					if(model==null){
						CVMain.logger.warn("Missing model for piled of "+name+" position "+j+" in "+i);
						hasModel=false;
					}
				}
				piled[i-1][j-1]=model;
			}
			String path="block/dynamic/"+name+"_center_"+i;
			piledAll[i-1]=DynamicBlockModelReference.getModel(CVMain.rl(path));
			if(piledAll[i-1]==null) {
				if(type==ModelType.MISC||!hasModel) {
					CVMain.logger.warn("Missing model for piled of "+name+" count "+i);
				}
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
	public DynamicBlockModelReference getPiled(int i) {
		return piledAll[i];
	}
	public ModelType getType() {
		return type;
	}
	public RenderType getRenderType() {
		return rtype;
	}
	@Override
	public String toString() {
		return "FruitModel [name=" + name + ", type=" + type + "]";
	}
	public boolean hasFoil() {
		return hasFoil;
	}
}
