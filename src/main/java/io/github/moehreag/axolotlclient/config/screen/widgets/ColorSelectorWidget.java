package io.github.moehreag.axolotlclient.config.screen.widgets;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.moehreag.axolotlclient.config.options.ColorOption;
import io.github.moehreag.axolotlclient.modules.hud.util.Color;
import io.github.moehreag.axolotlclient.modules.hud.util.DrawUtil;
import io.github.moehreag.axolotlclient.modules.hud.util.Rectangle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.Identifier;

public class ColorSelectorWidget extends CustomButtonWidget {

    private final Identifier picker = new Identifier("axolotlclient", "textures/gui/colorwheel.png");
    private final ColorOption option;
    private final Rectangle current;
    private final TextFieldWidget textInput;

    public ColorSelectorWidget(int x, int y, ColorOption option) {
        super(0, x, y, 75, 100, "", new Identifier("axolotlclient", "textures/gui/dialog2.png"));
        this.option=option;
        current=new Rectangle(x+18, y+62, 50, 15);
        textInput = new TextFieldWidget(0, MinecraftClient.getInstance().textRenderer, x+18, y+82, 50, 15);
        textInput.setMaxLength(9);
        textInput.setHasBorder(false);
        textInput.setText(option.get().toString());
    }

    @Override
    public void render(MinecraftClient client, int mouseX, int mouseY) {
        MinecraftClient.getInstance().getTextureManager().bindTexture(texture);
        drawTexture(x, y, 0, 0, width, height, width, height);
        MinecraftClient.getInstance().getTextureManager().bindTexture(picker);
        drawTexture(x+18, y+5, 0, 0, 50, 50, 50, 50);
        DrawUtil.fillRect(current, option.get());
        DrawUtil.outlineRect(current, Color.BLACK);

        textInput.render();

        GlStateManager.color3f(1F, 1F, 1F);
    }

    public void onClick(int mouseX, int mouseY){
        if(mouseX>=x+18 && mouseX<=x+65 && mouseY>=y+5 && mouseY <=y+55){
            int red = (mouseX-(x+41))*10;
            int blue = (mouseY-(y+30))*10;
            int green= mouseX<=x+46? (mouseX-(x+18))*10:0;
            green=green>0? (mouseY<y+30 && mouseX>x+41?green/4:255-green):0;

            if(red<50 && blue==0 && green<20)green=40;

            if(red<0)red=0;
            if(blue<0)blue=0;
            if(green<0)green=0;
            //System.out.println("R: "+red+" G: "+green+" B: "+blue);
            option.set(new Color(red, green, blue));
            textInput.setText(option.get().toString());
        }
        textInput.mouseClicked(mouseX, mouseY,0);
    }

    public void tick(){
        textInput.tick();
    }

    public void keyPressed(char character, int code){
        textInput.keyPressed(character, code);
    }
}
