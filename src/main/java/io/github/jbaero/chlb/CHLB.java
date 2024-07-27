package io.github.jbaero.chlb;

import com.laytonsmith.PureUtilities.SimpleVersion;
import com.laytonsmith.PureUtilities.Version;
import com.laytonsmith.core.exceptions.CRE.CREInvalidPluginException;
import com.laytonsmith.core.extensions.AbstractExtension;
import com.laytonsmith.core.extensions.MSExtension;
import org.bukkit.plugin.Plugin;

import com.laytonsmith.commandhelper.CommandHelperPlugin;
import com.laytonsmith.core.Static;
import com.laytonsmith.core.constructs.Target;
import com.laytonsmith.core.exceptions.ConfigRuntimeException;

import de.diddiz.LogBlock.Consumer;
import de.diddiz.LogBlock.LogBlock;

@MSExtension("CHLogBlock")
public class CHLB extends AbstractExtension {
	private static LogBlock lb;
	private static Consumer cons;

	public Version getVersion()  {
		return new SimpleVersion(0,5,0);
	}

	@Override
	public void onStartup() {
		try {
			Static.checkPlugin("LogBlock", Target.UNKNOWN);
			Static.getLogger().info("CHLogBlock " + getVersion() + " loaded.");
		} catch (ConfigRuntimeException cre) {
			Static.getLogger().warning("LogBlock not found, CHLogblock cannot function!");
		}
	}

	@Override
	public void onShutdown() {
		Static.getLogger().info("CHLogBlock " + getVersion() + " unloaded.");
	}

	public static Consumer getConsumer() {
		if(cons == null) {
			cons = getLB().getConsumer();
		}
		return cons;
	}

	public static LogBlock getLB() {
		if(lb == null) {
			Plugin pl = CommandHelperPlugin.self.getServer().getPluginManager().getPlugin("LogBlock");
			if (pl instanceof LogBlock) {
				lb = (LogBlock) pl;
			} else {
				throw new CREInvalidPluginException("", Target.UNKNOWN);
			}
		}
		return lb;
	}
}
