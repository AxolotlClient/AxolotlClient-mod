package io.github.axolotlclient.modules.hypixel.autoboop;

import io.github.axolotlclient.AxolotlclientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlclientConfig.options.OptionCategory;
import io.github.axolotlclient.modules.hypixel.AbstractHypixelMod;
import io.github.axolotlclient.util.Util;
import net.minecraft.text.Text;

public class AutoBoop implements AbstractHypixelMod {
    public static AutoBoop Instance = new AutoBoop();

    protected OptionCategory cat = new OptionCategory("autoBoop");
    protected BooleanOption enabled = new BooleanOption("enabled", "autoBoop", false);

    @Override
    public void init() {

        cat.add(enabled);
    }

    @Override
    public OptionCategory getCategory() {
        return cat;
    }

    public void onMessage(Text message){
        if(enabled.get() && message.asUnformattedString().contains("Friend >") && message.asUnformattedString().contains("joined.")){
            String player = message.asUnformattedString().substring(message.asFormattedString().indexOf(">") + 2, message.asUnformattedString().lastIndexOf(" "));
            Util.sendChatMessage( "/boop "+player);
        }
    }
}
