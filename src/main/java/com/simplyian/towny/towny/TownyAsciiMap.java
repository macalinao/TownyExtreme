package com.simplyian.towny.towny;

import com.simplyian.towny.towny.exception.TownyException;
import com.simplyian.towny.towny.exception.NotRegisteredException;
import org.bukkit.entity.Player;

import com.simplyian.towny.towny.object.Coord;
import com.simplyian.towny.towny.object.Nation;
import com.simplyian.towny.towny.object.Resident;
import com.simplyian.towny.towny.object.TownBlock;
import com.simplyian.towny.towny.object.TownBlockType;
import com.simplyian.towny.towny.object.TownyUniverse;
import com.simplyian.towny.towny.object.TownyWorld;
import com.simplyian.towny.util.ChatTools;
import com.simplyian.towny.util.Colors;
import com.simplyian.towny.util.Compass;

public class TownyAsciiMap {
	public static final int lineWidth = 27;
	public static final int halfLineWidth = lineWidth / 2;
	public static final String[] help = {
			"  " + Colors.Gray + "-" + Colors.LightGray + " = Unclaimed",
			"  " + Colors.White + "+" + Colors.LightGray + " = Claimed",
			"  " + Colors.White + "$" + Colors.LightGray + " = For sale",
			"  " + Colors.LightGreen + "+" + Colors.LightGray + " = Your town",
			"  " + Colors.Yellow + "+" + Colors.LightGray + " = Your plot",
			"  " + Colors.Green + "+" + Colors.LightGray + " = Ally",
			"  " + Colors.Red + "+" + Colors.LightGray + " = Enemy"
	};
	
	public static String[] generateCompass(Player player) {
		Compass.Point dir = Compass.getCompassPointForDirection(player.getLocation().getYaw());
		
		return new String[]{
				Colors.Black + "  -----  ",
				Colors.Black + "  -" + (dir == Compass.Point.NW ? Colors.Gold + "\\" : "-")
				+ (dir == Compass.Point.N ? Colors.Gold : Colors.White) + "N"
				+ (dir == Compass.Point.NE ? Colors.Gold + "/" + Colors.Black : Colors.Black + "-") + "-  ",
				Colors.Black + "  -" + (dir == Compass.Point.W ? Colors.Gold + "W" : Colors.White + "W") + Colors.LightGray + "+"
				+ (dir == Compass.Point.E ? Colors.Gold : Colors.White) + "E" + Colors.Black  + "-  ",
				Colors.Black + "  -" + (dir == Compass.Point.SW ? Colors.Gold + "/" : "-")
				+ (dir == Compass.Point.S ? Colors.Gold : Colors.White) + "S"
				+ (dir == Compass.Point.SE ? Colors.Gold + "\\" + Colors.Black : Colors.Black + "-") + "-  "};
	}
	
	public static void generateAndSend(Towny plugin, Player player, int lineHeight) {
		// Collect Sample Data
		boolean hasTown = false;
		Resident resident;
		try {
			resident = TownyUniverse.getDataSource().getResident(player.getName());
			if (resident.hasTown())
				hasTown = true;
		} catch (TownyException x) {
			TownyMessaging.sendErrorMsg(player, x.getMessage());
			return;
		}

		TownyWorld world;
		try {
			world = TownyUniverse.getDataSource().getWorld(player.getWorld().getName());
		} catch (NotRegisteredException e1) {
			TownyMessaging.sendErrorMsg(player, "You are not in a registered world.");
			return;
		}
		if (!world.isUsingTowny()) {
			TownyMessaging.sendErrorMsg(player, "This world is not using towny.");
			return;
		}
		Coord pos = Coord.parseCoord(plugin.getCache(player).getLastLocation());

		// Generate Map 
		int halfLineHeight = lineHeight / 2;
		String[][] townyMap = new String[lineWidth][lineHeight];
		int x, y = 0;
		for (int tby = pos.getX() + (lineWidth-halfLineWidth-1); tby >= pos.getX() - halfLineWidth; tby--) {
			x = 0;
			for (int tbx = pos.getZ() - halfLineHeight; tbx <= pos.getZ() + (lineHeight-halfLineHeight-1); tbx++) {
				try {
					TownBlock townblock = world.getTownBlock(tby, tbx);
					//TODO: possibly claim outside of towns
					if (!townblock.hasTown())
						throw new TownyException();
					if (x == halfLineHeight && y == halfLineWidth)
						// location
						townyMap[y][x] = Colors.Gold;
					else if (hasTown) {
						if (resident.getTown() == townblock.getTown()) {
							// own town
							townyMap[y][x] = Colors.LightGreen;
							try {
								if (resident == townblock.getResident())
									//own plot
									townyMap[y][x] = Colors.Yellow;
							} catch(NotRegisteredException e) {
							}
						} else if (resident.hasNation()) {
							if (resident.getTown().getNation().hasTown(townblock.getTown()))
								// towns
								townyMap[y][x] = Colors.Green;
							else if (townblock.getTown().hasNation()) {
								Nation nation = resident.getTown().getNation();
								if (nation.hasAlly(townblock.getTown().getNation()))
									townyMap[y][x] = Colors.Green;
								else if (nation.hasEnemy(townblock.getTown().getNation()))
									// towns
									townyMap[y][x] = Colors.Red;
								else
									townyMap[y][x] = Colors.White;
							} else
								townyMap[y][x] = Colors.White;
						} else
							townyMap[y][x] = Colors.White;
					} else
						townyMap[y][x] = Colors.White;

					// Registered town block
					if (townblock.getPlotPrice() != -1) {
						// override the colour if it's a shop plot for sale
						if (townblock.getType().equals(TownBlockType.COMMERCIAL))
							townyMap[y][x] = Colors.Blue;
						townyMap[y][x] += "$";
					} else if (townblock.isHomeBlock())
						townyMap[y][x] += "H";
					else
						townyMap[y][x] += townblock.getType().getAsciiMapKey();
				} catch (TownyException e) {
					if (x == halfLineHeight && y == halfLineWidth)
						townyMap[y][x] = Colors.Gold;
					else
						townyMap[y][x] = Colors.Gray;

					// Unregistered town block
					townyMap[y][x] += "-";
				}
				x++;
			}
			y++;
		}
		
		String[] compass = generateCompass(player);
		
		// Output
		player.sendMessage(ChatTools.formatTitle("Towny Map " + Colors.White + "(" + pos.toString() + ")"));
		String line;
		int lineCount = 0;
		// Variables have been rotated to fit N/S/E/W properly
		for (int my = 0; my < lineHeight; my++) {
			line = compass[0];
			if (lineCount < compass.length)
				line = compass[lineCount];

			for (int mx = lineWidth-1; mx >= 0; mx--)
				line += townyMap[mx][my];
			
			if (lineCount < help.length)
				line += help[lineCount];
			
			player.sendMessage(line);
			lineCount++;
		}
		
		// Current town block data
		try {
			TownBlock townblock = world.getTownBlock(pos);
			TownyMessaging.sendMsg(player, ("Town: " + (townblock.hasTown() ? townblock.getTown().getName() : "None") + " : "
					+ "Owner: " + (townblock.hasResident() ? townblock.getResident().getName() : "None")));
		} catch (TownyException e) {
			//plugin.sendErrorMsg(player, e.getError());
			// Send a blank line instead of an error, to keep the map position tidy.
			player.sendMessage ("");
		}
	}
}
