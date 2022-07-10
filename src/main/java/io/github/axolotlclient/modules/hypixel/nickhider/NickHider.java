package io.github.axolotlclient.modules.hypixel.nickhider;

import io.github.axolotlclient.config.options.BooleanOption;
import io.github.axolotlclient.config.options.OptionCategory;
import io.github.axolotlclient.config.options.StringOption;
import io.github.axolotlclient.modules.hypixel.AbstractHypixelMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
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

    @Override
    public OptionCategory getCategory() {
        return category;
    }

    public Text editMessage(Text message){
        if(NickHider.Instance.hideOwnName.get() || NickHider.Instance.hideOtherNames.get()) {
            String msg = message.getString();

            String playerName = MinecraftClient.getInstance().player.getName().getString();
            if (NickHider.Instance.hideOwnName.get() && msg.contains(playerName)) {
                msg = msg.replaceAll(playerName, NickHider.Instance.hiddenNameSelf.get());

            }

            if (NickHider.Instance.hideOtherNames.get()) {
                for (AbstractClientPlayerEntity player : MinecraftClient.getInstance().world.getPlayers()) {
                    if (msg.contains(player.getName().getString())) {
                        msg = msg.replaceAll(player.getName().getString(), NickHider.Instance.hiddenNameOthers.get());
                    }
                }
            }

            return Text.literal(msg).copy().setStyle(message.getStyle());
        }
        return message;
    }
}
