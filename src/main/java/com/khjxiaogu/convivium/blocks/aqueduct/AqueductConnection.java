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
	Z,
	A;
	public static AqueductConnection get(Direction dir,Direction cand) {
		 return A;
	}
	public static AqueductConnection get(Direction dir) {
		switch(dir) {
		case EAST:return E;
		case WEST:return W;
		case NORTH:return N;
		case SOUTH:return S;
		}
		return A;
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
		if(this==A) {
			switch(another) {
			case WEST:return W;
			case EAST:return E;
			case SOUTH:return S;
			case NORTH:return N;
			}
		}
		return this;
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
	
	public Direction[] getNext(Direction from) {
		if(this==NW) {
			switch(from) {
			case WEST:return NORTH;
			case NORTH:return WEST;
			}
			return NRW;
		}
		if(this==SW) {
			switch(from) {
			case WEST:return SOUTH;
			case SOUTH:return WEST;
			}
			return SUW;
		}
		if(this==SE) {
			switch(from) {
			case SOUTH:return EAST;
			case EAST:return SOUTH;
			}
			return SUE;
		}
		if(this==NE) {
			switch(from) {
			case EAST:return NORTH;
			case NORTH:return EAST;
			}
			return NRE;
		}
		if(this==X) {
			switch(from) {
			case EAST:return WEST;
			case WEST:return EAST;
			}
			return WE;
		}
		if(this==Z) {
			switch(from) {
			case SOUTH:return NORTH;
			case NORTH:return SOUTH;
			}
			return NS;
		}
		if(this==N&&from!=Direction.NORTH) {
			return NORTH;
		}
		if(this==E&&from!=Direction.EAST) {
			return EAST;
		}
		if(this==S&&from!=Direction.SOUTH) {
			return SOUTH;
		}
		if(this==W&&from!=Direction.WEST) {
			return WEST;
		}
		return NONE;
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
		if(this==N&&another==Direction.NORTH)
			return A;
		if(this==S&&another==Direction.SOUTH)
			return A;
		if(this==E&&another==Direction.EAST)
			return A;
		if(this==W&&another==Direction.WEST)
			return A;
		return this;
	}
	@Override
	public String getSerializedName() {
		// TODO Auto-generated method stub
		return this.name().toLowerCase();
	}
}
