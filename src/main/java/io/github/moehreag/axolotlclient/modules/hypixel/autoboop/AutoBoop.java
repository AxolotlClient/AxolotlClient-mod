package io.github.moehreag.axolotlclient.modules.hypixel.autoboop;

import io.github.moehreag.axolotlclient.config.options.BooleanOption;
import io.github.moehreag.axolotlclient.config.options.OptionCategory;
import io.github.moehreag.axolotlclient.modules.hypixel.AbstractHypixelMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.unmapped.C_fijiyucq;


// Based on https://github.com/VeryHolyCheeeese/AutoBoop/blob/main/src/main/java/autoboop/AutoBoop.java
public class AutoBoop implements AbstractHypixelMod {

	public static AutoBoop Instance = new AutoBoop();

	protected OptionCategory cat = new OptionCategory("autoBoop");
	protected BooleanOption enabled = new BooleanOption("enabled", false);

	// I suppose this is something introduced with the chat cryptography features in 1.19
	private final C_fijiyucq whateverThisIs = new C_fijiyucq(MinecraftClient.getInstance());

	@Override
	public void init() {

		cat.add(enabled);
	}

	@Override
	public OptionCategory getCategory() {
		return cat;
	}

	public void onMessage(Text message){
		if(message.getString().contains("Friend >") && message.getString().contains("joined.")){
			String player = message.getString().substring(message.getString().indexOf(">") + 2, message.getString().lastIndexOf(" "));
			String command = "/boop "+player;
			Text text = this.whateverThisIs.method_44037(command);
			MinecraftClient.getInstance().player.method_44096(command, text);
		}
	}
}
