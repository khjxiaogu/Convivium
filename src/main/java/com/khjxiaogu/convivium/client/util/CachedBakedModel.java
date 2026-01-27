package com.khjxiaogu.convivium.client.util;

import java.util.Arrays;
import java.util.function.Function;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;

public class CachedBakedModel {
	BakedModel[] models;
	int version=0;
	Function<Direction,BakedModel> builder;
	public CachedBakedModel(Function<Direction,BakedModel> build) {
		models=new BakedModel[6];
		builder=build;
	}
	public void updateVersion(int newver) {
		if(version!=newver)
			Arrays.fill(models, null);
		version=newver;
	}
	public BakedModel getModel(Direction val) {
		int idx=val.ordinal();
		if(models[idx]==null)
			models[idx]=builder.apply(val);
		return models[idx];
	}
	public BakedModel getModel(Direction val,int newver) {
		updateVersion(newver);
		return getModel(val);
	}
}
