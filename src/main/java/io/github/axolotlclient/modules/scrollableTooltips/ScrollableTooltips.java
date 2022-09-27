package io.github.axolotlclient.modules.scrollableTooltips;

import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.AxolotlclientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlclientConfig.options.IntegerOption;
import io.github.axolotlclient.AxolotlclientConfig.options.OptionCategory;
import io.github.axolotlclient.modules.AbstractModule;
import net.minecraft.client.gui.screen.Screen;

public class ScrollableTooltips extends AbstractModule {

    public int tooltipOffsetX;
    public int tooltipOffsetY;

    private static final ScrollableTooltips Instance = new ScrollableTooltips();

    private final OptionCategory category = new OptionCategory("scrollableTooltips");

    public final BooleanOption enabled = new BooleanOption("enabled", false);
    public final BooleanOption enableShiftHorizontalScroll = new BooleanOption("shiftHorizontalScroll", true);
    protected final IntegerOption scrollAmount = new IntegerOption("scrollAmount", 5, 1, 20);
    protected final BooleanOption inverse = new BooleanOption("inverse", false);

    public static ScrollableTooltips getInstance(){
        return Instance;
    }

    @Override
    public void init() {

        category.add(enabled);
        category.add(enableShiftHorizontalScroll);
        category.add(scrollAmount);
        category.add(inverse);

        AxolotlClient.CONFIG.rendering.addSubCategory(category);
    }

    protected boolean applyInverse(boolean value){
        if(inverse.get()){
            return !value;
        }
        return value;
    }

    public void onScroll(boolean reverse){

        if (Screen.hasShiftDown()) {
            if(applyInverse(reverse)){
                tooltipOffsetX -= scrollAmount.get();

            } else {
                tooltipOffsetX += scrollAmount.get();

            }

        } else {
            if (applyInverse(reverse)) {
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
