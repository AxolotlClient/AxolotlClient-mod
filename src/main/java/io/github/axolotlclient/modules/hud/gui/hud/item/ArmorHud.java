package io.github.axolotlclient.modules.hud.gui.hud.item;

import io.github.axolotlclient.AxolotlclientConfig.options.OptionBase;
import io.github.axolotlclient.modules.hud.gui.entry.TextHudEntry;
import io.github.axolotlclient.modules.hud.util.DrawPosition;
import io.github.axolotlclient.modules.hud.util.ItemUtil;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.List;

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
        ItemUtil.renderGuiItemModel(getScale(), stack, x, y);
        ItemUtil.renderGuiItemOverlay(client.textRenderer, stack, x, y, null, textColor.get().getAsInt(), shadow.get());
    }

    public void renderMainItem(ItemStack stack, int x, int y) {
        ItemUtil.renderGuiItemModel(getScale(), stack, x, y);
        String total = String.valueOf(ItemUtil.getTotal(client, stack));
        if (total.equals("1")) {
            total = null;
        }
        ItemUtil.renderGuiItemOverlay(client.textRenderer, stack, x, y, total, textColor.get().getAsInt(), shadow.get());
    }

    @Override
    public void renderPlaceholderComponent(float delta) {
        DrawPosition pos = getPos();
        int lastY = 2 + (4 * 20);
        ItemUtil.renderGuiItemModel(getScale(), new ItemStack(Blocks.GRASS), pos.x() + 2, pos.y() + lastY);
        ItemUtil.renderGuiItemOverlay(client.textRenderer, new ItemStack(Blocks.GRASS), pos.x() + 2, pos.y() + lastY, "90",
                textColor.get().getAsInt(), shadow.get()
        );
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
