package com.simplyian.towny.towny.question.towny;

import com.simplyian.towny.towny.exception.AlreadyRegisteredException;
import com.simplyian.towny.towny.exception.NotRegisteredException;
import com.simplyian.towny.towny.exception.TownyException;
import com.simplyian.towny.towny.TownyMessaging;
import com.simplyian.towny.towny.TownySettings;
import com.simplyian.towny.towny.object.Resident;
import com.simplyian.towny.towny.object.Nation;
import com.simplyian.towny.towny.object.TownyUniverse;
import com.simplyian.towny.util.ChatTools;

public class JoinNationTask extends ResidentNationQuestionTask {
	
	public JoinNationTask(Resident resident, Nation nation) {
		super(resident, nation);
	}

	@Override
	public void run() {
		try {
			nation.addTown(resident.getTown());
			//towny.deleteCache(resident.getName());
			TownyUniverse.getDataSource().saveResident(resident);
			TownyUniverse.getDataSource().saveTown(resident.getTown());
			TownyUniverse.getDataSource().saveNation(nation);
			
			TownyMessaging.sendNationMessage(nation, ChatTools.color(String.format(TownySettings.getLangString("msg_join_nation"), resident.getTown().getName())));
		} catch (AlreadyRegisteredException e) {
			try {
				TownyMessaging.sendResidentMessage(resident, e.getMessage());
			} catch (TownyException e1) {
			}
		} catch (NotRegisteredException e) {
			// TODO somehow this person is not the town mayor
			e.printStackTrace();
		}
	}
}
