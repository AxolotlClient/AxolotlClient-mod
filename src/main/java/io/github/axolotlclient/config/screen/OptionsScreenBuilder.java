package io.github.axolotlclient.config.screen;

import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.config.Color;
import io.github.axolotlclient.config.ConfigManager;
import io.github.axolotlclient.config.options.ColorOption;
import io.github.axolotlclient.config.options.OptionCategory;
import io.github.axolotlclient.config.options.Tooltippable;
import io.github.axolotlclient.config.screen.widgets.ColorSelectionWidget;
import io.github.axolotlclient.modules.hud.util.DrawUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

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
        if(AxolotlClient.someNiceBackground.get()) { // Credit to pridelib for the colors
            int alpha=MinecraftClient.getInstance().world==null?255:127;
            DrawUtil.fill(matrices, 0, 0, width, height/6, new Color(0xFFff0018).withAlpha(alpha).getAsInt());
            DrawUtil.fill(matrices, 0, height/6, width, height*2/6, new Color(0xFFffa52c).withAlpha(alpha).getAsInt());
            DrawUtil.fill(matrices, 0, height*2/6, width, height/2, new Color(0xFFffff41).withAlpha(alpha).getAsInt());
            DrawUtil.fill(matrices, 0, height*2/3, width, height*5/6, new Color(0xFF0000f9).withAlpha(alpha).getAsInt());
            DrawUtil.fill(matrices, 0, height/2, width, height*2/3, new Color(0xFF008018).withAlpha(alpha).getAsInt());
            DrawUtil.fill(matrices, 0, height*5/6, width, height, new Color(0xFF86007d).withAlpha(alpha).getAsInt());
        } else {
            if(MinecraftClient.getInstance().world!=null)DrawUtil.fill(matrices,0,0, width, height, 0xB0100E0E);
            else renderBackgroundTexture(0);
        }

        this.list.render(matrices, mouseX, mouseY, delta);
        drawCenteredText(matrices, textRenderer, cat.getTranslatedName(), width/2, 25, -1);

        if(picker!=null){
            picker.render(matrices, mouseX, mouseY, delta);
        } else {
            list.renderTooltips(matrices, mouseX, mouseY);
        }

        super.render(matrices, mouseX, mouseY, delta);
    }

    public void openColorPicker(ColorOption option){
        picker = new ColorSelectionWidget(option);
    }

    public void closeColorPicker(){
        ConfigManager.save();
        picker=null;
    }

    public boolean isPickerOpen(){
        return picker!=null;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return list.mouseScrolled(mouseX, mouseY, amount);
        //return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if(isPickerOpen()){
            return false;
        }
        return list.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if(picker!=null){
			if(!picker.isMouseOver(mouseX, mouseY)) {
				closeColorPicker();
			} else {
				picker.onClick(mouseX, mouseY);
			}
            return true;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if(isPickerOpen() && picker.mouseReleased(mouseX, mouseY, button)){
            return true;
        }
		return this.list.mouseReleased(mouseX, mouseY, button) || super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
    public void tick() {
        this.list.tick();
        if(isPickerOpen()){
            picker.tick();
        }
    }

    @Override
    public void init() {
        this.list = new ButtonWidgetList(this.client, this.width, this.height, 50, this.height - 50, 25, cat);

		this.addChild(list);

        this.addButton(new ButtonWidget(this.width/2-100, this.height-40, 200, 20, new TranslatableText("back"), buttonWidget -> {
            if(isPickerOpen()){
                closeColorPicker();
            }
            ConfigManager.save();
            MinecraftClient.getInstance().openScreen(parent);
        }));
        if(Objects.equals(cat.getName(), "config"))
            this.addButton(new ButtonWidget(this.width-106,
                this.height-26, 100, 20, new TranslatableText("credits"),
                buttonWidget -> MinecraftClient.getInstance().openScreen(new CreditsScreen(this))));
    }

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(isPickerOpen() && picker.keyPressed(keyCode, scanCode, modifiers)){
            return true;
        }
		return this.list.keyPressed(keyCode, scanCode, modifiers) || super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char chr, int modifiers) {
        if(isPickerOpen() && picker.charTyped(chr, modifiers)){
            return true;
        }
		return list.charTyped(chr, modifiers);
	}

	public void renderTooltip(MatrixStack matrices, Tooltippable option, int x, int y){
		List<Text> text = new ArrayList<>();
		String[] tooltip = Objects.requireNonNull(option.getTooltip()).getString().split("<br>");
		for(String s:tooltip) text.add(new LiteralText(s));
		this.renderTooltip(matrices, text, x, y);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        if(isPickerOpen()){
            picker.init();
        }
        ConfigManager.save();
        super.resize(client, width, height);
    }
}
