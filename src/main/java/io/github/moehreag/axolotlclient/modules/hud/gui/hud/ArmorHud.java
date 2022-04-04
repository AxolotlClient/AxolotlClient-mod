package io.github.moehreag.axolotlclient.modules.hud.gui.hud;

import io.github.moehreag.axolotlclient.config.options.Option;
import io.github.moehreag.axolotlclient.modules.hud.gui.AbstractHudEntry;
import io.github.moehreag.axolotlclient.modules.hud.util.DrawPosition;
import io.github.moehreag.axolotlclient.modules.hud.util.ItemUtil;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * This implementation of Hud modules is based on KronHUD.
 * https://github.com/DarkKronicle/KronHUD
 * Licensed under GPL-3.0
 */

public class ArmorHud extends AbstractHudEntry {
    public static final Identifier ID = new Identifier("kronhud", "armorhud");

    public ArmorHud() {
        super(20, 100);
    }

    @Override
    public void render() {
        scale();
        DrawPosition pos = getPos();
        if (background.get()) {
            fillRect(getBounds(),
                    backgroundColor.get());
        }
        int lastY = 2 + (4 * 20);
        if(client.player.inventory.getMainHandStack() !=null)
            ItemUtil.renderGuiItem(client.player.inventory.getMainHandStack(), pos.x, pos.y + lastY);
        lastY = lastY - 20;
        for (int i = 0; i <= 3; i++) {
            if(client.player.inventory.armor[i] != null)
                ItemUtil.renderGuiItem(client.player.inventory.armor[i], pos.x , lastY + pos.y);
            lastY = lastY - 20;
        }
    }

    @Override
    public void renderPlaceholder() {
        renderPlaceholderBackground();
        scale();
        DrawPosition pos = getPos();
        int lastY = 2 + (4 * 20);
        ItemStack itemStack = new ItemStack(Block.getById(2), 90);
        ItemUtil.renderGuiItem(itemStack, pos.x, pos.y+lastY);
        hovered = false;
    }

    @Override
    public boolean movable() {
        return true;
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public void addConfigOptions(List<Option> options) {
        //super.addConfigOptions(options);
        options.add(enabled);
        options.add(shadow);
        options.add(background);
        options.add(backgroundColor);
    }

}
