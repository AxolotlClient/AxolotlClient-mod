package io.github.moehreag.axolotlclient.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Tooltip;

@Config(name = "Axolotlclient")
public class AxolotlclientConfig implements ConfigData {

	@ConfigEntry.Gui.CollapsibleObject
	public nametagConf NametagConf = new nametagConf();
	public static class nametagConf {
		public boolean showOwnNametag = false;

		public boolean useShadows = false;
	}

	public boolean showBadge = true;

	@ConfigEntry.Gui.CollapsibleObject
	public Badges badgeOptions = new Badges();
	public static class Badges {

		public boolean showChatBadge = true;

		public boolean CustomBadge = false;


		@Tooltip
		public String badgeText = "";
	}

	@Tooltip(count = 3)
	public String OwnName = "";

	@ConfigEntry.Gui.CollapsibleObject
	public rpcConfig RPCConfig = new rpcConfig();
	public static class rpcConfig {

		public boolean enableRPC = true;

		public boolean showActivity = true;
	}

}
