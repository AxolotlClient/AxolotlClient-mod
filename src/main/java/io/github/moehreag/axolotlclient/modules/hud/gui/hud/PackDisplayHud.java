package io.github.moehreag.axolotlclient.modules.hud.gui.hud;

import io.github.moehreag.axolotlclient.AxolotlClient;
import io.github.moehreag.axolotlclient.config.options.Option;
import io.github.moehreag.axolotlclient.modules.hud.gui.AbstractHudEntry;
import io.github.moehreag.axolotlclient.modules.hud.util.DrawPosition;
import io.github.moehreag.axolotlclient.modules.hud.util.Rectangle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.pack.ResourcePack;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

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
    }

    @Override
    public void render(MatrixStack matrices) {
        scale(matrices);
        DrawPosition pos = getPos();

        if(widgets.isEmpty())init();

        if(background.get()){
            fillRect(matrices, new Rectangle(pos.x, pos.y, width, widgets.size()*18), backgroundColor.get());
        }

        if(outline.get()) outlineRect(matrices, getBounds(), outlineColor.get());

        int y= pos.y+1;
        for(PackDisplayHud.packWidget widget:widgets){
            widget.render(matrices, pos.x+1, y);
            y+=18;
        }

        matrices.pop();
    }

    @Override
    public void renderPlaceholder(MatrixStack matrices) {
        renderPlaceholderBackground(matrices);
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
                //this.texture = new NativeImageBackedTexture(pack.).getGlId();
            } catch (Exception e){
                AxolotlClient.LOGGER.warn("Pack "+pack.getName()+" somehow threw an error! Please investigate...");
            }
        }

        public void render(MatrixStack matrices, int x, int y) {
            /*GlStateManager.color3f(textColor.get().getRed(), textColor.get().getGreen(), textColor.get().getBlue());
            GlStateManager.bindTexture(texture);
            drawTexture(x, y, 0, 0, 16, 16, 16, 16);*/
            drawString(matrices, MinecraftClient.getInstance().textRenderer, name, x + 18, y + 6, textColor.get().getAsInt(), shadow.get());
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
    }
}
