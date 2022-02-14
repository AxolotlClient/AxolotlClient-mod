package io.github.moehreag.axolotlclient.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Tooltip;

@Config(name = "Axolotlclient")
public class AxolotlclientConfig implements ConfigData {

	@ConfigEntry.Category("Nametag Options")
	@ConfigEntry.Gui.TransitiveObject
	public nametagConf NametagConf = new nametagConf();
	public static class nametagConf {
		public boolean showOwnNametag = false;

		public boolean useShadows = false;
	}



	@ConfigEntry.Category("Badge Options")
	@ConfigEntry.Gui.TransitiveObject
	public Badges badgeOptions = new Badges();
	public static class Badges {
		public boolean showBadge = true;

		//public boolean showChatBadge = true;

		public boolean CustomBadge = false;


		@Tooltip
		public String badgeText = "";
	}

	@ConfigEntry.Category("NickHider Options")
	@ConfigEntry.Gui.TransitiveObject
	public nh NickHider = new nh();
	public static class nh {
		public boolean hideOwnName = false;
		public String OwnName = "";

		public boolean hideOtherNames = false;
		public String otherName = "Player";

		public boolean hideOwnSkin = false;
		public boolean hideOtherSkins = false;

	}

	@ConfigEntry.Category("Other")
	@ConfigEntry.Gui.TransitiveObject
	public other General = new other();
	public static class other {
		public boolean fullBed = false;
	}


	@ConfigEntry.Category("Discord RPC")
	@ConfigEntry.Gui.TransitiveObject
	public rpcConfig RPCConfig = new rpcConfig();
	public static class rpcConfig {

		public boolean enableRPC = true;

		public boolean showActivity = true;
	}

}
