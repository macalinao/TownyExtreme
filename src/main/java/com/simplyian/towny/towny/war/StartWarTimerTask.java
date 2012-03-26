package com.simplyian.towny.towny.war;

import com.simplyian.towny.towny.object.TownyUniverse;
import com.simplyian.towny.towny.tasks.TownyTimerTask;

public class StartWarTimerTask extends TownyTimerTask {

	public StartWarTimerTask(TownyUniverse universe) {
		super(universe);
	}

	@Override
	public void run() {
		universe.getWarEvent().start();
	}

}
