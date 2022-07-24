package io.github.axolotlclient.config.screen.widgets;

import io.github.axolotlclient.config.options.BooleanOption;
import io.github.axolotlclient.config.options.ColorOption;
import io.github.axolotlclient.config.options.DoubleOption;
import io.github.axolotlclient.config.options.EnumOption;
import io.github.axolotlclient.config.options.FloatOption;
import io.github.axolotlclient.config.options.IntegerOption;
import io.github.axolotlclient.config.options.OptionCategory;
import io.github.axolotlclient.config.options.StringOption;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ClickableWidget;

public class OptionWidgetProvider {

    public static ClickableWidget getBooleanWidget(int x, int y, int width, int height, BooleanOption option){
        return new BooleanWidget(x, y, width, height, option);
    }

    public static ClickableWidget getStringWidget(int x, int y, StringOption option){
        return new StringOptionWidget(x, y, option);
    }

    public static ClickableWidget getFloatWidget(int x, int y, FloatOption option){
        return new OptionSliderWidget(x, y, option);
    }

    public static ClickableWidget getDoubleWidget(int x, int y, DoubleOption option){
        return new OptionSliderWidget(x, y, option);
    }

    public static ClickableWidget getIntegerWidget(int x, int y, IntegerOption option){
        return new OptionSliderWidget(x, y, option);
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
