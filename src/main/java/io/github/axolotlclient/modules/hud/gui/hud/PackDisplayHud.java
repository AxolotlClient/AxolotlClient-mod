package io.github.axolotlclient.modules.hud.gui.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.texture.NativeImage;
import io.github.axolotlclient.AxolotlclientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlclientConfig.options.Option;
import io.github.axolotlclient.modules.hud.gui.entry.TextHudEntry;
import io.github.axolotlclient.modules.hud.util.DrawPosition;
import io.github.axolotlclient.util.Logger;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.pack.ResourcePack;
import net.minecraft.util.Identifier;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PackDisplayHud extends TextHudEntry {

    public static Identifier ID = new Identifier("axolotlclient","packdisplayhud");

    private final BooleanOption iconsOnly = new BooleanOption("iconsonly", false);

    public final List<PackWidget> widgets = new ArrayList<>();
    private final List<ResourcePack> packs = new ArrayList<>();
    private PackWidget placeholder;

    public PackDisplayHud() {
        super(200, 50, true);
    }

    @Override
    public void init() {
        packs.forEach(pack -> {
            try {
                InputStream s = pack.openRoot("pack.png");
                if(s != null) {
                    s.close();
                    if(packs.size()==1){
                        widgets.add(new PackWidget(pack));
                    } else if (!pack.getName().equalsIgnoreCase("Default")){
                        widgets.add(new PackWidget(pack));
                    }
                }
            } catch (Exception ignored) {
            }
        });

	    AtomicInteger w = new AtomicInteger(20);
		widgets.forEach(packWidget -> {
			int textW = MinecraftClient.getInstance().textRenderer.getWidth(packWidget.getName())+20;
			if(textW>w.get())
				w.set(textW);
		});
		setWidth(w.get());

		setHeight(widgets.size()*18);
        onBoundsUpdate();
    }

    public void setPacks(List<ResourcePack> packs){
        widgets.clear();
        this.packs.clear();
        this.packs.addAll(packs);
    }

    @Override
    public void renderComponent(MatrixStack matrices, float f) {
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
        if(y - pos.y+1 != getHeight()){
            setHeight(y- pos.y-1);
            onBoundsUpdate();
        }
    }

    @Override
    public void renderPlaceholderComponent(MatrixStack matrices, float f) {
        boolean updateBounds = false;
        if(getHeight()<18){
            setHeight(18);
            updateBounds = true;
        }
        if(getWidth()<56){
            setWidth(56);
            updateBounds = true;
        }
        if(updateBounds){
            onBoundsUpdate();
        }
        if(placeholder == null){
            placeholder = new PackWidget(MinecraftClient.getInstance().getResourcePackProvider().getPack());
        }
        placeholder.render(matrices, getPos().x+1, getPos().y+1);
    }

    @Override
    public List<Option<?>> getConfigurationOptions() {
        List<Option<?>> options = super.getConfigurationOptions();
        options.add(iconsOnly);
        return options;
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public boolean movable() {
        return true;
    }

    private class PackWidget {
        private int texture;
        @Getter
        public final String name;

        public PackWidget(ResourcePack pack){
            this.name=pack.getName();
            try {
	            InputStream stream = pack.openRoot("pack.png");
	            assert stream != null;
	            this.texture = new NativeImageBackedTexture(
					NativeImage.read(stream)).getGlId();
                stream.close();
            } catch (Exception e){
                Logger.warn("Pack "+pack.getName()+" somehow threw an error! Please investigate... Does it have an icon?");
            }
        }

        public void render(MatrixStack matrices, int x, int y) {
            if(!iconsOnly.get()) {
                RenderSystem.setShaderColor(1, 1, 1, 1F);
                RenderSystem.setShaderTexture(0, texture);
                DrawableHelper.drawTexture(matrices, x, y, 0, 0, 16, 16, 16, 16);
            }
            drawString(matrices, name, x + 18, y + 6, textColor.get().getAsInt(), shadow.get());
        }

    }
}
