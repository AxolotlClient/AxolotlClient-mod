package io.github.axolotlclient.config.screen;

import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.config.ConfigManager;
import io.github.axolotlclient.config.options.BooleanOption;
import io.github.axolotlclient.config.options.ColorOption;
import io.github.axolotlclient.config.options.DoubleOption;
import io.github.axolotlclient.config.options.EnumOption;
import io.github.axolotlclient.config.options.FloatOption;
import io.github.axolotlclient.config.options.IntegerOption;
import io.github.axolotlclient.config.options.Option;
import io.github.axolotlclient.config.options.OptionBase;
import io.github.axolotlclient.config.options.OptionCategory;
import io.github.axolotlclient.config.options.StringOption;
import io.github.axolotlclient.config.options.Tooltippable;
import io.github.axolotlclient.config.screen.widgets.BooleanWidget;
import io.github.axolotlclient.config.screen.widgets.CategoryWidget;
import io.github.axolotlclient.config.screen.widgets.ColorOptionWidget;
import io.github.axolotlclient.config.screen.widgets.EnumOptionWidget;
import io.github.axolotlclient.config.screen.widgets.OptionSliderWidget;
import io.github.axolotlclient.config.screen.widgets.OptionWidgetProvider;
import io.github.axolotlclient.config.screen.widgets.StringOptionWidget;
import io.github.axolotlclient.modules.hud.util.Rectangle;
import io.github.axolotlclient.util.Util;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

public class ButtonWidgetList extends AlwaysSelectedEntryListWidget<ButtonWidgetList.Pair> {

    public List<Pair> entries;

    private final OptionCategory category;

    public ButtonWidgetList(MinecraftClient minecraftClient, int width, int height, int top, int bottom, int entryHeight, OptionCategory category) {
        super(minecraftClient, width, height, top, bottom, entryHeight);

        this.setRenderBackground(false);
        this.setRenderHorizontalShadows(false);
        this.setRenderHeader(false, 0);
        this.setRenderSelection(false);
        this.category=category; // same as above

        this.entries = constructEntries(category);
        for(Pair p:entries){
            addEntry(p);
        }
    }



    @Override
	protected int addEntry(Pair entry) {
		//if(entry != null) this.entries.add(entry);
		return super.addEntry(entry);
	}

	private ClickableWidget createCategoryWidget(int x, OptionCategory cat){
        if(cat==null) {
            return null;
        } else {
            return OptionWidgetProvider.getCategoryWidget(x, 0,150, 20, cat);
        }
    }

    private ClickableWidget createWidget(int x, Option option) {
        if (option != null) {
            if (option instanceof FloatOption) return OptionWidgetProvider.getFloatWidget(x, 0, (FloatOption) option);
            else if (option instanceof IntegerOption) return OptionWidgetProvider.getIntegerWidget(x, 0, (IntegerOption) option);
            else if (option instanceof DoubleOption) return OptionWidgetProvider.getDoubleWidget(x, 0, (DoubleOption) option);
            else if (option instanceof BooleanOption) return OptionWidgetProvider.getBooleanWidget(x, 0, 35, 20, (BooleanOption) option);
            else if (option instanceof StringOption) return OptionWidgetProvider.getStringWidget(x, 0, (StringOption) option);
            else if (option instanceof ColorOption) return OptionWidgetProvider.getColorWidget(x, 0, (ColorOption) option);
            else if (option instanceof EnumOption) return OptionWidgetProvider.getEnumWidget(x, 0, (EnumOption) option);
        }
        return null;
    }

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		setFocused(null);
		boolean bl = super.mouseClicked(mouseX, mouseY, button);
        entries.forEach(pair -> {
            if(pair.left instanceof StringOptionWidget && ((StringOptionWidget) pair.left).textField.isFocused()){
                ((StringOptionWidget) pair.left).textField.mouseClicked(mouseX, mouseY, button);
            }
            if(pair.left instanceof ColorOptionWidget){
                if(((ColorOptionWidget) pair.left).textField.isFocused()) {
                    ((ColorOptionWidget) pair.left).textField.mouseClicked(mouseX, mouseY, button);
                }
            }
        });
        return bl;
	}

    @Override
    public int getRowWidth() {
        return 400;
    }

    @Override
	public int getRowLeft() {
		return this.width / 2 -155;
	}

    public void renderTooltips(MatrixStack matrices, int mouseX, int mouseY){
        Util.applyScissor(new Rectangle(0, top, this.width, bottom-top));
        entries.forEach(pair -> pair.renderTooltips(matrices, mouseX, mouseY));
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

	@Override
	protected void renderList(MatrixStack matrices, int x, int y, float delta) {
		Util.applyScissor(new Rectangle(0, top, this.width, bottom-top));
		super.renderList(matrices, x, y, delta);
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}

    @Override
    protected int getScrollbarPositionX() {
        return super.getScrollbarPositionX() + 32;
    }

    private boolean halftick = true;

    public void tick(){
		if(halftick) {
			for (Pair pair : entries) {
				if (pair.tickable) pair.tick();
			}
		}
	    halftick=!halftick;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers){
        for (Pair pair:entries) if(pair.keyPressed(keyCode, scanCode, modifiers)){
			return true;
        }
        if (getSelectedOrNull() != null) {
            return getSelectedOrNull().keyPressed(keyCode, scanCode, modifiers);
        }
	    return super.keyPressed(keyCode, scanCode, modifiers);
    }

    protected void m_enzpxkzi(MatrixStack matrices, int i, int j, float f, int k, int l, int m, int n, int o) {
        Pair entry = this.getEntry(k);

        entry.render(matrices, k, m, l, n, o, i, j, Objects.equals(getSelectedOrNull(), entry), f);
    }

	public boolean charTyped(char c, int modifiers){
		for(Pair pair:entries){
			if(pair.charTyped(c, modifiers)) return true;
		}
		return false;
	}

    public void filter(String searchTerm) {
        this.clearEntries();
        entries.clear();

        Collection<Tooltippable> children = getEntries();

        List<Tooltippable> matched = children.stream().filter(tooltippable -> {
            return tooltippable.toString().toLowerCase(Locale.ROOT).contains(searchTerm.toLowerCase(Locale.ROOT));
        }).collect(Collectors.toList());

        if(!searchTerm.isEmpty()){
            matched.sort(new Tooltippable.AlphabeticalComparator());
        }

        OptionCategory filtered = new OptionCategory(category.getName());
        for (Tooltippable tooltippable : matched) {

            if(tooltippable instanceof OptionBase<?>){
                filtered.add((OptionBase<?>) tooltippable);
            } else if (tooltippable instanceof OptionCategory){
                filtered.addSubCategory((OptionCategory) tooltippable);
            }
        }
        entries = constructEntries(filtered);
        for(Pair p:entries){
            addEntry(p);
        }


        if (getScrollAmount() > Math.max(0, this.getMaxPosition() - (this.bottom - this.top - 4))) {
            setScrollAmount(Math.max(0, this.getMaxPosition() - (this.bottom - this.top - 4)));
        }
    }

    protected List<Tooltippable> getEntries(){
        List<Tooltippable> list = new ArrayList<>(category.getSubCategories());
        list.addAll(category.getOptions());
        return list;
    }

    protected List<Pair> constructEntries(OptionCategory category){
        List<Pair> entries = new ArrayList<>();
        if(!category.getSubCategories().isEmpty()) {
            for (int i = 0; i < category.getSubCategories().size(); i += 2) {
                OptionCategory subCat = category.getSubCategories().get(i);
                ClickableWidget buttonWidget = this.createCategoryWidget(width / 2 - 155, subCat);

                OptionCategory subCat2 = i < category.getSubCategories().size() - 1 ? category.getSubCategories().get(i + 1) : null;
                ClickableWidget buttonWidget2 = this.createCategoryWidget(width / 2 - 155 + 160, subCat2);

                entries.add(new CategoryPair(subCat, buttonWidget, subCat2, buttonWidget2));
            }
            entries.add(new Spacer());
        }

        for (int i = 0; i < (category.getOptions().size()); i ++) {

            Option option = category.getOptions().get(i);
            if(option.getName().equals("x")||option.getName().equals("y")) continue;
            ClickableWidget buttonWidget = this.createWidget(width / 2 - 155+160, option);

            //addEntry(new OptionEntry(buttonWidget, option, width));
            entries.add(new OptionEntry(buttonWidget, option, width));
        }
        return entries;
    }

    @Environment(EnvType.CLIENT)
    public class Pair extends AlwaysSelectedEntryListWidget.Entry<Pair> {
        protected final MinecraftClient client = MinecraftClient.getInstance();
        protected final ClickableWidget left;
        protected final ClickableWidget right;

		protected final boolean tickable;

        public Pair(ClickableWidget left, ClickableWidget right) {
			super();
	        this.left = left;
            this.right = right;

			tickable = left instanceof StringOptionWidget || left instanceof ColorOptionWidget;
        }

	    @Override
	    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {

            if (this.left != null) {
                this.left.y = y;
                this.left.render(matrices, mouseX, mouseY, tickDelta);
            }

            if (this.right != null) {
                this.right.y = y;
                this.right.render(matrices, mouseX, mouseY, tickDelta);
            }

        }

        public void renderTooltips(MatrixStack matrices, int mouseX, int mouseY){

        }

		protected void renderTooltip(MatrixStack matrices, Tooltippable option, int x, int y){
			if(MinecraftClient.getInstance().currentScreen instanceof OptionsScreenBuilder &&
				AxolotlClient.CONFIG.showOptionTooltips.get() && option.getTooltip()!=null){
				GL11.glDisable(GL11.GL_SCISSOR_TEST);
				((OptionsScreenBuilder) MinecraftClient.getInstance().currentScreen).renderTooltip(matrices, option, x, y);
				Util.applyScissor(new Rectangle(0, top, width, bottom-top));
			}
		}

        @Override
	    public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (this.left.isMouseOver(mouseX, mouseY)) {
                onClick(this.left, mouseX, mouseY, button);

                return true;
            } else if (this.right != null && this.right.isMouseOver(mouseX, mouseY)) {
                onClick(this.right, mouseX, mouseY, button);

                return true;
            }
            return false;
        }

        protected void onClick(ClickableWidget button, double mouseX, double mouseY, int mB){
            if (button instanceof OptionSliderWidget){
                button.isMouseOver(mouseX, mouseY);
                ConfigManager.save();
            } else if (button instanceof CategoryWidget) {
                button.mouseClicked(mouseX, mouseY, mB);

            } else if (button instanceof EnumOptionWidget) {
                button.playDownSound(client.getSoundManager());
                button.mouseClicked(mouseX, mouseY, mB);
                ConfigManager.save();

            } else if (button instanceof StringOptionWidget) {
                ((StringOptionWidget) button).textField.mouseClicked(mouseX, mouseY, 0);
                ConfigManager.save();

            } else if (button instanceof BooleanWidget) {
                button.playDownSound(client.getSoundManager());
                ((BooleanWidget) button).option.toggle();
                ConfigManager.save();

            } else if (button instanceof ColorOptionWidget) {
                button.mouseClicked(mouseX, mouseY, 0);
                ConfigManager.save();

            }

        }

		@Override
		public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
			if(left!=null && left.keyPressed(keyCode, scanCode, modifiers)) return true;
			return super.keyPressed(keyCode, scanCode, modifiers);
		}

		@Override
		public boolean charTyped(char c, int modifiers) {
			return this.left != null && left.charTyped(c, modifiers);
		}

		@Override
	    public boolean mouseReleased(double mouseX, double mouseY, int button) {
            if (this.left != null) {
                return this.left.mouseReleased(mouseX, mouseY, button);
            }

            if (this.right != null) {
                return this.right.mouseReleased(mouseX, mouseY, button);
            }

		    return super.mouseReleased(mouseX, mouseY, button);

        }

		public void tick(){
			if(left instanceof StringOptionWidget) ((StringOptionWidget) left).tick();
			else if (left instanceof ColorOptionWidget) ((ColorOptionWidget) left).tick();

		}

        @Override
        public Text getNarration() {
            return null;
        }
    }

	public class CategoryPair extends Pair {

		protected OptionCategory left;
		protected OptionCategory right;

		public CategoryPair(OptionCategory catLeft, ClickableWidget btnLeft, OptionCategory catRight, ClickableWidget btnRight) {
			super(btnLeft, btnRight);
			left = catLeft;
			right = catRight;
		}

		@Override
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            super.render(matrices, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickDelta);

        }

        @Override
        public void renderTooltips(MatrixStack matrices, int mouseX, int mouseY) {
            if(AxolotlClient.CONFIG.showCategoryTooltips.get()) {
				if (super.left != null && super.left.isMouseOver(mouseX, mouseY)) {
                    if(AxolotlClient.CONFIG.quickToggles.get() && ((CategoryWidget)super.left).enabledButton != null && ((CategoryWidget)super.left).enabledButton.isMouseOver(mouseX, mouseY)){
                        renderTooltip(matrices, ((CategoryWidget) super.left).enabledButton.option, mouseX, mouseY);
                    } else {
                        renderTooltip(matrices, left, mouseX, mouseY);
                    }
				}
				if (super.right != null && super.right.isMouseOver(mouseX, mouseY)) {
                    if(AxolotlClient.CONFIG.quickToggles.get() && ((CategoryWidget)super.right).enabledButton != null && ((CategoryWidget)super.right).enabledButton.isMouseOver(mouseX, mouseY)){
                        renderTooltip(matrices, ((CategoryWidget) super.right).enabledButton.option, mouseX, mouseY);
                    } else {
                        renderTooltip(matrices, right, mouseX, mouseY);
                    }
				}
			}
		}
	}

    public class OptionEntry extends Pair {

        private final Option option;
		protected int width;
        protected int renderX;

        public OptionEntry(ClickableWidget left, Option option, int width) {
            super(left, null);
            this.option = option;
            if(left instanceof BooleanWidget) left.x = width / 2 + 5 + 57;
            else left.x = width / 2 + 5;
			this.width=width;
        }

	    @Override
	    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {

            DrawableHelper.drawTextWithShadow(matrices, client.textRenderer, option.getTranslatedName(), x, y + 5, -1);
            left.y = y;
            left.render(matrices, mouseX, mouseY, tickDelta);

            renderX = x;
        }

        @Override
        public void renderTooltips(MatrixStack matrices, int mouseX, int mouseY) {
		    if(AxolotlClient.CONFIG.showOptionTooltips.get() &&
			    mouseX>=renderX && mouseX<=left.x + left.getWidth() && mouseY>= left.y && mouseY<= left.y + 20){
			    renderTooltip(matrices, option, mouseX, mouseY);
		    }

        }
    }

    public class Spacer extends Pair {

        public Spacer() {
            super(null, null);
        }

	    @Override
	    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
	    }

	    @Override
	    public boolean mouseClicked(double mouseX, double mouseY, int button) {
			return false;
	    }

	    @Override
	    public boolean mouseReleased(double mouseX, double mouseY, int button) {
		    return false;
	    }

	    @Override
	    public boolean changeFocus(boolean lookForwards) {
		    return false;
	    }
    }
}
