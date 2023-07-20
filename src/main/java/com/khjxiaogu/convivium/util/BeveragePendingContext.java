package com.khjxiaogu.convivium.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.khjxiaogu.convivium.data.recipes.relishcondition.RelishCondition;
import com.teammoeg.caupona.data.recipes.IPendingContext;
import com.teammoeg.caupona.util.FloatemStack;
import com.teammoeg.caupona.util.FloatemTagStack;
import com.teammoeg.caupona.util.ResultCachingMap;

public class BeveragePendingContext extends IPendingContext {
	private ResultCachingMap<RelishCondition, Boolean> relish = new ResultCachingMap<>(e -> e.test(this));
	public Map<String,Integer> relishes=new HashMap<>();
	public List<String> activerelish=new ArrayList<>();
	public BeveragePendingContext(BeverageInfo info) {
		items = new ArrayList<>(info.stacks.size());
		for (FloatemStack fs : info.stacks) {
			super.items.add(new FloatemTagStack(fs));
			totalItems += fs.getCount();
		}
		for(String s:info.relishes) {
			if(s==null)continue;
			relishes.compute(s,(k,v)->v==null?1:v+1);
		}
		
	}
	public void checkActiveRelish() {
		int max=relishes.values().stream().mapToInt(t->t).max().orElse(0);
		if(max==1&&relishes.size()>1) {
			return;
		}
		for(Entry<String, Integer> i:relishes.entrySet()) {
			if(i.getValue()==max) {
				activerelish.add(i.getKey());
			}
		}
	}
	public boolean compute(RelishCondition cond) {
		return relish.compute(cond);
	}

	
}
