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

public enum AqueductConnection implements StringRepresentable{
	N(false,true,true,true),
	W(true,true,false,true),
	S(true,false,true,true),
	E(true,true,true,false),
	NE(false,true,true,false),
	NW(false,true,false,true),
	SE(true,false,true,false),
	SW(true,false,false,true),
	X(true,true,false,false),
	Z(false,false,true,true),
	A(true,true,true,true);
	public final boolean n;
	public final boolean s;
	public final boolean w;
	public final boolean e;
	private AqueductConnection(boolean n, boolean s, boolean w, boolean e) {
		this.n = n;
		this.s = s;
		this.w = w;
		this.e = e;
	}
	public static AqueductConnection get(Direction dir) {
		return switch(dir) {
		case EAST->E;
		case WEST->W;
		case NORTH->N;
		case SOUTH->S;
		default->A;
		};
	}
	public AqueductConnection connects(Direction another) {
		return switch(this) {
		case N:{
			yield switch(another) {
			case WEST->NW;
			case EAST->NE;
			case SOUTH->Z;
			default->this;
			};
		}
		case S:{
			yield switch(another) {
			case WEST->SW;
			case EAST->SE;
			case NORTH->Z;
			default->this;
			};
		}
		case W:{
			yield switch(another) {
			case NORTH->NW;
			case SOUTH->SW;
			case EAST->X;
			default->this;
			};
		}
		case E:{
			yield switch(another) {
			case WEST->X;
			case NORTH->NE;
			case SOUTH->SE;
			default->this;
			};
		}
		case A:{
			yield switch(another) {
			case WEST->W;
			case EAST->E;
			case SOUTH->S;
			case NORTH->N;
			default->this;
			};
		}
		default:yield this;
		};
	}
	private static final Direction[] NORTH=new Direction[] {Direction.NORTH};
	private static final Direction[] WEST=new Direction[] {Direction.WEST};
	private static final Direction[] SOUTH=new Direction[] {Direction.SOUTH};
	private static final Direction[] EAST=new Direction[] {Direction.EAST};
	private static final Direction[] NONE=new Direction[0];
	private static final Direction[] NRW=new Direction[] {Direction.NORTH,Direction.WEST};
	private static final Direction[] SUW=new Direction[] {Direction.SOUTH,Direction.WEST};
	private static final Direction[] SUE=new Direction[] {Direction.SOUTH,Direction.EAST};
	private static final Direction[] NRE=new Direction[] {Direction.NORTH,Direction.EAST};
	private static final Direction[] WE=new Direction[] {Direction.EAST,Direction.WEST};
	private static final Direction[] NS=new Direction[] {Direction.NORTH,Direction.SOUTH};
	public boolean canConnectTo(Direction d) {
		return switch(d) {
		case NORTH->!n;
		case SOUTH->!s;
		case WEST->!w;
		case EAST->!e;
		default->false;
		};
	}
	public Direction[] getNext(Direction from) {
		return switch(this) {
		case NW:{
			yield switch(from) {
			case WEST->NORTH;
			case NORTH->WEST;
			default->NRW;
			};
		}
		case SW:{
			yield switch(from) {
			case WEST->SOUTH;
			case SOUTH->WEST;
			default->SUW;
			};
		}
		case SE:{
			yield switch(from) {
			case SOUTH->EAST;
			case EAST->SOUTH;
			default->SUE;
			};
		}
		case NE:{
			yield switch(from) {
			case EAST->NORTH;
			case NORTH->EAST;
			default->NRE;
			};
		}
		case X:{
			yield switch(from) {
			case EAST->WEST;
			case WEST->EAST;
			default->WE;
			};
		}
		case Z:{
			yield switch(from) {
			case SOUTH->NORTH;
			case NORTH->SOUTH;
			default->NS;
			};
		}
		default:{
			if(this==N&&from!=Direction.NORTH) {
				yield NORTH;
			}
			if(this==E&&from!=Direction.EAST) {
				yield EAST;
			}
			if(this==S&&from!=Direction.SOUTH) {
				yield SOUTH;
			}
			if(this==W&&from!=Direction.WEST) {
				yield WEST;
			}
			yield NONE;
		}
		};
		
	}
	public AqueductConnection disconnects(Direction another) {
		return switch(this) {
		case NW:{
			yield switch(another) {
				case WEST->N;
				case NORTH->W;
				default->this;
			};
		}
		case SW:{
			yield switch(another) {
				case WEST->S;
				case SOUTH->W;
				default->this;
			};
		}
		case SE:{
			yield switch(another) {
				case SOUTH->E;
				case EAST->S;
				default->this;
			};
		}
		case NE:{
			yield switch(another) {
				case EAST->N;
				case NORTH->E;
				default->this;
			};
		}
		case X:{
			yield switch(another) {
				case EAST->W;
				case WEST->E;
				default->this;
			};
		}
		case Z:{
			yield switch(another) {
				case SOUTH->N;
				case NORTH->S;
				default->this;
			};
		}
		default:{
			if(this==N&&another==Direction.NORTH)
				yield A;
			if(this==S&&another==Direction.SOUTH)
				yield A;
			if(this==E&&another==Direction.EAST)
				yield A;
			if(this==W&&another==Direction.WEST)
				yield A;
			yield this;
		}
		};
		
	}
	@Override
	public String getSerializedName() {
		return this.name().toLowerCase();
	}
}
