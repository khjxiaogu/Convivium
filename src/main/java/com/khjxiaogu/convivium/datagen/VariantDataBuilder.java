package com.khjxiaogu.convivium.datagen;

import java.util.HashMap;
import java.util.Map;

import com.khjxiaogu.convivium.util.Constants;

public class VariantDataBuilder<T> {
	protected Map<String,Float> variantData=new HashMap<>();
	private T par;
	public VariantDataBuilder(T par) {
		super();
		this.par = par;
	}
	public VariantDataBuilder<T> val(String k,float v){
		variantData.put(k, v);
		return this;
	}
	public VariantDataBuilder<T> sweetness(float v){
		return val(Constants.SWEETNESS,v);
	}
	public VariantDataBuilder<T> astringency(float v){
		return val(Constants.ASTRINGENCY,v);
	}
	public VariantDataBuilder<T> pungency(float v){
		return val(Constants.PUNGENCY,v);
	}
	public VariantDataBuilder<T> soothingness(float v){
		return val(Constants.SOOTHINGNESS,v);
	}
	public VariantDataBuilder<T> thickness(float v){
		return val(Constants.THICKNESS,v);
	}
	public T end() {
		return par;
	};
}
