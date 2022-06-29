package io.github.axolotlclient.modules.scrollableTooltips;

import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.config.options.BooleanOption;
import io.github.axolotlclient.config.options.IntegerOption;
import io.github.axolotlclient.config.options.OptionCategory;
import io.github.axolotlclient.modules.AbstractModule;
import net.legacyfabric.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.util.Identifier;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class ScrollableTooltips extends AbstractModule {

    public static Identifier ID = new Identifier("scrollabletooltips");

    public int tooltipOffsetX;
    public int tooltipOffsetY;

    protected KeyBinding key = new KeyBinding("scrollHorizontally", Keyboard.KEY_LSHIFT, "category.axolotlclient");

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

        KeyBindingHelper.registerKeyBinding(key);

    }

    public void onRenderTooltip(){
        if(enabled.get()) {

            int i = Mouse.getDWheel();
            if (i != 0) {

                if (i < 0) {
                    onScroll(false);
                }

                if (i > 0) {
                    onScroll(true);
                }
            }
        }
    }

    public void onScroll(boolean reverse){

        if ((Screen.hasShiftDown() && key.getCode() == Keyboard.KEY_LSHIFT) || key.isPressed()) {
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
