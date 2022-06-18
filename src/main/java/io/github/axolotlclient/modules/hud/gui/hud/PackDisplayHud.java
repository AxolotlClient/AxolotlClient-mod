package io.github.axolotlclient.modules.hud.gui.hud;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.config.options.Option;
import io.github.axolotlclient.modules.hud.gui.AbstractHudEntry;
import io.github.axolotlclient.modules.hud.util.DrawPosition;
import io.github.axolotlclient.modules.hud.util.Rectangle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.resource.ResourcePack;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PackDisplayHud extends AbstractHudEntry {

    public static Identifier ID = new Identifier("axolotlclient","packdisplayhud");

    public final List<packWidget> widgets = new ArrayList<>();

    public PackDisplayHud() {
        super(200, 50);
    }

    @Override
    public void init() {
        AxolotlClient.packs.forEach(pack -> {
            try {
                if(!pack.getName().equalsIgnoreCase("Default") && pack.getIcon()!=null)
                    widgets.add(new packWidget(pack));
            } catch (Exception ignored) {
            }
        });

        AtomicInteger w = new AtomicInteger(20);
        widgets.forEach(packWidget -> {
            int textW = MinecraftClient.getInstance().textRenderer.getStringWidth(packWidget.name)+20;
            if(textW>w.get())
                w.set(textW);
        });
        width=w.get();

        height=(widgets.size()-1)*18+18;
    }

    @Override
    public void render() {
        scale();
        DrawPosition pos = getPos();

        if(widgets.isEmpty())init();

        if(background.get()){
            fillRect(new Rectangle(pos.x, pos.y, width, widgets.size()*18), backgroundColor.get());
        }

        if(outline.get()) outlineRect(getBounds(), outlineColor.get());

        int y= pos.y+1;
        for(int i=widgets.size()-1;i>=0;i--){ // Badly reverse the order (I'm sure there are better ways to do this)
            widgets.get(i).render(pos.x+1, y);
            y+=18;
        }

        GlStateManager.popMatrix();
    }

    @Override
    public void renderPlaceholder() {
        renderPlaceholderBackground();
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

    private class packWidget{
        private int texture;
        private final String name;

        public packWidget(ResourcePack pack){
            this.name=pack.getName();
            try {
                this.texture = new NativeImageBackedTexture(pack.getIcon()).getGlId();
            } catch (Exception e){
                AxolotlClient.LOGGER.warn("Pack "+pack.getName()+" somehow threw an error! Please investigate...");
            }
        }

        public void render(int x, int y) {
            GlStateManager.color3f(textColor.get().getRed(), textColor.get().getGreen(), textColor.get().getBlue());
            GlStateManager.bindTexture(texture);
            drawTexture(x, y, 0, 0, 16, 16, 16, 16);
            drawString(MinecraftClient.getInstance().textRenderer, name, x + 18, y + 6, chroma.get()? textColor.getChroma().getAsInt() : textColor.get().getAsInt(), shadow.get());
        }

    }

    @Override
    public void addConfigOptions(List<Option> options) {
        super.addConfigOptions(options);

        options.add(background);
        options.add(backgroundColor);
        options.add(outline);
        options.add(outlineColor);
        options.add(shadow);
        options.add(textColor);
        options.add(chroma);
    }
}
