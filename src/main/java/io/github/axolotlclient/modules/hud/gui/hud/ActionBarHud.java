package io.github.axolotlclient.modules.hud.gui.hud;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.config.options.Option;
import io.github.axolotlclient.modules.hud.gui.AbstractHudEntry;
import io.github.axolotlclient.config.Color;
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

    //public final IntegerOption timeShown = new IntegerOption("timeshown", 60, 40, 300);

    private String actionBar;
    private int color;
    MinecraftClient client;

    public ActionBarHud() {
        super(115, 13);
        client = MinecraftClient.getInstance();
    }

    public void setActionBar(String bar, int color){this.actionBar = bar; this.color = color;}

    @Override
    public void render() {
        if (new Color(color).getAlpha()==0){
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
    }
}
