package com.simplyian.towny.towny.question.towny;

import com.simplyian.towny.towny.exception.AlreadyRegisteredException;
import com.simplyian.towny.towny.exception.TownyException;
import com.simplyian.towny.towny.TownyMessaging;
import com.simplyian.towny.towny.TownySettings;
import com.simplyian.towny.towny.object.Resident;
import com.simplyian.towny.towny.object.Town;
import com.simplyian.towny.towny.object.TownyUniverse;
import com.simplyian.towny.util.ChatTools;

public class JoinTownTask extends ResidentTownQuestionTask {
	
	public JoinTownTask(Resident resident, Town town) {
		super(resident, town);
	}

	@Override
	public void run() {
		try {
			town.addResident(resident);
			towny.deleteCache(resident.getName());
			TownyUniverse.getDataSource().saveResident(resident);
			TownyUniverse.getDataSource().saveTown(town);
			
			TownyMessaging.sendTownMessage(town,  ChatTools.color(String.format(TownySettings.getLangString("msg_join_town"), resident.getName())));
		} catch (AlreadyRegisteredException e) {
			try {
				TownyMessaging.sendResidentMessage(resident, e.getMessage());
			} catch (TownyException e1) {
			}
		}
	}
}
