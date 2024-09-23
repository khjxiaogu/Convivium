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

package com.khjxiaogu.convivium.blocks.kinetics;

import com.khjxiaogu.convivium.CVConfig;
import com.teammoeg.caupona.util.LazyTickWorker;

public interface KineticConnected {

	int getSpeed();

	void setSpeed(int val);

	boolean isReceiver();
	public static LazyTickWorker createKineticValidator(KineticConnected entity) {
		return new LazyTickWorker(CVConfig.SERVER.kineticValidation.get(),()->{
			if(entity.getSpeed()!=0) {
				entity.setSpeed(0);
				return true;
			}
			return false;
		});
	}
}