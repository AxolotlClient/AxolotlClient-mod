package io.github.axolotlclient.modules.hud.gui.hud;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.modules.hud.gui.entry.TextHudEntry;
import io.github.axolotlclient.modules.hud.util.DrawPosition;
import io.github.axolotlclient.util.Logger;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.resource.ResourcePack;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PackDisplayHud extends TextHudEntry {

    public static Identifier ID = new Identifier("axolotlclient","packdisplayhud");

    public final List<packWidget> widgets = new ArrayList<>();

    private final List<ResourcePack> packs = new ArrayList<>();

    public PackDisplayHud() {
        super(200, 50, true);
    }

    @Override
    public void init() {
        packs.forEach(pack -> {
            try {
                if(!pack.getName().equalsIgnoreCase("Default") )//&& pack.getIcon()!=null)
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

		height=(widgets.size()-1)*18 + 18;
    }

    public void setPacks(List<ResourcePack> packs){
        widgets.clear();
        this.packs.addAll(packs);
        init();
    }

    @Override
    public void renderComponent(float f) {
        scale();
        DrawPosition pos = getPos();

        if(widgets.isEmpty())init();

        if(background.get()){
            fillRect(getBounds(), backgroundColor.get());
        }

        if(outline.get()) outlineRect(getBounds(), outlineColor.get());

        int y= pos.y+1;
        for(int i=widgets.size()-1;i>=0;i--){ // Badly reverse the order (I'm sure there are better ways to do this)
            widgets.get(i).render(pos.x+1, y);
            y+=18;
        }
    }

    @Override
    public void renderPlaceholderComponent(float f) {
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

    private class packWidget {
        private int texture;
        public final String name;

        public packWidget(ResourcePack pack){
            this.name=pack.getName();
            try {
                this.texture = new NativeImageBackedTexture(pack.getIcon()).getGlId();
            } catch (Exception e){
                Logger.warn("Pack "+pack.getName()+" somehow threw an error! Please investigate... Does it have an icon?");
				//e.printStackTrace();
            }
        }

        public void render(int x, int y) {
            GlStateManager.color4f(1, 1, 1, 1F);
            GlStateManager.bindTexture(texture);
            DrawableHelper.drawTexture(x, y, 0, 0, 16, 16, 16, 16);
            drawString(name, x + 18, y + 6, textColor.get().getAsInt(), shadow.get());
        }

    }
}
