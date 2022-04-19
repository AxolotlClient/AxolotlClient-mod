package io.github.moehreag.axolotlclient.config.screen.widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class EnumWidget extends CustomButtonWidget {

    private final Identifier DIALOG_TEXTURE= new Identifier("axolotlclient", "textures/gui/dialog.png");

    public EnumWidget(int x, int y, int width, int height, @NotNull Enum<?> message) {
        super(x, y, width, height, message.toString());
    }

    @Override
    public void render(MinecraftClient client, int mouseX, int mouseY) {
        this.text=text.substring(0, 1).toUpperCase()+text.substring(1).toLowerCase();
        MinecraftClient.getInstance().getTextureManager().bindTexture(DIALOG_TEXTURE);
        drawTexture(x, y, 0, 0, width, height, width, height);
        drawCenteredString(MinecraftClient.getInstance().textRenderer, text,
                x+width/2,
                y+height/4, -1);
    }
}
