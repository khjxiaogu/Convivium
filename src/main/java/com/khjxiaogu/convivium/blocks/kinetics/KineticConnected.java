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