package io.github.jbaero.chlb.functions;

import com.laytonsmith.PureUtilities.Common.StringUtils;
import com.laytonsmith.PureUtilities.SimpleVersion;
import com.laytonsmith.PureUtilities.Version;
import com.laytonsmith.annotations.api;
import com.laytonsmith.core.constructs.CArray;
import com.laytonsmith.core.constructs.CString;
import com.laytonsmith.core.constructs.Target;
import com.laytonsmith.core.environments.Environment;
import com.laytonsmith.core.exceptions.CRE.CREFormatException;
import com.laytonsmith.core.exceptions.CRE.CREIOException;
import com.laytonsmith.core.exceptions.CRE.CREInvalidPluginException;
import com.laytonsmith.core.exceptions.CRE.CREPlayerOfflineException;
import com.laytonsmith.core.exceptions.CRE.CREThrowable;
import com.laytonsmith.core.exceptions.ConfigRuntimeException;
import com.laytonsmith.core.functions.AbstractFunction;
import com.laytonsmith.core.natives.interfaces.Mixed;
import de.diddiz.LogBlock.BlockChange;
import de.diddiz.LogBlock.Consumer;
import de.diddiz.LogBlock.LogBlock;
import de.diddiz.LogBlock.QueryParams;
import io.github.jbaero.chlb.CHLB;
import io.github.jbaero.chlb.LBColumns;
import io.github.jbaero.chlb.LBOG;

public class LBQueries {

	public static String docs() {
		return "Functions for getting queries from LogBlock.";
	}
	
	public static Consumer getConsumer(Target t){
		Consumer cons = CHLB.getConsumer();
		if (cons != null) {
			return cons;
		} else {
			throw new CREInvalidPluginException("LogBlock is not properly loaded!", t);
		}
	}
	
	public static LogBlock getLB(Target t) {
		LogBlock lb = CHLB.getLB();
		if (lb != null) {
			return lb;
		} else {
			throw new  CREInvalidPluginException("LogBlock is not properly loaded!", t);
		}
	}
	
	@api
	public static class lb_get_changes extends AbstractFunction {

		public Class<? extends CREThrowable>[] thrown() {
			return new Class[]{CREIOException.class, CREPlayerOfflineException.class,
					CREFormatException.class, CREInvalidPluginException.class};
		}

		public boolean isRestricted() {
			return true;
		}

		public Boolean runAsync() {
			return false;
		}

		public Mixed exec(Target t, Environment environment, Mixed... args) throws ConfigRuntimeException {
			LogBlock lb = getLB(t);
			QueryParams qp = LBOG.GetGenerator().query(args[0], lb, t);
			java.util.List<BlockChange> bcl;
			CArray ret = new CArray(t);
			try {
				bcl = lb.getBlockChanges(qp);
			} catch (java.sql.SQLException e) {
				throw new CREIOException("Could not read database!", t);
			}
			for (BlockChange bc : bcl) {
				ret.push(new CString(bc.toString(), t), t);
			}
			return ret;
		}

		public String getName() {
			return "lb_get_changes";
		}

		public Integer[] numArgs() {
			return new Integer[]{1};
		}

		public String docs() {
			return "array {array} Takes an associative array of parameters to query the database with,"
					+ " and returns an array of the matching changes. Block changes are in the form of"
					+ " strings pre-formatted by LogBlock. The 'info' parameter allows you to specify columns"
					+ " to include data from. Columns available are "
					+ StringUtils.Join(LBColumns.values(), ", ", ", and ") + "."
					+ " The 'changetype' parameter can be ALL, BOTH, CHESTACCESS, CREATED, DESTROYED, CHAT, KILLS,"
					+ " ENTITIES, ENTITIES_CREATED, or ENTITIES_KILLED."
					+ " The 'location' (with optional 'radius') can be included, or alternatively a 'sel' value"
					+ " that can be either a pair of location corners or null to use the current WorldEdit selection."
					+ " The other available parameters are 'players', 'world', 'since', 'before', and 'limit'.";
		}

		@Override
		public Version since() {
			return new SimpleVersion(0, 1, 0);
		}
	}
}
