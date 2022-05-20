package io.github.moehreag.axolotlclient.config.screen.widgets;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.moehreag.axolotlclient.config.options.ColorOption;
import io.github.moehreag.axolotlclient.config.options.FloatOption;
import io.github.moehreag.axolotlclient.config.Color;
import io.github.moehreag.axolotlclient.modules.hud.util.DrawUtil;
import io.github.moehreag.axolotlclient.modules.hud.util.Rectangle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class ColorSelectorWidget extends CustomButtonWidget {

    private final Identifier picker = new Identifier("axolotlclient", "textures/gui/colorwheel.png");
    private final Identifier alphabg = new Identifier("axolotlclient", "textures/gui/alphabg.png");
    private final ColorOption option;
    private final Rectangle current;
    public final TextFieldWidget textInput;
    private final CustomSliderWidget alphaSlider;

    public ColorSelectorWidget(int x, int y, @NotNull ColorOption option) {
        super(0, x, y, 75, 100, "", new Identifier("axolotlclient", "textures/gui/dialog2.png"));
        this.option=option;
        current=new Rectangle(x+18, y+70, 50, 15);
        textInput = new TextFieldWidget(0, MinecraftClient.getInstance().textRenderer, x+18, y+88, 50, 10);
        textInput.setMaxLength(9);
        textInput.setHasBorder(false);
        textInput.setText(option.get().toString());
        alphaSlider = new CustomSliderWidget(0, x+15, y+59, 52, 8, 0F, 10F, option.get().getAlpha()/25.5F, (FloatOption) null);
        alphaSlider.showText(false);
        alphaSlider.visible=true;

    }

    @Override
    public void render(MinecraftClient client, int mouseX, int mouseY) {
        MinecraftClient.getInstance().getTextureManager().bindTexture(texture);
        drawTexture(x, y, 0, 0, width, height, width, height);
        MinecraftClient.getInstance().getTextureManager().bindTexture(picker);
        drawTexture(x+18, y+5, 0, 0, 50, 50, 50, 50);

        MinecraftClient.getInstance().getTextureManager().bindTexture(alphabg);
        drawTexture(x+18, y+58, 0, 0, 50, 10, 50, 10);

        DrawUtil.fillRect(current, option.get());
        DrawUtil.outlineRect(current, Color.BLACK);

        textInput.render();

        if(!textInput.isFocused() && alphaSlider.focused){
            textInput.setText(option.get().toString());
        }
        alphaSlider.render(client, mouseX, mouseY);

        option.set(new Color(option.get().getRed(), option.get().getGreen(), option.get().getBlue(), (int) (alphaSlider.getSliderValue()*25.5F)));
        GlStateManager.color3f(1F, 1F, 1F);
    }

    public void onClick(int mouseX, int mouseY){
        if(textInput.isFocused()){
            this.option.set(Color.parse(textInput.getText()));
        }
        if(mouseX>=x+18 && mouseX<=x+65 && mouseY>=y+5 && mouseY <=y+55){
            int red = (mouseX-(x+41))*10;
            int blue = (mouseY-(y+30))*10;
            int green= mouseX<=x+46? (mouseX-(x+18))*10:0;
            green=green>0? (mouseY<y+30 && mouseX>x+41?green/4:255-green):0;

            if(mouseX==x+43 && mouseY<y+30){red=255;green=255;}
            if(mouseX<x+43 && mouseX>x+35 && mouseY<y+30)red+=green/2;
            if(mouseX>x+43 && mouseX<x+51 && mouseY<y+30)green+=red/2;

            if(red<0)red=0;
            if(blue<0)blue=0;
            if(green<0)green=0;
            //System.out.println("R: "+red+" G: "+green+" B: "+blue);
            option.set(new Color(red, green, blue, (int)(alphaSlider.getSliderValue() * 25.5F)));
            textInput.setText(option.get().toString());
        } else if(alphaSlider.isMouseOver(MinecraftClient.getInstance(), mouseX, mouseY)){
            option.set(new Color(option.get().getRed(), option.get().getGreen(), option.get().getBlue(), (int) (alphaSlider.getSliderValue()*25.5F)));
            textInput.setText(option.get().toString());
        }
        textInput.mouseClicked(mouseX, mouseY,0);

    }

    @Override
    public boolean isMouseOver(MinecraftClient client, int mouseX, int mouseY) {
        if(alphaSlider.isMouseOver(client, mouseX, mouseY)){
            option.set(new Color(option.get().getRed(), option.get().getGreen(), option.get().getBlue(), (int) (alphaSlider.getSliderValue()*25.5F)));

        }
        return super.isMouseOver(client, mouseX, mouseY);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        alphaSlider.mouseReleased(mouseX, mouseY);
        //if(alphaSlider.isMouseOver(MinecraftClient.getInstance(), mouseX, mouseY)){
        //option.set(new Color(option.get().getRed(), option.get().getGreen(), option.get().getBlue(), (int) (alphaSlider.getSliderValue()*25.5F)));
        //textInput.setText(option.get().toString());
        //}
    }

    public void tick(){
        textInput.tick();
    }

    public void keyPressed(char character, int code){
        textInput.keyPressed(character, code);
    }
}
