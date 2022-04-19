package io.github.moehreag.axolotlclient.config.screen.widgets;

import io.github.moehreag.axolotlclient.config.options.OptionCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class CategoryWidget extends ButtonWidget {

    private final Identifier BUTTON_TEXTURE = new Identifier("axolotlclient", "textures/gui/button1.png");
    private final Identifier BUTTON2_TEXTURE = new Identifier("axolotlclient", "textures/gui/button2.png");

    public OptionCategory category;

    public CategoryWidget(OptionCategory category, int row, int line, int width, int height) {
        super(0, row, height/3 + (line*20+line*2), width, 20, "");
        this.category=category;
    }

    @Override
    public void render(@NotNull MinecraftClient client, int mouseX, int mouseY) {
        client.getTextureManager().bindTexture(BUTTON_TEXTURE);
        if(width==100)client.getTextureManager().bindTexture(BUTTON2_TEXTURE);
        drawTexture(x, y, 0, 0,width, height, width, height);
        drawCenteredString(client.textRenderer, category.getTranslatedName() +"...", x+(width/2), y+(height-8)/2, -1);
    }


}
