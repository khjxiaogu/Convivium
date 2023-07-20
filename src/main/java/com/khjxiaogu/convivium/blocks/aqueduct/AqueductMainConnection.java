package com.khjxiaogu.convivium.blocks.aqueduct;

import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;

public enum AqueductMainConnection implements StringRepresentable{
	N,
	L,
	R,
	A;
	public AqueductMainConnection connects(Direction now,Direction another) {
		if(this==N) {
			if(another==now.getClockWise())return R;
			if(another==now.getCounterClockWise())return L;
		}else
		if(this==L) {
			if(another==now.getClockWise())return A;
		}else
		if(this==R) {
			if(another==now.getCounterClockWise())return A;
		}
		return this;
	}
	public AqueductMainConnection disconnects(Direction now,Direction another) {
		if(this==A) {
			if(another==now.getClockWise())return L;
			if(another==now.getCounterClockWise())return R;
		}else if(this==L) {
			if(another==now.getCounterClockWise())return N;
		}else if(this==R) {
			if(another==now.getClockWise())return N;
		}
		return this;
	}
	@Override
	public String getSerializedName() {
		// TODO Auto-generated method stub
		return this.name().toLowerCase();
	}
}
