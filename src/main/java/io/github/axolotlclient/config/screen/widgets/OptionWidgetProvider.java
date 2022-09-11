package io.github.axolotlclient.config.screen.widgets;

import io.github.axolotlclient.config.options.*;
import net.minecraft.client.gui.widget.ClickableWidget;

public class OptionWidgetProvider {

    public static ClickableWidget getBooleanWidget(int x, int y, int width, int height, BooleanOption option){
        return new BooleanWidget(x, y, width, height, option);
    }

    public static ClickableWidget getStringWidget(int x, int y, StringOption option){
        return new StringOptionWidget(x, y, option);
    }

    public static ClickableWidget getSliderWidget(int x, int y, NumericOption<?> option){
        return new OptionSliderWidget<>(x, y, option);
    }

    public static ClickableWidget getColorWidget(int x, int y, ColorOption option){
        return new ColorOptionWidget(x, y, option);
    }

    public static ClickableWidget getEnumWidget(int x, int y, EnumOption option){
        return new EnumOptionWidget(x, y, option);
    }

    public static ClickableWidget getCategoryWidget(int x, int y, int width, int height, OptionCategory option){
        return new CategoryWidget(option, x, y, width, height);
    }
}
