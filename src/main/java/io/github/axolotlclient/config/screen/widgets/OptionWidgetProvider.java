package io.github.axolotlclient.config.screen.widgets;

import io.github.axolotlclient.config.options.*;
import net.minecraft.client.gui.widget.AbstractButtonWidget;

public class OptionWidgetProvider {

    public static AbstractButtonWidget getBooleanWidget(int x, int y, int width, int height, BooleanOption option){
        return new BooleanWidget(x, y, width, height, option);
    }

    public static AbstractButtonWidget getStringWidget(int x, int y, StringOption option){
        return new StringOptionWidget(x, y, option);
    }

    public static AbstractButtonWidget getFloatWidget(int x, int y, FloatOption option){
        return new OptionSliderWidget(x, y, option);
    }

    public static AbstractButtonWidget getDoubleWidget(int x, int y, DoubleOption option){
        return new OptionSliderWidget(x, y, option);
    }

    public static AbstractButtonWidget getIntegerWidget(int x, int y, IntegerOption option){
        return new OptionSliderWidget(x, y, option);
    }

    public static AbstractButtonWidget getColorWidget(int x, int y, ColorOption option){
        return new ColorOptionWidget(x, y, option);
    }

    public static AbstractButtonWidget getEnumWidget(int x, int y, EnumOption option){
        return new EnumOptionWidget(x, y, option);
    }

    public static AbstractButtonWidget getCategoryWidget(int x, int y, int width, int height, OptionCategory option){
        return new CategoryWidget(option, x, y, width, height);
    }
}
