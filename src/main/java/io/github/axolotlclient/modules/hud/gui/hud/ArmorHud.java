package io.github.axolotlclient.modules.hud.gui.hud;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.config.options.BooleanOption;
import io.github.axolotlclient.config.options.Option;
import io.github.axolotlclient.modules.hud.gui.AbstractHudEntry;
import io.github.axolotlclient.modules.hud.util.DrawPosition;
import io.github.axolotlclient.modules.hud.util.ItemUtil;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * This implementation of Hud modules is based on KronHUD.
 * https://github.com/DarkKronicle/KronHUD
 * Licensed under GPL-3.0
 */

public class ArmorHud extends AbstractHudEntry {
    public static final Identifier ID = new Identifier("kronhud", "armorhud");

    protected BooleanOption showProtLvl = new BooleanOption("showProtectionLevel", false);

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
        if(outline.get()) outlineRect(getBounds(), outlineColor.get());
        int lastY = 2 + (4 * 20);
        if(client.player.inventory.getMainHandStack() !=null)
            ItemUtil.renderGuiItem(client.player.inventory.getMainHandStack().copy(), pos.x, pos.y + lastY);
        lastY = lastY - 20;
        for (int i = 0; i <= 3; i++) {
            if(client.player.inventory.armor[i]!=null) {
                ItemStack stack = client.player.inventory.armor[i].copy();
                if (showProtLvl.get() && stack.hasEnchantments()) {
                    NbtList nbtList = stack.getEnchantments();
                    if (nbtList != null) {
                        for (int k = 0; k < nbtList.size(); ++k) {
                            int enchantId = nbtList.getCompound(k).getShort("id");
                            int level = nbtList.getCompound(k).getShort("lvl");
                            if (enchantId == 0 && Enchantment.byRawId(enchantId) != null) {
                                stack.count = level;
                            }
                        }
                    }
                }

                ItemUtil.renderGuiItem(stack, pos.x, lastY + pos.y);
            }

            lastY = lastY - 20;
        }
        GlStateManager.popMatrix();
    }

    @Override
    public void renderPlaceholder() {
        renderPlaceholderBackground();
        scale();
        DrawPosition pos = getPos();
        ItemStack itemStack = new ItemStack(Block.getById(2), 90);
        ItemUtil.renderGuiItem(itemStack, pos.x, pos.y+82);
        GlStateManager.popMatrix();
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
        super.addConfigOptions(options);
        options.add(textColor);
        options.add(shadow);
        options.add(background);
        options.add(backgroundColor);
        options.add(outline);
        options.add(outlineColor);
        options.add(showProtLvl);
    }

}
