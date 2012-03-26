package com.simplyian.towny.towny.question.towny;

import com.simplyian.towny.towny.object.Resident;
import com.simplyian.towny.towny.object.Town;

public class ResidentTownQuestionTask extends TownyQuestionTask {
	protected Resident resident;
	protected Town town;
	
	public ResidentTownQuestionTask(Resident resident, Town town) {
		this.resident = resident;
		this.town = town;
	}
	
	public Resident getResident() {
		return resident;
	}
	
	public Town getTown() {
		return town;
	}
	
	@Override
	public void run() {
		
	}

}
