package io.github.axolotlclient.modules.hud.gui.hud.vanilla;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.AxolotlclientConfig.Color;
import io.github.axolotlclient.AxolotlclientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlclientConfig.options.EnumOption;
import io.github.axolotlclient.AxolotlclientConfig.options.OptionBase;
import io.github.axolotlclient.modules.hud.gui.component.DynamicallyPositionable;
import io.github.axolotlclient.modules.hud.gui.entry.TextHudEntry;
import io.github.axolotlclient.modules.hud.gui.layout.AnchorPoint;
import io.github.axolotlclient.modules.hud.util.DefaultOptions;
import io.github.axolotlclient.modules.hud.util.DrawPosition;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 * @license GPL-3.0
 */

public class BossBarHud extends TextHudEntry implements DynamicallyPositionable {

    public static final Identifier ID = new Identifier("kronhud", "bossbarhud");
    private static final Identifier BARS_TEXTURE = new Identifier("textures/gui/icons.png");
    private final CustomBossBar placeholder = new CustomBossBar("Boss bar", Color.WHITE);

    private final BooleanOption text = new BooleanOption("axolotlclient.text", true);
    private final BooleanOption bar = new BooleanOption("axolotlclient.bar", true);
    // TODO custom color
    private final EnumOption anchor = DefaultOptions.getAnchorPoint(AnchorPoint.TOP_MIDDLE);

    public BossBarHud() {
        super(184, 24, false);
    }

    @Override
    public void renderComponent(float delta) {
        GlStateManager.enableAlphaTest();
        DrawPosition pos = getPos();
        if (BossBar.name != null && BossBar.framesToLive > 0) {
            client.getTextureManager().bindTexture(BARS_TEXTURE);
            --BossBar.framesToLive;
            if(bar.get()) {
                //GlStateManager.color4f(barColor.get().getRed(), barColor.get().getGreen(), barColor.get().getBlue(), barColor.get().getAlpha());
                drawTexture(pos.x , pos.y + 12, 0, 74, 182, 5);
                drawTexture(pos.x, pos.y + 12, 0, 74, 182, 5);
                if (BossBar.percent * 183F > 0) {
                    //GlStateManager.color4f(barColor.get().getRed(), barColor.get().getGreen(), barColor.get().getBlue(), barColor.get().getAlpha());
                    drawTexture(pos.x, pos.y + 12, 0, 79, (int) (BossBar.percent * 183F), 5);
                }
            }

            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            if(text.get()) {
                String string = BossBar.name;
                client.textRenderer.draw(string,
                        (float) ((pos.x + width / 2) - client.textRenderer.getStringWidth(BossBar.name) / 2), (float) (pos.y + 2), textColor.get().getAsInt(), shadow.get());
            }

        }
    }

    @Override
    public void renderPlaceholderComponent(float delta) {
        DrawPosition pos = getPos();
        placeholder.render(pos.x, pos.y+14);
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
    public List<OptionBase<?>> getConfigurationOptions() {
        List<OptionBase<?>> options = super.getConfigurationOptions();
        options.add(text);
        options.add(bar);
        options.add(anchor);
        return options;
    }

    @RequiredArgsConstructor
    public class CustomBossBar extends DrawableHelper {
        private final String name;
        private final Color barColor;


        public void render(int x, int y){
            GlStateManager.enableTexture();
            if(bar.get()) {
                MinecraftClient.getInstance().getTextureManager().bindTexture(BARS_TEXTURE);
                GlStateManager.color4f(barColor.getRed(), barColor.getGreen(), barColor.getBlue(), barColor.getAlpha());
                this.drawTexture(x+1, y, 0, 79, width, 5);

            }

            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            if(text.get()) {
                client.textRenderer.draw(name,
                        (float) ((x + width / 2) - client.textRenderer.getStringWidth(name) / 2), (float) (y -10), textColor.get().getAsInt(), shadow.get());
            }
        }
    }

    @Override
    public AnchorPoint getAnchor() {
        return AnchorPoint.valueOf(anchor.get());
    }
}
