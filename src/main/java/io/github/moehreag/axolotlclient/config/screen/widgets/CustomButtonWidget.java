package io.github.moehreag.axolotlclient.config.screen.widgets;

import io.github.moehreag.axolotlclient.config.options.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;
import org.lwjgl.input.Mouse;

import java.util.Objects;

public class CustomButtonWidget extends ButtonWidget {

    Identifier texture;
    String text;
    boolean textField;
    boolean slider;
    public TextFieldWidget textFieldWidget;
    public CustomSliderWidget sliderWidget;

    private final MinecraftClient client = MinecraftClient.getInstance();

    public CustomButtonWidget(int id, int x, int y, int width, int height, String message, Identifier texture) {
        super(id, x, y, width, height, message);
        this.texture=texture;
        this.text= I18n.translate(message);
    }

    public CustomButtonWidget(int id, int x, int y, int width, int height, String message, Identifier texture, boolean textField) {
        this(id, x, y, width, height, message, texture, textField, false, (FloatOption) null);
    }

    public CustomButtonWidget(int id, int x, int y, int width, int height, String message, Identifier texture, boolean textField, boolean slider, FloatOption option) {
        super(id, x, y, width, height, message);
        this.texture=texture;
        this.text= I18n.translate(message);
        this.textField=textField;
        if(textField){
            textFieldWidget = new TextFieldWidget(2, MinecraftClient.getInstance().textRenderer, x+12, y+(height-8/2), 50, 18);
            textFieldWidget.setHasBorder(false);
        }
        if(slider){
            sliderWidget = new CustomSliderWidget(3, x+12, y+1, 50, 18, option.getMin(), option.getMax(), option.get());
        }
    }

    public CustomButtonWidget(int id, int x, int y, int width, int height, String message, Identifier texture, boolean textField, boolean slider, DoubleOption option) {
        super(id, x, y, width, height, message);
        this.texture=texture;
        this.text= I18n.translate(message);
        this.textField=textField;
        if(textField){
            textFieldWidget = new TextFieldWidget(2, MinecraftClient.getInstance().textRenderer, x+12, y+(height-8/2), 50, 18);
            textFieldWidget.setHasBorder(false);
        }
        if(slider){
            sliderWidget = new CustomSliderWidget(3, x+12, y+1, 50, 18, option.getMin(), option.getMax(), option.get());
        }
    }

    public CustomButtonWidget(int id, int x, int y, int width, int height, String message, Identifier texture, boolean textField, boolean slider, IntegerOption option) {
        super(id, x, y, width, height, message);
        this.texture=texture;
        this.text= I18n.translate(message);
        this.textField=textField;
        if(textField){
            textFieldWidget = new TextFieldWidget(2, MinecraftClient.getInstance().textRenderer, x+12, y+(height-8/2), 50, 18);
            textFieldWidget.setHasBorder(false);
        }
        if(slider){
            sliderWidget = new CustomSliderWidget(3, x+12, y+1, 50, 18, option.getMin(), option.getMax(), option.get());
        }
    }

    public void setText(String text){
        this.text=text;
        this.render(MinecraftClient.getInstance(), Mouse.getX(), Mouse.getY());
    }

    @Override
    public void render(MinecraftClient client, int mouseX, int mouseY) {
        if(this.texture!=null){
            client.getTextureManager().bindTexture(texture);
            drawTexture(x, y, 0, 0,width, height, width, height);
        }
        if(!Objects.equals(this.text, "")){
            drawCenteredString(this.client.textRenderer, text, x + width/2, y+height/4, -1);
        }
        if(textField){
            textFieldWidget.render();
        } else if(slider){
            sliderWidget.render(client, Mouse.getX(), Mouse.getY());
            sliderWidget.focused=true;
        }
    }
}
