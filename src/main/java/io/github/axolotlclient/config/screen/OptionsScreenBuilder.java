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
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OptionsScreenBuilder extends Screen {

    private final Screen parent;
    protected OptionCategory cat;

    protected ColorSelectionWidget picker;
    protected TextFieldWidget searchWidget;

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
        if(list.isMouseOver(mouseX, mouseY)) {
            return list.mouseScrolled(mouseX, mouseY, amount);
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
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
        boolean bl = super.mouseClicked(mouseX, mouseY, button);

        if(isPickerOpen()){
            if(!picker.isMouseOver(mouseX, mouseY)) {
                closeColorPicker();
                this.list.mouseClicked(mouseX, mouseY, button);

            } else {
                picker.onClick(mouseX, mouseY);
            }
        } else {
            searchWidget.mouseClicked(mouseX, mouseY, button);
            return bl || this.list.mouseClicked(mouseX, mouseY, button);
        }
        return bl;
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
        searchWidget.tick();
        if(isPickerOpen()){
            picker.tick();
        }
    }

    @Override
    public void init() {
        createWidgetList(cat);

		this.addSelectableChild(list);

        this.addDrawableChild(new ButtonWidget(this.width/2-100, this.height-40, 200, 20, Text.translatable("back"), buttonWidget -> {
            if(isPickerOpen()){
                closeColorPicker();
            }

            ConfigManager.save();
            MinecraftClient.getInstance().setScreen(parent);
        }));

        this.addDrawableChild(searchWidget = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, width - 120, 20, 100, 20, Text.translatable("search")){

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if(isMouseOver(mouseX, mouseY)) {
                    if (!isFocused() && super.mouseClicked(mouseX, mouseY, button) && cat.getName().equals("config")) {
                        MinecraftClient.getInstance().setScreen(new OptionsScreenBuilder(MinecraftClient.getInstance().currentScreen, getAllOptions()));
                        return true;
                    }
                    return super.mouseClicked(mouseX, mouseY, button);
                }
                setFocused(false);
                return false;
            }

            @Override
            public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
                super.renderButton(matrices, mouseX, mouseY, delta);

                drawVerticalLine(matrices, x-5, y-1, y+11, -1);
                drawHorizontalLine(matrices, x-5, x+width, y+11, -1);
            }
        });

        if(Objects.equals(cat.getName(), "config")) {
            this.addDrawableChild(new ButtonWidget(this.width - 106, this.height - 26, 100, 20, Text.translatable("credits"), buttonWidget -> MinecraftClient.getInstance().setScreen(new CreditsScreen(this))));
        } else {
            setInitialFocus(searchWidget);
        }
        searchWidget.setDrawsBackground(false);
        searchWidget.setSuggestion(Formatting.ITALIC + Text.translatable("search").append("...").getString());
        searchWidget.setChangedListener(s -> {
            list.filter(s);

            if(!s.equals("")){
                searchWidget.setSuggestion("");
            } else {
                searchWidget.setSuggestion(Formatting.ITALIC + Text.translatable("search").append("...").getString());
            }
        });
    }

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(isPickerOpen() && picker.keyPressed(keyCode, scanCode, modifiers)){
            return true;
        }
		//return this.list.keyPressed(keyCode, scanCode, modifiers) ||
        return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char chr, int modifiers) {
        if(isPickerOpen() && picker.charTyped(chr, modifiers)){
            return true;
        }
        return super.charTyped(chr, modifiers);
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

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        if(isPickerOpen()){
            picker.init();
        }
        ConfigManager.save();
        super.resize(client, width, height);
    }

    protected void createWidgetList(OptionCategory category){
        this.list = new ButtonWidgetList(client, this.width, height, 50, height-50, 25, category);
    }

    protected OptionCategory getAllOptions(){
        OptionCategory temp = new OptionCategory("", false);

        for(OptionCategory cat:AxolotlClient.CONFIG.getCategories()) {
            setupOptionsList(temp, cat);
        }

        List<OptionCategory> list = temp.getSubCategories();

        if(AxolotlClient.CONFIG.searchSort.get()){
            if(AxolotlClient.CONFIG.searchSortOrder.get().equals("ASCENDING")) {
                list.sort(new Tooltippable.AlphabeticalComparator());
            } else {
                list.sort(new Tooltippable.AlphabeticalComparator().reversed());
            }
        }

        return new OptionCategory("searchOptions", false)
            .addSubCategories(list);
    }

    protected void setupOptionsList(OptionCategory target, OptionCategory cat){
        target.addSubCategory(cat);
        if(!cat.getSubCategories().isEmpty()){
            for(OptionCategory sub : cat.getSubCategories()){
                setupOptionsList(target, sub);
            }
        }
    }
}
