package com.simplyian.towny.towny.question.towny;

import com.simplyian.towny.towny.object.Resident;
import com.simplyian.towny.towny.object.Nation;

public class ResidentNationQuestionTask extends TownyQuestionTask {
	protected Resident resident;
	protected Nation nation;
	
	public ResidentNationQuestionTask(Resident resident, Nation nation) {
		this.resident = resident;
		this.nation = nation;
	}
	
	public Resident getResident() {
		return resident;
	}
	
	public Nation getNation() {
		return nation;
	}
	
	@Override
	public void run() {
		
	}

}
