package io.github.jbaero.chlb.functions;

import com.laytonsmith.PureUtilities.SimpleVersion;
import com.laytonsmith.PureUtilities.Version;
import com.laytonsmith.abstraction.MCLocation;
import com.laytonsmith.abstraction.blocks.MCBlockData;
import com.laytonsmith.abstraction.bukkit.BukkitMCLocation;
import com.laytonsmith.annotations.api;
import com.laytonsmith.core.ObjectGenerator;
import com.laytonsmith.core.Static;
import com.laytonsmith.core.constructs.CArray;
import com.laytonsmith.core.constructs.CVoid;
import com.laytonsmith.core.constructs.Target;
import com.laytonsmith.core.environments.Environment;
import com.laytonsmith.core.exceptions.CRE.CREFormatException;
import com.laytonsmith.core.exceptions.CRE.CREIllegalArgumentException;
import com.laytonsmith.core.exceptions.CRE.CREInvalidPluginException;
import com.laytonsmith.core.exceptions.CRE.CREPlayerOfflineException;
import com.laytonsmith.core.exceptions.CRE.CREThrowable;
import com.laytonsmith.core.exceptions.ConfigRuntimeException;
import com.laytonsmith.core.functions.AbstractFunction;
import com.laytonsmith.core.natives.interfaces.Mixed;
import de.diddiz.LogBlock.Actor;
import de.diddiz.LogBlock.Consumer;
import io.github.jbaero.chlb.CHLB;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;

public class LBLogging {
	
	public static String docs() {
		return "Functions for manually logging block changes in LogBlock.";
	}
	
	private static Consumer getConsumer(Target t){
		Consumer cons = CHLB.getConsumer();
		if (cons != null) {
			return cons;
		} else {
			throw new CREInvalidPluginException("LogBlock is not properly loaded!", t);
		}
	}

	@api
	public static class lb_log_break extends AbstractFunction {

		public Class<? extends CREThrowable>[] thrown() {
			return new Class[]{CREInvalidPluginException.class, CREPlayerOfflineException.class, CREFormatException.class};
		}

		public boolean isRestricted() {
			return true;
		}

		public Boolean runAsync() {
			return false;
		}

		public Mixed exec(Target t, Environment environment, Mixed... args) throws ConfigRuntimeException {
			Static.checkPlugin("LogBlock", t);
			Actor actor = new Actor(args[0].val());
			MCLocation loc = ObjectGenerator.GetGenerator().location(args[1], null, t);
			if (args.length == 3) {
				MCBlockData bd;
				try {
					bd = Static.getServer().createBlockData(args[1].val());
				} catch (IllegalArgumentException iae) {
					throw new CREIllegalArgumentException("Cannot create block data from string: " + args[1].val(), t);
				}
				getConsumer(t).queueBlockBreak(actor, ((BukkitMCLocation) loc).asLocation(), (BlockData) bd.getHandle());
			} else {
				getConsumer(t).queueBlockBreak(actor, ((BukkitMCLocation) loc).asLocation(),
						(BlockData) loc.getBlock().getBlockData().getHandle());
			}
			return CVoid.VOID;
		}

		public String getName() {
			return "lb_log_break";
		}

		public Integer[] numArgs() {
			return new  Integer[]{2, 3};
		}

		public String docs() {
			return "void {player, locationArray, [blockDataString]} Manually logs a block breaking at a location."
					+ " You can choose to specify the BlockData of the block being broken.";
		}

		@Override
		public Version since() {
			return new SimpleVersion(0, 1, 0);
		}
	}

	@api
	public static class lb_log_break_sign extends AbstractFunction {

		public Class<? extends CREThrowable>[] thrown() {
			return new Class[]{CREInvalidPluginException.class, CREIllegalArgumentException.class, CREFormatException.class};
		}

		public boolean isRestricted() {
			return true;
		}

		public Boolean runAsync() {
			return false;
		}

		public Mixed exec(Target t, Environment environment, Mixed... args) throws ConfigRuntimeException {
			Static.checkPlugin("LogBlock", t);
			String p = args[0].val();
			MCLocation loc = ObjectGenerator.GetGenerator().location(args[1], null, t);
			BlockState blockState = ((BukkitMCLocation) loc).asLocation().getBlock().getState();
			if(!(blockState instanceof Sign)) {
				throw new CREIllegalArgumentException("The location specified is not a sign.", t);
			}
			Sign sign = (Sign) blockState;
			if(args.length >= 3) {
				SignSide front = sign.getSide(Side.FRONT);
				if(!(args[2] instanceof CArray)) {
					throw new CREFormatException("Expected an array of strings for the front side of sign", t);
				}
				CArray frontLines = ((CArray) args[2]);
				for (int i = 0; i < 4; i++) {
					if(frontLines.containsKey(i)) {
						front.setLine(i, frontLines.get(i, t).val());
					}
				}
				if(args.length == 4) {
					SignSide back = sign.getSide(Side.BACK);
					if(!(args[3] instanceof CArray)) {
						throw new CREFormatException("Expected an array of strings for the back side of sign", t);
					}
					CArray backLines = ((CArray) args[3]);
					for (int i = 0; i < 4; i++) {
						if(backLines.containsKey(i)) {
							back.setLine(i, backLines.get(i, t).val());
						}
					}
				}
			}
			getConsumer(t).queueBlockBreak(new Actor(p), sign);
			return CVoid.VOID;
		}

		public String getName() {
			return "lb_log_break_sign";
		}

		public Integer[] numArgs() {
			return new  Integer[]{2, 3, 4};
		}

		public String docs() {
			return "void {playerName, locationArray, [frontLines], [backLines]} Manually logs a sign being broken at a location."
					+ " The third and fourth params are arrays of lines for the front and back sides of the sign,"
					+ " defaulting to current lines on the sign."
					+ " Note 1: An error will be thrown if the specified location is not a sign."
					+ " Note 2: Specifying lines will update the physical sign as well.";
		}

		@Override
		public Version since() {
			return new SimpleVersion(0, 1, 0);
		}
	}

	@api
	public static class lb_log_place extends AbstractFunction {

		public Class<? extends CREThrowable>[] thrown() {
			return new Class[]{CREInvalidPluginException.class, CREIllegalArgumentException.class, CREFormatException.class};
		}

		public boolean isRestricted() {
			return true;
		}

		public Boolean runAsync() {
			return false;
		}

		public Mixed exec(Target t, Environment environment, Mixed... args) throws ConfigRuntimeException {
			Static.checkPlugin("LogBlock", t);
			String p = args[0].val();
			MCLocation loc = ObjectGenerator.GetGenerator().location(args[1], null, t);
			if (args.length == 3) {
				MCBlockData bd;
				try {
					bd = Static.getServer().createBlockData(args[1].val());
				} catch (IllegalArgumentException iae) {
					throw new CREIllegalArgumentException("Cannot create block data from string: " + args[1].val(), t);
				}
				getConsumer(t).queueBlockPlace(new Actor(p), ((BukkitMCLocation) loc).asLocation(), (BlockData) bd.getHandle());
			} else {
				getConsumer(t).queueBlockPlace(new Actor(p), ((BukkitMCLocation) loc).asLocation(),
						(BlockData) loc.getBlock().getBlockData().getHandle());
			}
			return CVoid.VOID;
		}

		public String getName() {
			return "lb_log_place";
		}

		public Integer[] numArgs() {
			return new  Integer[]{2, 3};
		}

		public String docs() {
			return "void {player, locationArray, [blockDataString]} Manually logs a block being placed at a location."
					+ " You can choose to specify the BlockData of the block being placed.";
		}

		@Override
		public Version since() {
			return new SimpleVersion(0, 1, 0);
		}
	}

	@api
	public static class lb_log_place_sign extends AbstractFunction {

		public Class<? extends CREThrowable>[] thrown() {
			return new Class[]{CREInvalidPluginException.class, CREIllegalArgumentException.class, CREFormatException.class};
		}

		public boolean isRestricted() {
			return true;
		}

		public Boolean runAsync() {
			return false;
		}

		public Mixed exec(Target t, Environment environment, Mixed... args) throws ConfigRuntimeException {
			Static.checkPlugin("LogBlock", t);
			String p = args[0].val();
			MCLocation loc = ObjectGenerator.GetGenerator().location(args[1], null, t);
			Block block = ((BukkitMCLocation) loc).asLocation().getBlock();
			BlockState blockState = block.getState();
			if(!(blockState instanceof Sign)) {
				throw new CREIllegalArgumentException("The location specified is not a sign.", t);
			}
			Sign sign = (Sign) blockState;
			if(args.length >= 3) {
				SignSide front = sign.getSide(Side.FRONT);
				if(!(args[2] instanceof CArray)) {
					throw new CREFormatException("Expected an array of strings for the front side of sign", t);
				}
				CArray frontLines = ((CArray) args[2]);
				for (int i = 0; i < 4; i++) {
					if(frontLines.containsKey(i)) {
						front.setLine(i, frontLines.get(i, t).val());
					}
				}
				if(args.length == 4) {
					SignSide back = sign.getSide(Side.BACK);
					if(!(args[3] instanceof CArray)) {
						throw new CREFormatException("Expected an array of strings for the back side of sign", t);
					}
					CArray backLines = ((CArray) args[3]);
					for (int i = 0; i < 4; i++) {
						if(backLines.containsKey(i)) {
							back.setLine(i, backLines.get(i, t).val());
						}
					}
				}
			}
			getConsumer(t).queueBlockPlace(new Actor(p), sign);
			return CVoid.VOID;
		}

		public String getName() {
			return "lb_log_place_sign";
		}

		public Integer[] numArgs() {
			return new  Integer[]{2, 3, 4};
		}

		public String docs() {
			return "void {playerName, locationArray, [frontLines], [backLines]} Manually logs a sign being placed at a location."
					+ " The third and fourth params are arrays of lines for the front and back sides of the sign,"
					+ " defaulting to current lines on the sign."
					+ " Note 1: An error will be thrown if the specified location is not a sign."
					+ " Note 2: Specifying lines will update the physical sign as well.";
		}

		@Override
		public Version since() {
			return new SimpleVersion(0, 1, 0);
		}
	}
}
