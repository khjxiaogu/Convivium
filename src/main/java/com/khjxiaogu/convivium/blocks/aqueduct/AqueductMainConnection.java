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
