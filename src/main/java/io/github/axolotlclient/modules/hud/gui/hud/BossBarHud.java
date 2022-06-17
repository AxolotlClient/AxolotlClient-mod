package io.github.axolotlclient.modules.hud.gui.hud;


import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.config.options.BooleanOption;
import io.github.axolotlclient.config.options.ColorOption;
import io.github.axolotlclient.config.options.Option;
import io.github.axolotlclient.modules.hud.gui.AbstractHudEntry;
import io.github.axolotlclient.modules.hud.util.DrawPosition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * This implementation of Hud modules is based on KronHUD.
 * https://github.com/DarkKronicle/KronHUD
 * Licensed under GPL-3.0
 */

public class BossBarHud extends AbstractHudEntry {

    public static final Identifier ID = new Identifier("kronhud", "bossbarhud");
    private static final Identifier BARS_TEXTURE = new Identifier("textures/gui/icons.png");
    
    private final MinecraftClient client;
    private final ColorOption barColor = new ColorOption("barColor", "#FFFFFFFF");
    private final BooleanOption text = new BooleanOption("text", true);
    private final BooleanOption bar = new BooleanOption("bar", true);
    // TODO custom colour

    public BossBarHud() {
        super(186, 20);
        client = MinecraftClient.getInstance();
    }

    @Override
    public void render() {
        scale();
        DrawPosition pos = getPos();

        if (BossBar.name != null && BossBar.framesToLive > 0) {
            client.getTextureManager().bindTexture(BARS_TEXTURE);
            --BossBar.framesToLive;
            if(bar.get()) {
                GlStateManager.color4f(barColor.get().getRed(), barColor.get().getGreen(), barColor.get().getBlue(), barColor.get().getAlpha());
                drawTexture(pos.x + 2, pos.y + 12, 0, 74, 182, 5);
                drawTexture(pos.x + 2, pos.y + 12, 0, 74, 182, 5);
                if (BossBar.percent * 183F > 0) {
                    GlStateManager.color4f(barColor.get().getRed(), barColor.get().getGreen(), barColor.get().getBlue(), barColor.get().getAlpha());
                    drawTexture(pos.x + 2, pos.y + 12, 0, 79, (int) (BossBar.percent * 183F), 5);
                }
            }

            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            if(text.get()) {
                String string = BossBar.name;
                client.textRenderer.drawWithShadow(string,
                        (float) ((pos.x + width / 2) - client.textRenderer.getStringWidth(BossBar.name) / 2), (float) (pos.y + 2), textColor.get().getAsInt());
            }

        }
        GlStateManager.popMatrix();
    }

    @Override
    public void renderPlaceholder() {
        renderPlaceholderBackground();
        scale();
        DrawPosition pos = getPos();
        //outlineRect(getBounds(), Color.BLACK);

        client.getTextureManager().bindTexture(BARS_TEXTURE);
        GlStateManager.color4f(barColor.get().getRed(), barColor.get().getGreen(), barColor.get().getBlue(), barColor.get().getAlpha());
        drawTexture(pos.x+2, pos.y+12, 0, 74, 182, 5);
        drawTexture(pos.x+2, pos.y+12, 0, 79, 183, 5);

        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        client.textRenderer.drawWithShadow("Boss Name",
                (float)((pos.x+width/2)- client.textRenderer.getStringWidth("Boss Name") / 2),
                (float)(pos.y +3), textColor.get().getAsInt());
        
        hovered = false;
        GlStateManager.popMatrix();
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
    public void addConfigOptions(List<Option> options) {
        super.addConfigOptions(options);
        options.add(text);
        options.add(textColor);
        options.add(shadow);
        options.add(bar);
        options.add(barColor);
    }
}
