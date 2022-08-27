package io.github.axolotlclient.modules.hud;

import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.config.ConfigManager;
import io.github.axolotlclient.config.options.BooleanOption;
import io.github.axolotlclient.config.options.OptionCategory;
import io.github.axolotlclient.config.screen.OptionsScreenBuilder;
import io.github.axolotlclient.modules.hud.gui.AbstractHudEntry;
import io.github.axolotlclient.modules.hud.snapping.SnappingHelper;
import io.github.axolotlclient.modules.hud.util.DrawPosition;
import io.github.axolotlclient.modules.hud.util.Rectangle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.List;
import java.util.Optional;

/**
 * This implementation of Hud modules is based on KronHUD.
 * https://github.com/DarkKronicle/KronHUD
 * Licensed under GPL-3.0
 */

public class HudEditScreen extends Screen {

    private final BooleanOption snapping = new BooleanOption("snapping", true);
    private AbstractHudEntry current;
    private DrawPosition offset = null;
    private final HudManager manager;
    private boolean mouseDown;
    private SnappingHelper snap;
    private final Screen parent;

    public HudEditScreen(Screen parent){
	    super(Text.empty());
	    snapping.setDefaults();
        updateSnapState();
        manager = HudManager.getInstance();
        mouseDown = false;
        this.parent=parent;
    }

    public HudEditScreen(){
        this(null);
    }

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if(MinecraftClient.getInstance().world!=null)fillGradient(matrices, 0,0, width, height, new Color(0xB0100E0E, true).hashCode(), new Color(0x46212020, true).hashCode());
        else {
			renderBackgroundTexture(0);
		}

        //GlStateManager.enableAlphaTest();
		super.render(matrices, mouseX, mouseY, delta);

        manager.renderPlaceholder(matrices);
        if (mouseDown && snap != null) {
            snap.renderSnaps(matrices);
        }

        Optional<AbstractHudEntry> entry = manager.getEntryXY(mouseX, mouseY);
        entry.ifPresent(abstractHudEntry -> abstractHudEntry.setHovered(true));

    }

	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		Optional<AbstractHudEntry> entry = HudManager.getInstance().getEntryXY((int) Math.round(mouseX), (int) Math.round(mouseY));
		if (button == 0) {
			mouseDown = true;
			if (entry.isPresent()) {
				current = entry.get();
				offset = new DrawPosition((int) Math.round(mouseX - current.getX()), (int) Math.round(mouseY - current.getY()));
				updateSnapState();
				return true;
			} else {
				current = null;
                return super.mouseClicked(mouseX, mouseY, button);
			}
		} else if (button == 1) {
			entry.ifPresent(abstractHudEntry -> MinecraftClient.getInstance().setScreen(new OptionsScreenBuilder(this, abstractHudEntry.getOptionsAsCategory())));
		}
		return false;
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if(current!=null){
            ConfigManager.save();
        }
        current = null;
        snap = null;
        mouseDown = false;
		return super.mouseReleased(mouseX, mouseY, button);
    }

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (current != null) {
            current.setXY((int) (mouseX - offset.x), (int) (mouseY - offset.y));
            if (snap != null) {
                Integer snapX, snapY;
                snap.setCurrent(current.getScaledBounds());
                if ((snapX = snap.getCurrentXSnap()) != null) {
                    current.setX(snapX);
                }
                if ((snapY = snap.getCurrentYSnap()) != null) {
                    current.setY(snapY);
                }
            }
            if (current.tickable()) {
                current.tick();
            }
        }
		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    private void updateSnapState() {
        if (snapping.get() && current != null) {
            List<Rectangle> bounds = manager.getAllBounds();
            bounds.remove(current.getScaledBounds());
            snap = new SnappingHelper(bounds, current.getScaledBounds());
        } else if (snap != null) {
            snap = null;
        }
    }

    @Override
    public void init() {
	    this.addDrawableChild(new ButtonWidget(width / 2 - 50,
		    height/2+ 12,
		    100, 20,
		    Text.translatable("hud.snapping").append(": ").append(Text.translatable(snapping.get()?"options.on":"options.off")),
		    buttonWidget -> {
			snapping.toggle();
			buttonWidget.setMessage(Text.translatable("hud.snapping").append(": ").append(Text.translatable(snapping.get()?"options.on":"options.off")));
			ConfigManager.save();
		}));

		this.addDrawableChild(new ButtonWidget(width / 2 - 75,
			height/2-10,
			150, 20,
			Text.translatable("hud.clientOptions"),
			buttonWidget -> MinecraftClient.getInstance().setScreen(
				new OptionsScreenBuilder(this, new OptionCategory("config")
					.addSubCategories(AxolotlClient.CONFIG.getCategories())))));

        if(parent!=null)addDrawableChild(new ButtonWidget(
                width/2 -75, height - 50 + 22, 150, 20,
                Text.translatable("back"), buttonWidget -> MinecraftClient.getInstance().setScreen(parent)));
        else addDrawableChild(new ButtonWidget(
                width/2 -75, height - 50 + 22, 150, 20,
                Text.translatable("close"), buttonWidget -> MinecraftClient.getInstance().setScreen(null)));

    }

    @Override
    public void tick() {
        if(current!=null && current.tickable()) {
            current.tick();
        }
    }

}
