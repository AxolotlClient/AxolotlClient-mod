package io.github.axolotlclient.modules.hud.gui.hud;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.config.options.BooleanOption;
import io.github.axolotlclient.config.options.ColorOption;
import io.github.axolotlclient.config.options.Option;
import io.github.axolotlclient.modules.hud.gui.AbstractHudEntry;
import io.github.axolotlclient.modules.hud.util.DrawPosition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

import java.util.List;

public class ChatHud extends AbstractHudEntry {

    public static Identifier ID = new Identifier("axolotlclient", "chathud");
    public BooleanOption background = new BooleanOption("background", true);
    public ColorOption bgColor = new ColorOption("bgColor", "#00FFFFFF");


    public ChatHud() {
        super(320,
                20);
    }

    @Override
    public void render() {
        //look at ChatHudMixin
        // we got the values there, so I did it there
    }

    @Override
    protected double getDefaultX() {
        return 0.01;
    }

    @Override
    protected float getDefaultY() {
        return 0.9F;
    }

    @Override
    public boolean tickable() {
        return true;
    }

    @Override
    public void tick() {
        width = (int) (client.options.chatWidth*320);
        height=(int) (client.options.chatHeightUnfocused*180)+11;
    }

    @Override
    public void renderPlaceholder() {
        renderPlaceholderBackground();
        scale();
        DrawPosition pos = getPos();
        if(MinecraftClient.getInstance().player!=null) {
            client.textRenderer.drawWithShadow(
                    "<" + MinecraftClient.getInstance().player.method_6344().asFormattedString() + "> OOh! There's my Chat now!",
                    pos.x + 1, pos.y + height - 9, -1
            );
        } else {
            client.textRenderer.drawWithShadow(
                    "This is where your new and fresh looking chat will be!",
                    pos.x + 1, pos.y + height - 9, -1
            );
        }
        GlStateManager.popMatrix();
        hovered=false;
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
        options.add(background);
        options.add(bgColor);
    }
}
