package io.github.moehreag.axolotlclient.modules.hud;

import io.github.moehreag.axolotlclient.Axolotlclient;
import io.github.moehreag.axolotlclient.config.AxolotlclientConfigScreen;
import io.github.moehreag.axolotlclient.config.options.BooleanOption;
import io.github.moehreag.axolotlclient.config.screen.ScreenBuilder;
import io.github.moehreag.axolotlclient.config.widgets.BooleanButtonWidget;
import io.github.moehreag.axolotlclient.modules.hud.gui.AbstractHudEntry;
import io.github.moehreag.axolotlclient.modules.hud.gui.screen.ConfigScreen;
import io.github.moehreag.axolotlclient.modules.hud.snapping.SnappingHelper;
import io.github.moehreag.axolotlclient.modules.hud.util.Rectangle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;

import java.util.List;
import java.util.Optional;

public class HudEditScreen extends Screen {

    private BooleanOption snapping = new BooleanOption("snapping", true);
    private AbstractHudEntry current;
    private final HudManager manager;
    private boolean mouseDown;
    private SnappingHelper snap;

    public HudEditScreen(){
        snapping.setDefaults();
        updateSnapState();
        manager = HudManager.getINSTANCE();
        mouseDown = false;
    }

    @Override
    public void render(int mouseX, int mouseY, float tickDelta) {
        this.renderBackground();
        super.render(mouseX, mouseY, tickDelta);
        Optional<AbstractHudEntry> entry = manager.getEntryXY(mouseX, mouseY);
        entry.ifPresent(abstractHudEntry -> abstractHudEntry.setHovered(true));
        manager.renderPlaceholder();
        if (mouseDown && snapping.get()) {
            if(snap == null)updateSnapState();
            else snap.renderSnaps();
        }

    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        mouseDown = true;
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int button) {
        super.mouseReleased(mouseX, mouseY, button);
        mouseDown=false;
    }

    @Override
    protected void mouseDragged(int i, int j, int k, long l) {
        super.mouseDragged(i, j, k, l);

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
    protected void buttonClicked(ButtonWidget button) {
        super.buttonClicked(button);
        if(button.id==1){
            snapping.toggle();
            MinecraftClient.getInstance().openScreen(this);
        }

        if(button.id==3)MinecraftClient.getInstance().openScreen(new ScreenBuilder(this));
        if(button.id==2)MinecraftClient.getInstance().openScreen(new ConfigScreen(this, manager));
    }

    @Override
    public void init() {
        super.init();
        this.buttons.add(new BooleanButtonWidget(1,
                width / 2 - 50,
                height - 50 - 22,
                100, 20,
                I18n.translate("hud.snapping"), snapping
        ));
        this.buttons.add(new ButtonWidget(2,
                width / 2 - 50,
                height - 50 ,
                100, 20,
                I18n.translate("hud.configuration")
        ));
        this.buttons.add(new ButtonWidget(3,
                width / 2 - 50,
                height - 50 + 22 ,
                100, 20,
                I18n.translate("hud.clientOptions")
        ));
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void removed() {
        manager.save();
    }

    @Override
    public boolean shouldPauseGame() {
        return false;
    }


}
