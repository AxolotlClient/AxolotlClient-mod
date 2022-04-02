package io.github.moehreag.axolotlclient.config.screen.widgets;

import io.github.moehreag.axolotlclient.config.options.BooleanOption;
import io.github.moehreag.axolotlclient.config.options.Option;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.util.Identifier;

public class OptionWidget extends CustomWidget{

    private final Option option;
    private final Identifier BUTTON_TEXTURE = new Identifier("axolotlclient", "textures/gui/button1.png");
    private final Identifier CONFIGURE_TEXTURE= new Identifier("axolotlclient", "textures/gui/settings.png");
    /*private final int height = 20;
    private final int width = 150;*/
    public boolean visible = true;
    public boolean active = true;
    public static action action;

    public OptionWidget(Option option, int row, int line, int height, CustomWidget.action action){
        super(row, height/3 + (line*20+line*2), action);
        this.option = option;
        //OptionWidget.action =action;
    }

    public void render(MinecraftClient client, int mouseX, int mouseY){
        this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

        /*DrawableHelper.fill(x,y, x+width, y+height, new Color(50,50,50,50).hashCode());
        drawHorizontalLine(x, x+width, y, -167726);
        drawHorizontalLine(x, x+width, y+height, -167726);
        drawVerticalLine(x, y, y+height, -167726);
        drawVerticalLine(x+width, y, y+height, -167726);*/
        client.getTextureManager().bindTexture(BUTTON_TEXTURE);
        drawTexture(x, y, 0, 0,width, height, 150, 20);
        //client.getTextureManager().bindTexture(CONFIGURE_TEXTURE);
        //drawTexture(x+5, y+5, 0,0, 15, 15, );
        MinecraftClient.getInstance().textRenderer.drawWithShadow(option.getTranslatedName(), x+30, y+5, -1);

    }

    public boolean isHovered(int mouseX, int mouseY){
        return mouseX >= this.x && mouseY >= this.y && mouseX < (this.x + this.width) && mouseY < (this.y + this.height);
    }

    public Option getOption(){
        return this.option;
    }

    public ButtonWidget getDialog(){
        System.out.println(option instanceof BooleanOption);
        /*if(option instanceof BooleanOption){
            if(((BooleanOption) this.option).get())return new CustomWidget(x+100,
                    y+2,
                    50,
                    15,
                    "options."+(((BooleanOption) option).get()?"on":"off"),
                    widget -> ((BooleanOption) this.option).toggle(),
                    BUTTON_TEXTURE
            );
        }*/
        return new ButtonWidget(929, x + 100, y, 75, 20, "Configure");
        //return new CustomWidget(x+100, y, widget -> {}, BUTTON_TEXTURE);
    }

    public interface action{
        void onClick(OptionWidget widget);
    }
}
