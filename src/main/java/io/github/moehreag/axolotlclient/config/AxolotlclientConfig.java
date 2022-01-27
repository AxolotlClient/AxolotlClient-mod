package io.github.moehreag.axolotlclient.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Tooltip;

@Config(name = "Axolotlclient")
public class AxolotlclientConfig implements ConfigData {
	public boolean showOwnNametag = false;
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

}
