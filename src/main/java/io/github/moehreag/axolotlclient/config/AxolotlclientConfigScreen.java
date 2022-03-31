package io.github.moehreag.axolotlclient.config;

import io.github.moehreag.axolotlclient.config.screen.*;
import io.github.prospector.modmenu.gui.ModListScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;

public class AxolotlclientConfigScreen extends Screen {

    protected final Screen parent;

    public AxolotlclientConfigScreen(){
        super();
        this.parent = new ModListScreen(MinecraftClient.getInstance().world != null ? new GameMenuScreen(): new TitleScreen());
    }

    public AxolotlclientConfigScreen(Screen parent){
        super();
        this.parent = parent;
    }

    @Override
    public void init() {
        this.buttons.add(new ButtonWidget(1, this.width / 2 - 155, this.height / 6 + 72 - 6, 150, 20, I18n.translate("general")));
        this.buttons.add(new ButtonWidget(2, this.width / 2 + 5, this.height / 6 + 72 - 6, 150, 20, I18n.translate("badges")));
        this.buttons.add(new ButtonWidget(3, this.width / 2 - 155, this.height / 6 + 96 - 6, 150, 20, I18n.translate("nametagConf")));
        this.buttons.add(new ButtonWidget(4, this.width / 2 + 5, this.height / 6 + 96 - 6, 150, 20, I18n.translate("nickHider")));
        this.buttons.add(new ButtonWidget(5, this.width / 2 - 155, this.height / 6 + 120 - 6, 150, 20, I18n.translate("rpcConf")));
        this.buttons.add(new ButtonWidget(6, this.width / 2 + 5, this.height / 6 + 120 - 6, 150, 20, I18n.translate("cursedConf")));

        this.buttons.add(new ButtonWidget(0, this.width / 2 - 100, this.height / 6 + 168, I18n.translate("back")));
    }

    @Override
    public void render(int mouseX, int mouseY, float tickDelta) {
        this.renderBackground();
        this.drawCenteredString(this.textRenderer, I18n.translate("config"), this.width / 2, 17, 16777215);
        super.render(mouseX, mouseY, tickDelta);
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        if(button.id == 0){
            ConfigManager.save();
            this.client.openScreen(parent);
        }

        switch (button.id){
            case 1: this.client.openScreen(new GeneralConfScreen(new AxolotlclientConfigScreen(parent))); break;
            case 2: this.client.openScreen(new BadgeConfScreen(new AxolotlclientConfigScreen(parent))); break;
            case 3: this.client.openScreen(new NametagConfScreen(new AxolotlclientConfigScreen(parent))); break;
            case 4: this.client.openScreen(new NickHiderConfScreen(new AxolotlclientConfigScreen(parent))); break;
            case 5: this.client.openScreen(new RPCConfScreen(new AxolotlclientConfigScreen(parent))); break;
            case 6: this.client.openScreen(new CursedConfScreen(new AxolotlclientConfigScreen(parent))); break;
        }
    }
}
