package com.simplyian.towny.towny.tasks;

import java.util.TimerTask;

import com.simplyian.towny.towny.Towny;
import com.simplyian.towny.towny.object.TownyUniverse;

public abstract class TownyTimerTask extends TimerTask {
	protected TownyUniverse universe;
	protected Towny plugin;

	public TownyTimerTask(TownyUniverse universe) {
		this.universe = universe;
		this.plugin = TownyUniverse.getPlugin();
	}

	//@Override
	//public void run() {

	//}

}
