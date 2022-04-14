package io.github.moehreag.axolotlclient.config.screen.widgets;

import io.github.moehreag.axolotlclient.Axolotlclient;
import io.github.moehreag.axolotlclient.config.options.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.util.Identifier;

public class OptionWidget extends CustomWidget{

    private final Option option;
    private final Identifier BUTTON_TEXTURE = new Identifier("axolotlclient", "textures/gui/button1.png");
    private final Identifier BUTTON2_TEXTURE = new Identifier("axolotlclient", "textures/gui/button2.png");
    private final Identifier DIALOG_TEXTURE= new Identifier("axolotlclient", "textures/gui/dialog.png");

    public OptionWidget(Option option, int row, int line, int height, CustomWidget.action action){
        super(row, height/3 + (line*20+line*2), action);
        this.option = option;
    }

    public OptionWidget(Option option, int row, int line, int width, int height, CustomWidget.action action){
        super(row, height/3 + (line*20+line*2), action);
        this.option = option;
        this.width=width;
    }

    public void render(MinecraftClient client, int mouseX, int mouseY){
        this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

        client.getTextureManager().bindTexture(BUTTON_TEXTURE);
        if(width==100)client.getTextureManager().bindTexture(BUTTON2_TEXTURE);
        drawTexture(x, y, 0, 0,width, height, width, height);
        drawCenteredString(this.client.textRenderer, option.getTranslatedName(), (int) (x+width/(float)2), y+5, -1);

    }

    public Option getOption(){
        return this.option;
    }

    public int getX(){
        return x;
    }

    public int getWidth(){
        return width;
    }

    public int getY(){
        return y;
    }

    public CustomButtonWidget getDialog(){
        //System.out.println(option.getType());
        if(option.getType() == OptionType.BOOLEAN){
            return new CustomButtonWidget(
                    2,
                    x+width+2,
                    y,
                    75,
                    20,
                    "options."+(((BooleanOption) option).get()?"on":"off"),
                    DIALOG_TEXTURE
            );
        } else if (option.getType() == OptionType.STRING){
            return new CustomButtonWidget(
                    4,
                    x+width+2,
                    y,
                    75,
                    20,
                    ((StringOption)option).get(),
                    DIALOG_TEXTURE,
                    true);
        } else if (option.getType() == OptionType.FLOAT){
            return new CustomButtonWidget(5,
                    x+width+2,
                    y,
                    75,
                    20,
                    "", DIALOG_TEXTURE, false, true, (FloatOption) option);
        } else if(option.getType() == OptionType.DOUBLE){
            return new CustomButtonWidget(5,
                    x+width+2,
                    y,
                    75,
                    20,
                    "", DIALOG_TEXTURE, false, true, (DoubleOption) option);
        } else if(option.getType() == OptionType.INT){
            return new CustomButtonWidget(5,
                    x+width+2,
                    y,
                    75,
                    20,
                    "", DIALOG_TEXTURE, false, true, (IntegerOption) option);
        } else if(option.getType() == OptionType.COLOR){
            return new ColorSelectorWidget(x+width+2, y, (ColorOption)option);
        } else if(option.getType() == OptionType.ENUM){
            return new EnumWidget(x+width+2, y, 75, 20, ((EnumOption)option).get());
        }

        Axolotlclient.LOGGER.warn("Configuration dialog building had some kind of error... This shouldn't happen!?");
        return new CustomButtonWidget(3, x+width+2, y+2, 75, 20,"No Configuration", DIALOG_TEXTURE);
    }
}
