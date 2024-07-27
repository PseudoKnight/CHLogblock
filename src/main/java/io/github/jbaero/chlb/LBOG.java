package io.github.jbaero.chlb;

import com.laytonsmith.abstraction.MCLocation;
import com.laytonsmith.abstraction.MCPlayer;
import com.laytonsmith.abstraction.bukkit.BukkitMCLocation;
import com.laytonsmith.abstraction.bukkit.BukkitMCWorld;
import com.laytonsmith.abstraction.bukkit.entities.BukkitMCPlayer;
import com.laytonsmith.core.ArgumentValidation;
import com.laytonsmith.core.ObjectGenerator;
import com.laytonsmith.core.Static;
import com.laytonsmith.core.constructs.CArray;
import com.laytonsmith.core.constructs.Target;
import com.laytonsmith.core.exceptions.CRE.CREFormatException;
import com.laytonsmith.core.natives.interfaces.Mixed;
import de.diddiz.LogBlock.LogBlock;
import de.diddiz.LogBlock.QueryParams;
import de.diddiz.LogBlock.QueryParams.BlockChangeType;
import de.diddiz.util.CuboidRegion;
import de.diddiz.worldedit.WorldEditHelper;

import java.util.ArrayList;
import java.util.List;

public class LBOG {

	private static LBOG lbog = null;

	public static LBOG GetGenerator() {
		if (lbog == null) {
			lbog = new LBOG();
		}
		return lbog;
	}
    
	public QueryParams query(Mixed c, LogBlock lb, Target t) {
		QueryParams queryParams = new QueryParams(lb);
		if (!(c instanceof CArray)) {
			throw new CREFormatException("Expected an associative array but received " + c, t);
		}
		CArray argParams = (CArray) c;
		if(!argParams.isAssociative()) {
			throw new CREFormatException("Parameter array must be associative.", t);
		}
		MCLocation corner1, corner2;
		CuboidRegion worldEditSelection;
		queryParams.merge(columns(queryParams, argParams.get("info", t), t));
		if (argParams.containsKey("changetype")) {
			try {
				queryParams.bct = BlockChangeType.valueOf(argParams.get("changetype", t).val());
			} catch (IllegalArgumentException iae) {
				throw new CREFormatException("Applicable changetypes: " + BlockChangeType.values().toString(), t);
			}
		}
		if (argParams.containsKey("location")) {
			corner1 = ObjectGenerator.GetGenerator().location(argParams.get("location", t), null, t);
			queryParams.setLocation(((BukkitMCLocation) corner1).asLocation());
			if (argParams.containsKey("radius")) {
				queryParams.radius = ArgumentValidation.getInt32(argParams.get("radius", t), t);
			}
		} else if (argParams.containsKey("sel")) {
			Mixed sel = argParams.get("sel", t);
			if (sel instanceof CArray) {
				CArray ca = (CArray) sel;
				if(ca.size() < 2) {
					throw new CREFormatException("Expected 2 location arrays for sel", t);
				}
				corner1 = ObjectGenerator.GetGenerator().location(ca.get(0, t), null, t);
				corner2 = ObjectGenerator.GetGenerator().location(ca.get(1, t), null, t);
				worldEditSelection = CuboidRegion.fromCorners(((BukkitMCWorld) corner1.getWorld()).__World(),
						((BukkitMCLocation) corner1).asLocation(), ((BukkitMCLocation) corner2).asLocation());
				queryParams.setSelection(worldEditSelection);
			} else {
				MCPlayer player = Static.GetPlayer(argParams.get("sel", t), t);
				worldEditSelection = WorldEditHelper.getSelectedRegion(((BukkitMCPlayer) player)._Player());
				queryParams.setSelection(worldEditSelection);
			}
		}
		if (argParams.containsKey("players")) {
			List<String> plist = new ArrayList<String>();
			if (argParams.get("players", t) instanceof CArray) {
				for (int i=0; i < ((CArray) argParams.get("players", t)).size(); i++) {
					plist.add(((CArray) argParams.get("players", t)).get(i, t).val());
				}
			} else {
				plist.add(argParams.get("players", t).val());
			}
			queryParams.players = plist;
		}
		if (argParams.containsKey("world")) {
			queryParams.world = ((BukkitMCWorld) Static.getServer().getWorld(argParams.get("world", t).val())).__World();
		}
		if (argParams.containsKey("since")) {
			queryParams.since = ArgumentValidation.getInt32(argParams.get("since", t), t);
		}
		if (argParams.containsKey("before")) {
			queryParams.before = ArgumentValidation.getInt32(argParams.get("before", t), t);
		}
		if (argParams.containsKey("limit")) {
			queryParams.limit = ArgumentValidation.getInt32(argParams.get("limit", t), t);
		}
		return queryParams;
	}

	public QueryParams columns(QueryParams queryParams, Mixed col, Target t) {
		if (!(col instanceof CArray)) {
			throw new CREFormatException("Info must be an array of columns to request.", t);
		}
		CArray columnsArray = (CArray) col;
		if(columnsArray.isAssociative()) {
			throw new CREFormatException("Info array must not be associative.", t);
		}
		if(columnsArray.isEmpty()) {
			throw new CREFormatException("Info array must not be empty.", t);
		}
		for (int i = 0; i < columnsArray.size(); i++) {
			LBColumns lbc;
			try {
				lbc = LBColumns.valueOf(columnsArray.get(i, t).val().toUpperCase());
			} catch (IllegalArgumentException ex) {
				continue;
			}
			switch (lbc) {
				case CHESTACCESS:
					queryParams.needChestAccess = true;
					break;
				case COORDS:
					queryParams.needCoords = true;
					break;
				case COUNT:
					queryParams.needCount = true;
					break;
				case DATA:
					queryParams.needData = true;
					break;
				case DATE:
					queryParams.needDate = true;
					break;
				case ID:
					queryParams.needId = true;
					break;
				case KILLER:
					queryParams.needKiller = true;
					break;
				case MESSAGE:
					queryParams.needMessage = true;
					break;
				case PLAYER:
					queryParams.needPlayer = true;
					break;
				case TYPE:
					queryParams.needType = true;
					break;
				case VICTIM:
					queryParams.needVictim = true;
					break;
				case WEAPON:
					queryParams.needWeapon = true;
					break;
			}
		}
		return queryParams;
	}
}
