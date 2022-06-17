package io.github.axolotlclient.modules.hud.gui.hud;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.config.Color;
import io.github.axolotlclient.config.options.IntegerOption;
import io.github.axolotlclient.config.options.Option;
import io.github.axolotlclient.modules.hud.gui.AbstractHudEntry;
import io.github.axolotlclient.modules.hud.util.DrawPosition;
import io.github.axolotlclient.modules.hud.util.ItemUtil;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This implementation of Hud modules is based on KronHUD.
 * https://github.com/DarkKronicle/KronHUD
 * Licensed under GPL-3.0
 */

public class ItemUpdateHud extends AbstractHudEntry {
    public static final Identifier ID = new Identifier("kronhud", "itemupdatehud");

    private List<ItemUtil.ItemStorage> oldItems = new ArrayList<>();
    private ArrayList<ItemUtil.TimedItemStorage> removed;
    private ArrayList<ItemUtil.TimedItemStorage> added;

    private IntegerOption timeout = new IntegerOption("timeout", 6, 1, 60);

    public ItemUpdateHud() {
        super(200, 11 * 6 - 2);
        removed = new ArrayList<>();
        added = new ArrayList<>();
    }

    public void update() {
        this.removed = ItemUtil.removeOld(removed, timeout.get() * 1000);
        this.added = ItemUtil.removeOld(added, timeout.get() * 1000);
        updateAdded();
        updateRemoved();
        oldItems = ItemUtil.storageFromItem(ItemUtil.getItems(client));
    }

    @Override
    public boolean tickable() {
        return true;
    }

    @Override
    public void tick() {
        if (client.world != null) {
            update();
        }
    }

    private void updateAdded() {
        List<ItemUtil.ItemStorage> added = ItemUtil.compare(ItemUtil.storageFromItem(ItemUtil.getItems(client)), oldItems);
        ArrayList<ItemUtil.TimedItemStorage> timedAdded = new ArrayList<>();
        for (ItemUtil.ItemStorage stack : added) {
            timedAdded.add(stack.timed());
        }
        for (ItemUtil.TimedItemStorage stack : timedAdded) {
            if (stack.stack.isEmpty()) {
                continue;
            }
            Optional<ItemUtil.TimedItemStorage> item = ItemUtil.getTimedItemFromItem(stack.stack, this.added);
            if (item.isPresent()) {
                item.get().incrementTimes(stack.times);
            } else {
                this.added.add(stack);
            }
        }
        this.added.sort((o1, o2) -> Float.compare(o1.getPassedTime(), o2.getPassedTime()));
    }

    private void updateRemoved() {
        List<ItemUtil.ItemStorage> removed = ItemUtil.compare(oldItems, ItemUtil.storageFromItem(ItemUtil.getItems(client)));
        List<ItemUtil.TimedItemStorage> timed = ItemUtil.untimedToTimed(removed);
        for (ItemUtil.TimedItemStorage stack : timed) {
            if (stack.stack.isEmpty()) {
                continue;
            }
            Optional<ItemUtil.TimedItemStorage> item = ItemUtil.getTimedItemFromItem(stack.stack, this.removed);
            if (item.isPresent()) {
                item.get().incrementTimes(stack.times);
            } else {
                this.removed.add(stack);
            }
        }
        this.removed.sort((o1, o2) -> Float.compare(o1.getPassedTime(), o2.getPassedTime()));
    }

    @Override
    public void render() {
        scale();
        DrawPosition pos = getPos();
        int lastY = 1;
        int i = 0;
        for (ItemUtil.ItemStorage item : this.added) {
            if (i > 5) {
                GlStateManager.popMatrix();
                return;
            }
            String message = "+ " +
                    Formatting.DARK_GRAY + "[" +
                    Formatting.WHITE + item.times +
                    Formatting.DARK_GRAY + "] " +
                    Formatting.RESET+
                    item.stack.getName();
            if (shadow.get()) {
                client.textRenderer.drawWithShadow(message, pos.x, pos.y + lastY,
                        Color.SELECTOR_GREEN.getAsInt());
            } else {
                client.textRenderer.draw(message, pos.x, pos.y + lastY,
                        Color.SELECTOR_GREEN.getAsInt());
            }
            lastY = lastY + client.textRenderer.fontHeight + 2;
            i++;
        }
        for (ItemUtil.ItemStorage item : this.removed) {
            if (i > 5) {
                GlStateManager.popMatrix();
                return;
            }
            String message = "- " +
                    Formatting.DARK_GRAY + "[" +
                    Formatting.WHITE + item.times +
                    Formatting.DARK_GRAY + "] " +
                    Formatting.RESET+
                    item.stack.getName();
            if (shadow.get()) {
                client.textRenderer.drawWithShadow(message, pos.x, pos.y + lastY,
                        Color.SELECTOR_RED.getAsInt());
            } else {
                client.textRenderer.draw(message, pos.x, pos.y + lastY,
                        Color.SELECTOR_RED.getAsInt());
            }
            lastY = lastY + client.textRenderer.fontHeight + 2;
            i++;
        }
        GlStateManager.popMatrix();
    }

    @Override
    public void renderPlaceholder() {
        renderPlaceholderBackground();
        scale();
        DrawPosition pos = getPos();
        String addM = "+ " +
                Formatting.DARK_GRAY + "[" +
                Formatting.WHITE + 2 +
                Formatting.DARK_GRAY + "] " +
                Formatting.RESET+
                new ItemStack(Blocks.DIRT).getName();
        if (shadow.get()) {
            client.textRenderer.drawWithShadow(addM, pos.x+1, pos.y+1,
                    Color.SELECTOR_GREEN.getAsInt());
        } else {
            client.textRenderer.draw(addM, pos.x+1, pos.y+1 + client.textRenderer.fontHeight + 2,
                    Color.SELECTOR_GREEN.getAsInt());
        }
        String removeM = "- " +
                Formatting.DARK_GRAY + "[" +
                Formatting.WHITE + 4 +
                Formatting.DARK_GRAY + "] " +
                Formatting.RESET+
                new ItemStack(Blocks.GRASS).getName();
        if (shadow.get()) {
            client.textRenderer.drawWithShadow(removeM, pos.x+1, pos.y+1 + client.textRenderer.fontHeight + 2,
                    Color.SELECTOR_RED.getAsInt());
        } else {
            client.textRenderer.draw(removeM, pos.x+1, pos.y+1 + client.textRenderer.fontHeight + 3,
                    Color.SELECTOR_RED.getAsInt());
        }
        hovered = false;
        GlStateManager.popMatrix();
    }

    @Override
    public void addConfigOptions(List<Option> options) {
        super.addConfigOptions(options);
        options.add(shadow);
        options.add(timeout);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public boolean movable() {
        return true;
    }

}

