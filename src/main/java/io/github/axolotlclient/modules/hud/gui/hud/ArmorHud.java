package io.github.axolotlclient.modules.hud.gui.hud;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.AxolotlclientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlclientConfig.options.OptionBase;
import io.github.axolotlclient.modules.hud.gui.AbstractHudEntry;
import io.github.axolotlclient.modules.hud.util.DrawPosition;
import io.github.axolotlclient.modules.hud.util.ItemUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 * @license GPL-3.0
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

    ItemStack[] placeholders = new ItemStack[]{
            new ItemStack(Items.IRON_HELMET),
            new ItemStack(Items.IRON_CHESTPLATE),
            new ItemStack(Items.IRON_LEGGINGS),
            new ItemStack(Items.IRON_BOOTS),
            new ItemStack(Items.IRON_SWORD)
    };

    @Override
    public void renderPlaceholder() {
        renderPlaceholderBackground();
        scale();
        DrawPosition pos = getPos();
        int y = 2;
        for(ItemStack stack:placeholders) {
            ItemUtil.renderGuiItem(stack, pos.x, pos.y + y);
            y+=20;
        }
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
    public void addConfigOptions(List<OptionBase<?>> options) {
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
