package io.github.axolotlclient.config.screen.widgets;

import io.github.axolotlclient.config.options.*;
import net.minecraft.client.gui.widget.ButtonWidget;

public class OptionWidgetProvider {

    public static ButtonWidget getBooleanWidget(int x, int y, int width, int height, BooleanOption option){
        return new BooleanWidget(0, x, y, width, height, option);
    }

    public static ButtonWidget getStringWidget(int x, int y, StringOption option){
        return new StringOptionWidget(0, x, y, option);
    }

    public static ButtonWidget getSliderWidget(int x, int y, NumericOption<?> option){
        return new OptionSliderWidget<>(0, x, y, option);
    }

    public static ButtonWidget getColorWidget(int x, int y, ColorOption option){
        return new ColorOptionWidget(0, x, y, option);
    }

    public static ButtonWidget getEnumWidget(int x, int y, EnumOption option){
        return new EnumOptionWidget(0, x, y, option);
    }

    public static ButtonWidget getCategoryWidget(int x, int y, int width, int height, OptionCategory option){
        return new CategoryWidget(option, x, y, width, height);
    }
}
