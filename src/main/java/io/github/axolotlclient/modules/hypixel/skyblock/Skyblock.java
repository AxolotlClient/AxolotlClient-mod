package io.github.axolotlclient.modules.hypixel.skyblock;

import com.mojang.blaze3d.platform.InputUtil;
import io.github.axolotlclient.AxolotlclientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlclientConfig.options.KeyBindOption;
import io.github.axolotlclient.AxolotlclientConfig.options.OptionCategory;
import io.github.axolotlclient.modules.hypixel.AbstractHypixelMod;
import lombok.Getter;
import net.minecraft.client.option.KeyBind;

public class Skyblock implements AbstractHypixelMod {

    @Getter
    private final static Skyblock Instance = new Skyblock();

    private final OptionCategory category = new OptionCategory("skyblock");

    public final BooleanOption rotationLocked = new BooleanOption("rotationLocked", false);
    private final KeyBindOption lock = new KeyBindOption("lockRotation",
            new KeyBind("lockRotation", InputUtil.KEY_P_CODE, "category.axolotlclient"),
            keyBinding -> rotationLocked.toggle());

    @Override
    public void init() {
        category.add(rotationLocked, lock);
    }

    @Override
    public OptionCategory getCategory() {
        return category;
    }
}
