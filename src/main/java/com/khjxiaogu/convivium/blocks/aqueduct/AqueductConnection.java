package com.khjxiaogu.convivium.blocks.aqueduct;

import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;

public enum AqueductConnection implements StringRepresentable{
	N,
	W,
	S,
	E,
	NE,
	NW,
	SE,
	SW,
	X,
	Z;
	public static AqueductConnection get(Direction dir,Direction cand) {
		 AqueductConnection s1=get(dir);
		 return s1==null?get(cand):s1;
	}
	public static AqueductConnection get(Direction dir) {
		switch(dir) {
		case EAST:return E;
		case WEST:return W;
		case NORTH:return N;
		case SOUTH:return S;
		}
		return null;
	}
	public AqueductConnection connects(Direction another) {
		if(this==N) {
			switch(another) {
			case WEST:return NW;
			case EAST:return NE;
			case SOUTH:return Z;
			}
			return this;
		}
		if(this==S) {
			switch(another) {
			case WEST:return SW;
			case EAST:return SE;
			case NORTH:return Z;
			}
			return this;
		}
		if(this==W) {
			switch(another) {
			case NORTH:return NW;
			case SOUTH:return SW;
			case EAST:return X;
			}
			return this;
		}
		if(this==E) {
			switch(another) {
			case WEST:return X;
			case NORTH:return NE;
			case SOUTH:return SE;
			}
			return this;
		}
		return this;
	}
	public Direction[] getNext(Direction from) {
		if(this==NW) {
			switch(from) {
			case WEST:return new Direction[] {Direction.NORTH};
			case NORTH:return new Direction[] {Direction.WEST};
			}
			return new Direction[] {Direction.NORTH,Direction.WEST};
		}
		if(this==SW) {
			switch(from) {
			case WEST:return new Direction[] {Direction.SOUTH};
			case SOUTH:return new Direction[] {Direction.WEST};
			}
			return new Direction[] {Direction.SOUTH,Direction.WEST};
		}
		if(this==SE) {
			switch(from) {
			case SOUTH:return new Direction[] {Direction.EAST};
			case EAST:return new Direction[] {Direction.SOUTH};
			}
			return new Direction[] {Direction.SOUTH,Direction.EAST};
		}
		if(this==NE) {
			switch(from) {
			case EAST:return new Direction[] {Direction.NORTH};
			case NORTH:return new Direction[] {Direction.EAST};
			}
			return new Direction[] {Direction.NORTH,Direction.EAST};
		}
		if(this==X) {
			switch(from) {
			case EAST:return new Direction[] {Direction.WEST};
			case WEST:return new Direction[] {Direction.EAST};
			}
			return new Direction[] {Direction.EAST,Direction.WEST};
		}
		if(this==Z) {
			switch(from) {
			case SOUTH:return new Direction[] {Direction.NORTH};
			case NORTH:return new Direction[] {Direction.SOUTH};
			}
			return new Direction[] {Direction.NORTH,Direction.SOUTH};
		}
		if(this==N&&from!=Direction.NORTH) {
			return new Direction[] {Direction.NORTH};
		}
		if(this==E&&from!=Direction.EAST) {
			return new Direction[] {Direction.EAST};
		}
		if(this==S&&from!=Direction.SOUTH) {
			return new Direction[] {Direction.SOUTH};
		}
		if(this==W&&from!=Direction.WEST) {
			return new Direction[] {Direction.WEST};
		}
		return new Direction[0];
	}
	public AqueductConnection disconnects(Direction another) {
		if(this==NW) {
			switch(another) {
			case WEST:return N;
			case NORTH:return W;
			}
			return this;
		}
		if(this==SW) {
			switch(another) {
			case WEST:return S;
			case SOUTH:return W;
			}
			return this;
		}
		if(this==SE) {
			switch(another) {
			case SOUTH:return E;
			case EAST:return S;
			}
			return this;
		}
		if(this==NE) {
			switch(another) {
			case EAST:return N;
			case NORTH:return E;
			}
			return this;
		}
		if(this==X) {
			switch(another) {
			case EAST:return W;
			case WEST:return E;
			}
			return this;
		}
		if(this==Z) {
			switch(another) {
			case SOUTH:return N;
			case NORTH:return S;
			}
			return this;
		}
		return this;
	}
	@Override
	public String getSerializedName() {
		// TODO Auto-generated method stub
		return this.name().toLowerCase();
	}
}
