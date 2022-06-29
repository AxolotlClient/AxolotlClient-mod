package io.github.axolotlclient.modules.scrollableTooltips;

import com.mojang.blaze3d.platform.InputUtil;
import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.config.options.BooleanOption;
import io.github.axolotlclient.config.options.IntegerOption;
import io.github.axolotlclient.config.options.OptionCategory;
import io.github.axolotlclient.modules.AbstractModule;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBind;
import net.minecraft.util.Identifier;

public class ScrollableTooltips extends AbstractModule {

    public static Identifier ID = new Identifier("scrollabletooltips");

    public int tooltipOffsetX;
    public int tooltipOffsetY;

    //protected KeyBind key = new KeyBind("scrollHorizontally", InputUtil.KEY_LEFT_SHIFT_CODE, "category.axolotlclient");

    public static ScrollableTooltips Instance = new ScrollableTooltips();

    private final OptionCategory category = new OptionCategory("scrollableTooltips");

    public final BooleanOption enabled = new BooleanOption("enabled", false);
    public final BooleanOption enableShiftHorizontalScroll = new BooleanOption("shiftHorizontalScroll", true);
    protected final IntegerOption scrollAmount = new IntegerOption("scrollAmount", 5, 1, 20);

    @Override
    public void init() {

        category.add(enabled);
        category.add(enableShiftHorizontalScroll);
        category.add(scrollAmount);

        AxolotlClient.CONFIG.rendering.addSubCategory(category);

        //KeyBindingHelper.registerKeyBinding(key); // Sorry, otherwise the keys would conflict and neither would work :/

    }

    public void onScroll(boolean reverse){

        if (Screen.hasShiftDown()) {
            if(reverse){
                tooltipOffsetX -= scrollAmount.get();

            } else {
                tooltipOffsetX += scrollAmount.get();

            }

        } else {
            if (reverse) {
                tooltipOffsetY -= scrollAmount.get();

            } else {
                tooltipOffsetY += scrollAmount.get();

            }
        }
    }

    public void resetScroll(){
        tooltipOffsetY = tooltipOffsetX = 0;
    }

}
