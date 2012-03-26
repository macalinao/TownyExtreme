package com.simplyian.towny.towny.tasks;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;

import com.simplyian.towny.towny.exception.NotRegisteredException;
import com.simplyian.towny.towny.exception.TownyException;
import com.simplyian.towny.towny.TownyMessaging;
import com.simplyian.towny.towny.TownySettings;
import com.simplyian.towny.towny.object.Coord;
import com.simplyian.towny.towny.object.TownBlock;
import com.simplyian.towny.towny.object.TownyUniverse;
import com.simplyian.towny.towny.object.TownyWorld;
import com.simplyian.towny.util.JavaUtil;


public class MobRemovalTimerTask extends TownyTimerTask {
	private Server server;
	@SuppressWarnings("rawtypes")
	public static List<Class> worldMobsToRemove = new ArrayList<Class>();
	@SuppressWarnings("rawtypes")
	public static List<Class> townMobsToRemove = new ArrayList<Class>();
	
	@SuppressWarnings("rawtypes")
	public MobRemovalTimerTask(TownyUniverse universe, Server server) {
		super(universe);
		this.server = server;
		
		worldMobsToRemove.clear();
		for (String mob : TownySettings.getWorldMobRemovalEntities())
			if (!mob.equals(""))
				try {
					Class c = Class.forName("org.bukkit.entity."+mob);
					if (JavaUtil.isSubInterface(LivingEntity.class, c))
						worldMobsToRemove.add(c);
					else
						throw new Exception();
				} catch (ClassNotFoundException e) {
					TownyMessaging.sendErrorMsg("WorldMob: " + mob + " is not an acceptable class.");
				} catch (Exception e) {
					TownyMessaging.sendErrorMsg("WorldMob: " + mob + " is not an acceptable living entity.");
				}
		
		townMobsToRemove.clear();
		for (String mob : TownySettings.getTownMobRemovalEntities())
			if (!mob.equals(""))
				try {
					Class c = Class.forName("org.bukkit.entity."+mob);
					if (JavaUtil.isSubInterface(LivingEntity.class, c))
						townMobsToRemove.add(c);
					else
						throw new Exception();
				} catch (ClassNotFoundException e) {
					TownyMessaging.sendErrorMsg("TownMob: " + mob + " is not an acceptable class.");
				} catch (Exception e) {
					TownyMessaging.sendErrorMsg("TownMob: " + mob + " is not an acceptable living entity.");
				}
	}
	
	
	@SuppressWarnings("rawtypes")
	public static boolean isRemovingWorldEntity(LivingEntity livingEntity) {
		for (Class c : worldMobsToRemove)
			if (c.isInstance(livingEntity))
				return true;
			else if (c.getName().contains(livingEntity.toString()))
				System.out.print(livingEntity.toString());
		return false;
	}
	
	@SuppressWarnings("rawtypes")
	public static boolean isRemovingTownEntity(LivingEntity livingEntity) {
		for (Class c : townMobsToRemove)
			if (c.isInstance(livingEntity))
				return true;
			else if (c.getName().contains(livingEntity.toString()))
				System.out.print(livingEntity.toString());
		return false;
	}
	
	
	@Override
	public void run() {
		//int numRemoved = 0;
		//int livingEntities = 0;
		
		/* OLD METHOD
		for (World world : server.getWorlds()) {
			List<LivingEntity> worldLivingEntities = new ArrayList<LivingEntity>(world.getLivingEntities());
			livingEntities += worldLivingEntities.size();
			for (LivingEntity livingEntity : worldLivingEntities)
				if (isRemovingEntity(livingEntity)) {
					Location loc = livingEntity.getLocation();
					Coord coord = Coord.parseCoord(loc);
					try {
						TownyWorld townyWorld = universe.getWorld(world.getName());
						TownBlock townBlock = townyWorld.getTownBlock(coord);
						if (!townBlock.getTown().hasMobs()) {
							//universe.getPlugin().sendDebugMsg("MobRemoval Removed: " + livingEntity.toString());
							livingEntity.teleportTo(new Location(world, loc.getX(), -50, loc.getZ()));
							numRemoved++;
						}
					} catch (TownyException x) {
					}
				}
			//universe.getPlugin().sendDebugMsg(world.getName() + ": " + StringMgmt.join(worldLivingEntities));
		}
		//universe.getPlugin().sendDebugMsg("MobRemoval (Removed: "+numRemoved+") (Total Living: "+livingEntities+")");
		*/
		
		//System.out.println("[Towny] MobRemovalTimerTask - run()");
		
		//boolean isRemovingWorldMobs = TownySettings.isRemovingWorldMobs();
		//boolean isRemovingTownMobs = TownySettings.isRemovingTownMobs();
		
		// Build a list of mobs to be removed
		//if (isRemovingTownMobs || isRemovingWorldMobs)
			for (World world : server.getWorlds()) {
				List<LivingEntity> livingEntitiesToRemove = new ArrayList<LivingEntity>();
				
				for (LivingEntity livingEntity : world.getLivingEntities()) {
					Coord coord = Coord.parseCoord(livingEntity.getLocation());
					TownyWorld townyWorld = null;
					try {
						townyWorld = TownyUniverse.getDataSource().getWorld(world.getName());
					} catch (NotRegisteredException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
						try {
							TownBlock townBlock = townyWorld.getTownBlock(coord);
							if ((!townBlock.getTown().hasMobs() && !townBlock.getPermissions().mobs && isRemovingTownEntity(livingEntity))) {
								//System.out.println("[Towny] Town MobRemovalTimerTask - added: " + livingEntity.toString());
								livingEntitiesToRemove.add(livingEntity);
							}
								
						} catch (TownyException x) {
							// it will fall through here if the mob has no townblock.
							if ((!townyWorld.hasWorldMobs() && isRemovingWorldEntity(livingEntity))) {
								//System.out.println("[Towny] World MobRemovalTimerTask - added: " + livingEntity.toString());
								livingEntitiesToRemove.add(livingEntity);
							}
						}
				}
					

				for (LivingEntity livingEntity : livingEntitiesToRemove) {
					TownyMessaging.sendDebugMsg("MobRemoval Removed: " + livingEntity.toString());
					//livingEntity.teleportTo(new Location(world, livingEntity.getLocation().getX(), -50, livingEntity.getLocation().getZ()));
					livingEntity.remove();
					//numRemoved++;
				}
				
				//universe.getPlugin().sendDebugMsg(world.getName() + ": " + StringMgmt.join(worldLivingEntities));

			}
			
	}
}
