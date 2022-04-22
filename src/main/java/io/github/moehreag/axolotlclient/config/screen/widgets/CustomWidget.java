package io.github.moehreag.axolotlclient.config.screen.widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.util.Identifier;

import java.util.Objects;

public class CustomWidget extends DrawableHelper {
    int x;
    int y;
    int width;
    int height;
    public static action onClick;
    MinecraftClient client = MinecraftClient.getInstance();
    boolean hovered;
    Identifier texture;
    String text;

    public CustomWidget(int x, int y, action onClick){
        this(x, y, 150, 20, "", onClick, null);
    }

    public CustomWidget(int x, int y, action onClick, Identifier texture){
        this(x, y, 150, 20, "", onClick, texture);
    }

    public CustomWidget(int x, int y, int width, int height, String text, action onClick){
        this(x, y, width, height, text, onClick, null);
    }

    public CustomWidget(int x, int y, int width, int height, action onClick, Identifier texture){
        this(x, y, width, height, "", onClick, texture);
    }

    public CustomWidget(int x, int y, int width, int height, String text, action onClick, Identifier texture){
        this.x=x;
        this.y=y;
        this.width=width;
        this.height=height;
        CustomWidget.onClick =onClick;
        this.texture=texture;
        this.text= I18n.translate(text);
    }

    public void render(){
        this.render(0,0);
    }
    
    public void render(int mouseX, int mouseY){
        if(mouseX!=0 && mouseY != 0)this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        if(this.texture!=null){
            client.getTextureManager().bindTexture(texture);
            drawTexture(x, y, 0, 0,width, height, width, height);
        }
        if(!Objects.equals(this.text, "")){
            drawCenteredString(this.client.textRenderer, text, x + width/2, y+(height-8)/2, -1);
        }
    }

    public void playDownSound() {
        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(new Identifier("gui.button.press"), 1.0F));
    }

    public CustomButtonWidget getDialog(){return null;}

    public boolean isHovered(int mouseX, int mouseY){
        return mouseX >= this.x && mouseY >= this.y && mouseX < (this.x + this.width) && mouseY < (this.y + this.height);
    }

    public boolean isHovered() {
        return hovered;
    }

    public interface action{
        void onClick(CustomWidget widget);
    }
}
