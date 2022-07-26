package io.github.axolotlclient.modules.hud.gui.hud;

import io.github.axolotlclient.config.Color;
import io.github.axolotlclient.config.options.BooleanOption;
import io.github.axolotlclient.config.options.IntegerOption;
import io.github.axolotlclient.config.options.OptionBase;
import io.github.axolotlclient.modules.hud.gui.AbstractHudEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * This implementation of Hud modules is based on KronHUD.
 * https://github.com/DarkKronicle/KronHUD
 * Licensed under GPL-3.0
 */

public class ActionBarHud extends AbstractHudEntry {

    public static final Identifier ID = new Identifier("kronhud", "actionbarhud");

    public final IntegerOption timeShown = new IntegerOption("timeshown", 60, 40, 300);
	public final BooleanOption customTextColor = new BooleanOption("customtextcolor", false);

    private Text actionBar;
	private int ticksShown;
    private int color;
    MinecraftClient client;
	private final String placeholder = "Action Bar";

    public ActionBarHud() {
        super(115, 13);
        client = MinecraftClient.getInstance();
    }

    public void setActionBar(Text bar, int color){this.actionBar = bar; this.color = color;}

	@Override
	public void render(MatrixStack matrices) {
		if (ticksShown >= timeShown.get()){
			this.actionBar = null;
		}
		Color vanillaColor = new Color(color);
		if(this.actionBar != null) {

			matrices.push();
			scale(matrices);
            drawString(matrices, actionBar.asOrderedText().toString(), getPos().x, getPos().y + 3, customTextColor.get() ? (textColor.get().getAlpha()==255 ?
                new Color(
                    textColor.get().getRed(),
                    textColor.get().getGreen(),
                    textColor.get().getBlue(),
                    vanillaColor.getAlpha()):
                textColor.get()) :
                vanillaColor, shadow.get());
			matrices.pop();
			ticksShown++;
		} else {
			ticksShown = 0;
		}
	}

	@Override
	public void renderPlaceholder(MatrixStack matrices) {
		matrices.push();
		renderPlaceholderBackground(matrices);
		scale(matrices);
		client.textRenderer.draw(matrices, placeholder,  (float)getPos().x + Math.round((float) width /2) - (float) client.textRenderer.getWidth(placeholder) /2, (float)getPos().y + 3, -1);
		matrices.pop();
		hovered = false;
	}

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public boolean movable() {
        return true;
    }

    @Override
    public void addConfigOptions(List<OptionBase<?>> options){
        super.addConfigOptions(options);
        options.add(shadow);
    }
}
