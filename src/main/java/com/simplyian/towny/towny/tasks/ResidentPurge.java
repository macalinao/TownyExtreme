package com.simplyian.towny.towny.tasks;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;

import com.simplyian.towny.towny.Towny;
import com.simplyian.towny.towny.TownyMessaging;
import com.simplyian.towny.towny.object.Resident;
import com.simplyian.towny.towny.object.TownyUniverse;


/**
 * @author ElgarL
 *
 */
public class ResidentPurge extends Thread {
	
	Towny plugin;
	private CommandSender sender = null;
	long deleteTime;
	
    /**
     * @param plugin reference to towny
     */
    public ResidentPurge(Towny plugin, CommandSender sender, long deleteTime) {
        super();
        this.plugin = plugin;
        this.deleteTime = deleteTime;
        this.setPriority(MIN_PRIORITY);
    }
    
    @Override
	public void run() {
    	
    	int count = 0;
    	
    	message("Scanning for old residents...");
        for (Resident resident : new ArrayList<Resident>(TownyUniverse.getDataSource().getResidents())) {
                if (!resident.isNPC()
                	&& (System.currentTimeMillis() - resident.getLastOnline() > (this.deleteTime)) && !plugin.isOnline(resident.getName())
                	&& !plugin.isOnline(resident.getName())) {
                	count++;
                	message("Deleting resident: " + resident.getName());
                	TownyUniverse.getDataSource().removeResident(resident);
                	TownyUniverse.getDataSource().removeResidentList(resident);

                }
        }
        
        message("Resident purge complete: " + count + " deleted.");					

    }
    
    private void message(String msg) {
    	
    	if (this.sender != null)
        	TownyMessaging.sendMessage(this.sender, msg);
        else
        	TownyMessaging.sendMsg(msg);
    	
    }
}
    
