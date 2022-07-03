package io.github.axolotlclient.modules.hud.gui.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.config.options.Option;
import io.github.axolotlclient.modules.hud.gui.AbstractHudEntry;
import io.github.axolotlclient.modules.hud.util.DrawPosition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourcePack;
import net.minecraft.util.Identifier;

import java.io.InputStream;
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
                if(!pack.getName().equalsIgnoreCase("Default") )//&& pack.getIcon()!=null)
                    widgets.add(new packWidget(pack));
            } catch (Exception ignored) {
            }
        });

	    AtomicInteger w = new AtomicInteger(20);
		widgets.forEach(packWidget -> {
			int textW = MinecraftClient.getInstance().textRenderer.getWidth(packWidget.name)+20;
			if(textW>w.get())
				w.set(textW);
		});
		width=w.get();

		height=(widgets.size()-1)*18 + 18;
    }

    @Override
    public void render(MatrixStack matrices) {
        scale(matrices);
        DrawPosition pos = getPos();

        if(widgets.isEmpty())init();

        if(background.get()){
            fillRect(matrices, getBounds(), backgroundColor.get());
        }

        if(outline.get()) outlineRect(matrices, getBounds(), outlineColor.get());

        int y= pos.y+1;
        for(int i=widgets.size()-1;i>=0;i--){ // Badly reverse the order (I'm sure there are better ways to do this)
            widgets.get(i).render(matrices, pos.x+1, y);
            y+=18;
        }

        matrices.pop();
    }

    @Override
    public void renderPlaceholder(MatrixStack matrices) {
        renderPlaceholderBackground(matrices);
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
	            InputStream stream = pack.openRoot("pack.png");
	            assert stream != null;
	            this.texture = new NativeImageBackedTexture(
					NativeImage.read(stream)).getGlId();
            } catch (Exception e){
                AxolotlClient.LOGGER.warn("Pack "+pack.getName()+" somehow threw an error! Please investigate... Does it have an icon?");
				//e.printStackTrace();
            }
        }

        public void render(MatrixStack matrices, int x, int y) {
            RenderSystem.setShaderColor(1, 1, 1, 1F);
            RenderSystem.setShaderTexture(0, texture);
            DrawableHelper.drawTexture(matrices, x, y, 0, 0, 16, 16, 16, 16);
            drawString(matrices, MinecraftClient.getInstance().textRenderer, name, x + 18, y + 6, chroma.get()? textColor.getChroma().getAsInt() : textColor.get().getAsInt(), shadow.get());
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
