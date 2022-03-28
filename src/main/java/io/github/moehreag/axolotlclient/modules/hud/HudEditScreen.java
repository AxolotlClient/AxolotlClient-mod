package io.github.moehreag.axolotlclient.modules.hud;

import io.github.moehreag.axolotlclient.config.AxolotlclientConfigScreen;
import io.github.moehreag.axolotlclient.config.widgets.BooleanButtonWidget;
import io.github.moehreag.axolotlclient.modules.hud.gui.AbstractHudEntry;
import io.github.moehreag.axolotlclient.modules.hud.gui.screen.ConfigScreen;
import io.github.moehreag.axolotlclient.modules.hud.snapping.SnappingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;

import java.util.Optional;

public class HudEditScreen extends Screen {

    private boolean snapping;
    private final HudManager manager;
    private boolean mouseDown;

    public HudEditScreen(){
        snapping=true;
        manager = new HudManager();
        mouseDown = false;
    }

    @Override
    public void render(int mouseX, int mouseY, float tickDelta) {
        this.renderBackground();
        super.render(mouseX, mouseY, tickDelta);
        /*Optional<AbstractHudEntry> entry = manager.getEntryXY(mouseX, mouseY);
        entry.ifPresent(abstractHudEntry -> abstractHudEntry.setHovered(true));*/
        manager.renderPlaceholder();
        if (mouseDown && snapping) {
            //SnappingHelper.renderSnaps();
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



    @Override
    protected void buttonClicked(ButtonWidget button) {
        super.buttonClicked(button);
        if(button.id==1){
            snapping=!snapping;
        }
        MinecraftClient.getInstance().openScreen(this);
        if(button.id==3)MinecraftClient.getInstance().openScreen(new AxolotlclientConfigScreen(this));
        if(button.id==2)MinecraftClient.getInstance().openScreen(new ConfigScreen(this));
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
