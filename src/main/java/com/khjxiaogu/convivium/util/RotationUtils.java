/*
 * Copyright (c) 2023 IEEM Trivium Society/khjxiaogu
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

package com.khjxiaogu.convivium.util;

import org.joml.AxisAngle4f;
import org.joml.Quaternionf;

import net.minecraft.core.BlockPos;

public class RotationUtils {
	private static int ticksOfSecond;

	public static void resetTimer() {
		ticksOfSecond = 0;
	}

	public static void tick() {
		ticksOfSecond++;
		if (ticksOfSecond >= 40)
			ticksOfSecond = 0;
	}

	public static float getCycle(float pt, BlockPos pos) {
		return getCycle(pt, isBlackGrid(pos));
	}

	public static int getTicks() {
		return ticksOfSecond;
	}

	public static float getCycle(float pt, boolean black) {
		if (black)
			return (ticksOfSecond + pt) / 40f;
		return (20 - (ticksOfSecond + pt)) / 40f;
	}

	public static boolean isBlackGrid(BlockPos pos) {
		return ((pos.getX() & 1) == 1) ^ ((pos.getZ() & 1) == 1);
	}

	public static Quaternionf getYRotation(float pt, boolean black) {
		return new Quaternionf(new AxisAngle4f((float) (getCycle(pt, black) * 2 * Math.PI), 0, 1, 0));
	}

	public static Quaternionf getRotation(float pt, float x, float y, float z, boolean black) {
		return new Quaternionf(new AxisAngle4f((float) (getCycle(pt, black) * 2 * Math.PI), x, y, z));
	}
	public static Quaternionf getRotation(float pt,float delta, float x, float y, float z, boolean black) {
		return new Quaternionf(new AxisAngle4f((float) (getCycle(pt, black) * 2 * Math.PI)+delta, x, y, z));
	}
	public static Quaternionf getYRotation(float pt, BlockPos pos) {
		return getYRotation(pt, isBlackGrid(pos));
	}

	public static Quaternionf getRotation(float pt, float x, float y, float z, BlockPos pos) {
		return getRotation(pt, x, y, z, isBlackGrid(pos));
	}

	public static double getRotationAngle(float pt, boolean black) {
		return getCycle(pt, black) * 2 * Math.PI;
	}

	public static double getRotationAngle(float pt, BlockPos pos) {
		return getRotationAngle(pt, isBlackGrid(pos));
	}
}
