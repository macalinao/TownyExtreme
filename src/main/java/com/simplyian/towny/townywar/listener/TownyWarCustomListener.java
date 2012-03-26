package com.simplyian.towny.townywar.listener;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.simplyian.towny.towny.exception.NotRegisteredException;
import com.simplyian.towny.towny.Towny;
import com.simplyian.towny.towny.exception.TownyException;
import com.simplyian.towny.towny.TownyMessaging;
import com.simplyian.towny.towny.TownySettings;
import com.simplyian.towny.towny.command.TownCommand;
import com.simplyian.towny.towny.object.Nation;
import com.simplyian.towny.towny.object.Resident;
import com.simplyian.towny.towny.object.Town;
import com.simplyian.towny.towny.object.TownBlock;
import com.simplyian.towny.towny.object.TownyUniverse;
import com.simplyian.towny.towny.object.TownyWorld;
import com.simplyian.towny.towny.object.WorldCoord;
import com.simplyian.towny.towny.tasks.TownClaim;
import com.simplyian.towny.townywar.CellUnderAttack;
import com.simplyian.towny.townywar.TownyWar;
import com.simplyian.towny.townywar.event.CellAttackCanceledEvent;
import com.simplyian.towny.townywar.event.CellAttackEvent;
import com.simplyian.towny.townywar.event.CellDefendedEvent;
import com.simplyian.towny.townywar.event.CellWonEvent;

public class TownyWarCustomListener implements Listener {
	private final Towny plugin;

	public TownyWarCustomListener(Towny instance) {
		plugin = instance;
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onCellAttackEvent(CellAttackEvent event) {
		
			try {
				CellUnderAttack cell = event.getData();
				TownyWar.registerAttack(cell);
			} catch (Exception e) {
				event.setCancelled(true);
				event.setReason(e.getMessage());
			}
			
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onCellDefendedEvent(CellDefendedEvent event) {
		
			Player player = event.getPlayer();
			CellUnderAttack cell = event.getCell().getAttackData();
			
			TownyUniverse universe = plugin.getTownyUniverse();
			try {
				TownyWorld world = TownyUniverse.getDataSource().getWorld(cell.getWorldName());
				WorldCoord worldCoord = new WorldCoord(world, cell.getX(), cell.getZ());
				universe.removeWarZone(worldCoord);
				
				plugin.updateCache(worldCoord);
			} catch (NotRegisteredException e) {
				e.printStackTrace();
			}
			
			String playerName;
			if (player == null) {
				playerName = "Greater Forces";
			} else {
				playerName = player.getName();
				try {
					playerName = TownyUniverse.getDataSource().getResident(player.getName()).getFormattedName();
				} catch (TownyException e) {
				}
			}
			
			plugin.getServer().broadcastMessage(String.format(TownySettings.getLangString("msg_enemy_war_area_defended"),
					playerName,
					cell.getCellString()));
			
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onCellWonEvent(CellWonEvent event) {
		
			CellUnderAttack cell = event.getCellAttackData();
			
			TownyUniverse universe = plugin.getTownyUniverse();
			try {
				Resident resident = TownyUniverse.getDataSource().getResident(cell.getNameOfFlagOwner());
				Town town = resident.getTown();
				Nation nation = town.getNation();
				
				TownyWorld world = TownyUniverse.getDataSource().getWorld(cell.getWorldName());
				WorldCoord worldCoord = new WorldCoord(world, cell.getX(), cell.getZ());
				universe.removeWarZone(worldCoord);
				
				TownBlock townBlock = worldCoord.getTownBlock();
				TownyUniverse.getDataSource().removeTownBlock(townBlock);
				
				try {
					List<WorldCoord> selection = new ArrayList<WorldCoord>();
					selection.add(worldCoord);
					TownCommand.checkIfSelectionIsValid(town, selection, false, 0, false);
					new TownClaim(plugin, null, town, selection, false, true, false).start();
					
					//TownCommand.townClaim(town, worldCoord);
					//TownyUniverse.getDataSource().saveTown(town);
					//TownyUniverse.getDataSource().saveWorld(world);
					
					//TODO
					//PlotCommand.plotClaim(resident, worldCoord);
					//TownyUniverse.getDataSource().saveResident(resident);
					//TownyUniverse.getDataSource().saveWorld(world);
				} catch (TownyException te) {
					// Couldn't claim it.
				}
				
				plugin.updateCache(worldCoord);
				
				TownyMessaging.sendGlobalMessage(String.format(TownySettings.getLangString("msg_enemy_war_area_won"),
						resident.getFormattedName(),
						(nation.hasTag() ? nation.getTag() : nation.getFormattedName()),
						cell.getCellString()));
			} catch (NotRegisteredException e) {
				e.printStackTrace();
			}
			
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onCellAttackCanceledEvent(CellAttackCanceledEvent event) {
		
			CellUnderAttack cell = event.getCell();
			
			TownyUniverse universe = plugin.getTownyUniverse();
			try {
				TownyWorld world = TownyUniverse.getDataSource().getWorld(cell.getWorldName());
				WorldCoord worldCoord = new WorldCoord(world, cell.getX(), cell.getZ());
				universe.removeWarZone(worldCoord);
				plugin.updateCache(worldCoord);
			} catch (NotRegisteredException e) {
				e.printStackTrace();
			}
			System.out.println(cell.getCellString());
		}
	}

