package io.github.moehreag.axolotlclient.modules.hud.gui.hud;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.moehreag.axolotlclient.config.options.IntegerOption;
import io.github.moehreag.axolotlclient.config.options.Option;
import io.github.moehreag.axolotlclient.modules.hud.gui.AbstractHudEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * This implementation of Hud modules is based on KronHUD.
 * https://github.com/DarkKronicle/KronHUD
 * Licensed under GPL-3.0
 */

public class ActionBarHud extends AbstractHudEntry {

    public static final Identifier ID = new Identifier("kronhud", "actionbarhud");

    public final IntegerOption timeShown = new IntegerOption("timeshown", 60, 40, 300);

    private String actionBar;
    private int ticksShown;
    private int color;
    MinecraftClient client;

    public ActionBarHud() {
        super(115, 13);
        client = MinecraftClient.getInstance();
    }

    public void setActionBar(String bar, int color){this.actionBar = bar; this.color = color;}

    @Override
    public void render() {
        if (ticksShown >= timeShown.get()){
            this.actionBar = null;
        }
        if(this.actionBar != null) {

            scale();
            if (shadow.get()){
                client.textRenderer.drawWithShadow(actionBar, (float)getPos().x + Math.round((float) width /2) -  (float) client.textRenderer.getStringWidth(actionBar) /2, (float)getPos().y + 3, color);
            } else {

                client.textRenderer.draw(actionBar, getPos().x + Math.round(width /2F) - (client.textRenderer.getStringWidth(actionBar) /2), getPos().y + 3, color);
            }
            GlStateManager.popMatrix();
            ticksShown++;
        } else {
            ticksShown = 0;
        }
    }

    @Override
    public void renderPlaceholder() {
        renderPlaceholderBackground();
        scale();
        client.textRenderer.draw(I18n.translate("actionBarPlaceholder"),
                getPos().x + Math.round(width /2F) - client.textRenderer.getStringWidth(I18n.translate("actionBarPlaceholder")) /2,
                getPos().y + 3,
                -1);
        GlStateManager.popMatrix();
        hovered = false;
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public boolean movable() {
        return true;
    }

    @Override
    public void addConfigOptions(List<Option> options){
        super.addConfigOptions(options);
        options.add(shadow);
        options.add(timeShown);
    }
}
