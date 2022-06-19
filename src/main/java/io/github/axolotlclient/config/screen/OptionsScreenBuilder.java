package io.github.axolotlclient.config.screen;

import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.config.options.ColorOption;
import io.github.axolotlclient.config.options.OptionCategory;
import io.github.axolotlclient.config.options.Tooltippable;
import io.github.axolotlclient.config.screen.widgets.ColorSelectionWidget;
import io.github.axolotlclient.modules.hud.util.DrawUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OptionsScreenBuilder extends Screen {

    private final Screen parent;
    protected OptionCategory cat;

    protected ColorSelectionWidget picker;

    private ButtonWidgetList list;

    public OptionsScreenBuilder(Screen parent, OptionCategory category){
	    super(Text.of(""));
	    this.parent=parent;
        this.cat=category;
    }

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if(MinecraftClient.getInstance().world!=null)DrawUtil.fill(matrices, 0,0, width, height, 0xB0100E0E);
        else renderBackgroundTexture(0);

        if(AxolotlClient.someNiceBackground.get()) { // Credit to pridelib for the colors
            DrawUtil.fill(matrices, 0, 0, width, height/6, 0xFFff0018);
            DrawUtil.fill(matrices, 0, height/6, width, height*2/6, 0xFFffa52c);
            DrawUtil.fill(matrices, 0, height*2/6, width, height/2, 0xFFffff41);
            DrawUtil.fill(matrices, 0, height*2/3, width, height*5/6, 0xFF0000f9);
            DrawUtil.fill(matrices, 0, height/2, width, height*2/3, 0xFF008018);
            DrawUtil.fill(matrices, 0, height*5/6, width, height, 0xFF86007d);
        }

        this.list.render(matrices, mouseX, mouseY, delta);
        drawCenteredText(matrices, textRenderer, cat.getTranslatedName(), width/2, 25, -1);

        if(picker!=null){
            picker.render(matrices, mouseX, mouseY, delta);
        }

        super.render(matrices, mouseX, mouseY, delta);
    }

    public void openColorPicker(ColorOption option){
        picker = new ColorSelectionWidget(option);
    }

    public void closeColorPicker(){
        picker=null;
    }

    public boolean isPickerOpen(){
        return picker!=null;
    }

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if(picker!=null){
			if(!picker.isMouseOver(mouseX, mouseY)) {
				closeColorPicker();
			} else {
				picker.onClick(mouseX, mouseY);
			}
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		return this.list.mouseReleased(mouseX, mouseY, button) || super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
    public void tick() {
        this.list.tick();
    }

    @Override
    public void init() {
        this.list = new ButtonWidgetList(this.client, this.width, this.height, 50, this.height - 50, 25, cat);

		this.addSelectableChild(list);

        this.addDrawableChild(new ButtonWidget(this.width/2-100, this.height-40, 200, 20, Text.translatable("back"), buttonWidget -> MinecraftClient.getInstance().setScreen(parent)));
        if(Objects.equals(cat.getName(), "config")) this.addDrawableChild(new ButtonWidget(this.width-106, this.height-26, 100, 20, Text.translatable("credits"), buttonWidget -> MinecraftClient.getInstance().setScreen(new CreditsScreen(this))));
    }

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return this.list.keyPressed(keyCode, scanCode, modifiers) || super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char chr, int modifiers) {
		return list.charTyped(chr, modifiers);
	}

	public void renderTooltip(MatrixStack matrices, Tooltippable option, int x, int y){
		List<Text> text = new ArrayList<>();
		String[] tooltip = Objects.requireNonNull(option.getTooltip()).getString().split("<br>");
		for(String s:tooltip) text.add(Text.literal(s));
		this.renderTooltip(matrices, text, x, y);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
