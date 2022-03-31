package io.github.moehreag.axolotlclient.config.screen.widgets;

import io.github.moehreag.axolotlclient.config.options.Option;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.util.Identifier;

import java.awt.*;

public class OptionWidget extends DrawableHelper{

    private final Option option;
    private final Identifier TEXTURE = new Identifier("axolotlclient", "textures/gui/optionwidget.png");
    private final int x;
    private final int y;
    private final int height = 20;
    private final int width = 150;
    public boolean visible = true;
    public boolean active = true;
    private boolean hovered;
    public static action action;

    public OptionWidget(Option option, int row, int line, int height, OptionWidget.action action){
        this.option = option;
        x = row;
        y=height/3+ line*20;
        OptionWidget.action =action;
    }

    public void render(MinecraftClient client, int mouseX, int mouseY){
        this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

        DrawableHelper.fill(x,y, x+width, y+height, new Color(50,50,50,50).hashCode());
        drawHorizontalLine(x, x+width, y, -167726);
        drawHorizontalLine(x, x+width, y+height, -167726);
        drawVerticalLine(x, y, y+height, -167726);
        drawVerticalLine(x+width, y, y+height, -167726);
        MinecraftClient.getInstance().textRenderer.drawWithShadow(option.getTranslatedName(), x+20, y+5, -1);

    }

    private String getMessage(){
        return this.option.getName();
    }

    public boolean isHovered(int mouseX, int mouseY){
        return mouseX >= this.x && mouseY >= this.y && mouseX < (this.x + this.width) && mouseY < (this.y + this.height);
    }

    public Option getOption(){
        return this.option;
    }

    public ButtonWidget getDialog(){
        return new ButtonWidget(929, x + 100, y, 75, 20, "Configure");
    }

    public interface action{
        void onClick(OptionWidget widget);
    }
}
