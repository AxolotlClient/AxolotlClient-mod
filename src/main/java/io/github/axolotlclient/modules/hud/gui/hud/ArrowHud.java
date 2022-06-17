package io.github.axolotlclient.modules.hud.gui.hud;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.config.options.BooleanOption;
import io.github.axolotlclient.config.options.Option;
import io.github.axolotlclient.modules.hud.gui.AbstractHudEntry;
import io.github.axolotlclient.modules.hud.util.DrawPosition;
import io.github.axolotlclient.modules.hud.util.ItemUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * This implementation of Hud modules is based on KronHUD.
 * https://github.com/DarkKronicle/KronHUD
 * Licensed under GPL-3.0
 */

public class ArrowHud extends AbstractHudEntry {
    public static final Identifier ID = new Identifier("kronhud", "arrowhud");
    private int arrows = 0;
    private final BooleanOption dynamic = new BooleanOption("dynamic", false);

    public ArrowHud() {
        super(20, 30);
    }

    @Override
    public void render() {
        if (dynamic.get()) {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if(player.getMainHandStack()==null)return;
            if (    !player.getMainHandStack().isEmpty() &&
                    !(player.getMainHandStack().getItem() instanceof BowItem)) {
                return;
            }
        }
        scale();

        DrawPosition pos = getPos();
        if (background.get()) {
            fillRect(getBounds(), backgroundColor.get());
        }
        if(outline.get()) outlineRect(getBounds(), outlineColor.get());
        drawCenteredString(client.textRenderer, String.valueOf(arrows), new DrawPosition(pos.x + width / 2,
                pos.y + height - 10), textColor.get(), shadow.get());
        ItemUtil.renderGuiItem(new ItemStack(Items.ARROW), pos.x + 2, pos.y + 2);
        GlStateManager.popMatrix();
    }

    @Override
    public boolean tickable() {
        return true;
    }

    @Override
    public void tick() {
        arrows = ItemUtil.getTotal(client, new ItemStack(Items.ARROW));

    }

    @Override
    public void renderPlaceholder() {
        renderPlaceholderBackground();
        scale();
        DrawPosition pos = getPos();
        drawCenteredString(client.textRenderer, "64", new DrawPosition(pos.x + width / 2,
                pos.y + height - 10), textColor.get(), shadow.get());
        ItemUtil.renderGuiItem(new ItemStack(Items.ARROW), pos.x + 2, pos.y + 2);
        hovered = false;
        GlStateManager.popMatrix();
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
        options.add(dynamic);
    }

    @Override
    public boolean movable() {
        return true;
    }

    @Override
    public Identifier getId() {
        return ID;
    }

}
