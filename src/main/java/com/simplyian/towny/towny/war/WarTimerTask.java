package com.simplyian.towny.towny.war;

import org.bukkit.entity.Player;

import com.simplyian.towny.towny.exception.NotRegisteredException;
import com.simplyian.towny.towny.TownyMessaging;
import com.simplyian.towny.towny.TownySettings;
import com.simplyian.towny.towny.object.Coord;
import com.simplyian.towny.towny.object.Nation;
import com.simplyian.towny.towny.object.Resident;
import com.simplyian.towny.towny.object.TownBlock;
import com.simplyian.towny.towny.object.TownyUniverse;
import com.simplyian.towny.towny.object.WorldCoord;
import com.simplyian.towny.towny.tasks.TownyTimerTask;

public class WarTimerTask extends TownyTimerTask {
	War warEvent;
	
	public WarTimerTask(War warEvent) {
		super(warEvent.getTownyUniverse());
		this.warEvent = warEvent;
	}

	@Override
	public void run() {
		//TODO: check if war has ended and end gracefully
		if (!warEvent.isWarTime()) {
			warEvent.end();
			universe.clearWarEvent();
			TownyUniverse.getPlugin().updateCache();
			TownyMessaging.sendDebugMsg("War ended.");
			return;
		}
		
		int numPlayers = 0;
		for (Player player : TownyUniverse.getOnlinePlayers()) {
			numPlayers += 1;
			TownyMessaging.sendDebugMsg("[War] "+player.getName()+": ");
			try {
				Resident resident = TownyUniverse.getDataSource().getResident(player.getName());
				if (resident.hasNation()) {
					Nation nation = resident.getTown().getNation();
					TownyMessaging.sendDebugMsg("[War]   hasNation");
					if (nation.isNeutral()) {
						if (warEvent.isWarringNation(nation))
							warEvent.nationLeave(nation);
						continue;
					}
					TownyMessaging.sendDebugMsg("[War]   notNeutral");
					if (!warEvent.isWarringNation(nation))
						continue;
					TownyMessaging.sendDebugMsg("[War]   warringNation");
					//TODO: Cache player coord & townblock
					
					WorldCoord worldCoord = new WorldCoord(TownyUniverse.getDataSource().getWorld(player.getWorld().getName()), Coord.parseCoord(player));
					if (!warEvent.isWarZone(worldCoord))
						continue;
					TownyMessaging.sendDebugMsg("[War]   warZone");
					if (player.getLocation().getBlockY() < TownySettings.getMinWarHeight())
						continue;
					TownyMessaging.sendDebugMsg("[War]   aboveMinHeight");
					TownBlock townBlock = worldCoord.getTownBlock(); //universe.getWorld(player.getWorld().getName()).getTownBlock(worldCoord);
					if (nation == townBlock.getTown().getNation() || townBlock.getTown().getNation().hasAlly(nation))
						continue;
					TownyMessaging.sendDebugMsg("[War]   notAlly");
					//Enemy nation
					warEvent.damage(resident.getTown(), townBlock);
					TownyMessaging.sendDebugMsg("[War]   damaged");
				}
			} catch(NotRegisteredException e) {
				continue;
			}
		}
		
		TownyMessaging.sendDebugMsg("[War] # Players: " + numPlayers);
	}
}