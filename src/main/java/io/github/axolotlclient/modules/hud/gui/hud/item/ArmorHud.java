package io.github.axolotlclient.modules.hud.gui.hud.item;

import io.github.axolotlclient.AxolotlclientConfig.options.OptionBase;
import io.github.axolotlclient.modules.hud.gui.entry.TextHudEntry;
import io.github.axolotlclient.modules.hud.util.DrawPosition;
import io.github.axolotlclient.modules.hud.util.ItemUtil;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 * @license GPL-3.0
 */

public class ArmorHud extends TextHudEntry {

    public static final Identifier ID = new Identifier("kronhud", "armorhud");

    public ArmorHud() {
        super(20, 100, true);
    }

    @Override
    public void renderComponent(float delta) {
        DrawPosition pos = getPos();
        int lastY = 2 + (4 * 20);
        renderMainItem(client.player.inventory.getMainHandStack(), pos.x() + 2, pos.y() + lastY);
        lastY = lastY - 20;
        for (int i = 0; i <= 3; i++) {
            ItemStack item = client.player.inventory.armor[i];
            renderItem(item, pos.x() + 2, lastY + pos.y());
            lastY = lastY - 20;
        }
    }

    public void renderItem(ItemStack stack, int x, int y) {
        ItemUtil.renderGuiItemModel(stack, x, y);
        ItemUtil.renderGuiItemOverlay(client.textRenderer, stack, x, y, null, textColor.get().getAsInt(), shadow.get());
    }

    public void renderMainItem(ItemStack stack, int x, int y) {
        ItemUtil.renderGuiItemModel(stack, x, y);
        String total = String.valueOf(ItemUtil.getTotal(client, stack));
        if (total.equals("1")) {
            total = null;
        }
        ItemUtil.renderGuiItemOverlay(client.textRenderer, stack, x, y, total, textColor.get().getAsInt(), shadow.get());
    }

    private final ItemStack[] placeholderStacks = new ItemStack[]{
            new ItemStack(Items.IRON_BOOTS),
            new ItemStack(Items.IRON_LEGGINGS),
            new ItemStack(Items.IRON_CHESTPLATE),
            new ItemStack(Items.IRON_HELMET),
            new ItemStack(Items.IRON_SWORD)
    };

    @Override
    public void renderPlaceholderComponent(float delta) {
        DrawPosition pos = getPos();
        int lastY = 2 + (4 * 20);
        renderItem(placeholderStacks[4], pos.x() + 2, pos.y() + lastY);
        lastY = lastY - 20;
        for (int i = 0; i <= 3; i++) {
            ItemStack item = placeholderStacks[i];
            renderItem(item, pos.x() + 2, lastY + pos.y());
            lastY = lastY - 20;
        }
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
    public List<OptionBase<?>> getConfigurationOptions() {
        return super.getConfigurationOptions();
    }

}
