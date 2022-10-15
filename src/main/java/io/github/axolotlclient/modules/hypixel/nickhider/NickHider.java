package io.github.axolotlclient.modules.hypixel.nickhider;

import io.github.axolotlclient.AxolotlclientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlclientConfig.options.OptionCategory;
import io.github.axolotlclient.AxolotlclientConfig.options.StringOption;
import io.github.axolotlclient.modules.hypixel.AbstractHypixelMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class NickHider implements AbstractHypixelMod {

    public static NickHider Instance = new NickHider();

    private final OptionCategory category = new OptionCategory("nickhider");

    public StringOption hiddenNameSelf = new StringOption("hiddenNameSelf", "You");
    public StringOption hiddenNameOthers = new StringOption("hiddenNameOthers", "Player");
    public BooleanOption hideOwnName = new BooleanOption("hideOwnName", false);
    public BooleanOption hideOtherNames = new BooleanOption("hideOtherNames", false);
    public BooleanOption hideOwnSkin = new BooleanOption("hideOwnSkin", false);
    public BooleanOption hideOtherSkins = new BooleanOption("hideOtherSkins", false);

    @Override
    public void init() {
        category.add(hiddenNameSelf);
        category.add(hiddenNameOthers);
        category.add(hideOwnName);
        category.add(hideOtherNames);
        category.add(hideOwnSkin);
        category.add(hideOtherSkins);
    }

    public Text editMessage(Text message){

        if(hideOwnName.get() || hideOtherNames.get()) {
            String msg = message.asFormattedString();
            String playerName = MinecraftClient.getInstance().player.getGameProfile().getName();
            if (hideOwnName.get() && msg.contains(playerName)) {
                msg = msg.replaceAll(playerName, hiddenNameSelf.get());

            }

            if (hideOtherNames.get()) {
                for (PlayerEntity player : MinecraftClient.getInstance().world.playerEntities) {
                    if (msg.contains(player.getGameProfile().getName())) {
                        msg = msg.replaceAll(player.getGameProfile().getName(), hiddenNameOthers.get());
                    }
                }
            }


            return new LiteralText(msg).setStyle(message.getStyle().deepCopy());
        }
        return message;
    }

    @Override
    public OptionCategory getCategory() {
        return category;
    }
}
