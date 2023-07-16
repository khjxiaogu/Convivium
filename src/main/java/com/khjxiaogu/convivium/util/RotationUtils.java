package com.khjxiaogu.convivium.util;

import org.joml.AxisAngle4f;
import org.joml.Quaternionf;

import net.minecraft.core.BlockPos;

public class RotationUtils {
	private static int ticksOfSecond;
	public static void resetTimer() {
		ticksOfSecond=0;
	}
	public static void tick() {
		ticksOfSecond++;
	}
	public static float getCycle(float pt,BlockPos pos) {
		return getCycle(pt,isBlackGrid(pos));
	}
	public static float getCycle(float pt,boolean black) {
		if(black)
			return (ticksOfSecond+pt)/20f;
		return (20-(ticksOfSecond+pt))/20f;
	}
	public static boolean isBlackGrid(BlockPos pos) {
		return ((pos.getX()&1)==1)^((pos.getZ()&1)==1);
	}
	public static Quaternionf getYRotation(float pt,boolean black) {
		return new Quaternionf(new AxisAngle4f((float) (getCycle(pt,black)*2*Math.PI),0,1,0));
	}
	public static Quaternionf getRotation(float pt,float x,float y,float z,boolean black) {
		return new Quaternionf(new AxisAngle4f((float) (getCycle(pt,black)*2*Math.PI),x,y,z));
	}
	public static Quaternionf getYRotation(float pt,BlockPos pos) {
		return getYRotation(pt,isBlackGrid(pos));
	}
	public static Quaternionf getRotation(float pt,float x,float y,float z,BlockPos pos) {
		return getRotation(pt,x,y,z,isBlackGrid(pos));
	}
}
