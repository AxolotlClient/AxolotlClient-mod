package io.github.moehreag.axolotlclient.config.screen;

import com.google.common.collect.Lists;
import io.github.moehreag.axolotlclient.config.options.*;
import io.github.moehreag.axolotlclient.config.screen.widgets.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.resource.language.I18n;

import java.util.List;

public class ButtonWidgetList extends EntryListWidget {

    private final List<Pair> entries = Lists.newArrayList();

    public ButtonWidgetList(MinecraftClient minecraftClient, int width, int height, int top, int bottom, int entryHeight, OptionCategory category) {
        super(minecraftClient, width, height, top, bottom, entryHeight);
        this.field_7735 = false;


        for (int i = 0; i < (category.getOptions().size()); i += 2) {

            Option option = category.getOptions().get(i);
            ButtonWidget buttonWidget = this.createWidget(width / 2 - 155, option);

            Option option2 = i < category.getOptions().size() - 1 ? category.getOptions().get(i + 1) : null;
            ButtonWidget buttonWidget2 = this.createWidget(width / 2 - 155 + 160, option2);
            this.entries.add(new Pair(buttonWidget, buttonWidget2));
        }

        for (int i = 0; i < category.getSubCategories().size(); i += 2) {
            OptionCategory subCat = category.getSubCategories().get(i);
            ButtonWidget buttonWidget = this.createCategoryWidget(width / 2 - 155, subCat);

            OptionCategory subCat2 = i < category.getSubCategories().size()-1 ? category.getSubCategories().get(i+1): null;
            ButtonWidget buttonWidget2 = this.createCategoryWidget(width / 2 - 155+160, subCat2);

            this.entries.add(new Pair(buttonWidget, buttonWidget2));
        }

    }

    private ButtonWidget createCategoryWidget(int x, OptionCategory cat){
        if(cat==null) {
            return null;
        } else {
            return new CategoryWidget(cat, x, 0,150, 20);
        }
    }

    private ButtonWidget createWidget(int x, Option option) {
        if (option == null) {
            return null;
        } else {
            int i = 0;
            if(option instanceof FloatOption) return new OptionSliderWidget(i, x, 0, (FloatOption) option);
            else if (option instanceof IntegerOption) return new OptionSliderWidget(i, x, 0, (IntegerOption) option);
            else if (option instanceof DoubleOption) return new OptionSliderWidget(i, x, 0, (DoubleOption) option);
            else if (option instanceof BooleanOption) return new OptionButtonWidget(i, x, 0, option, option.getName());
            else if (option instanceof StringOption) return new StringOptionWidget(i, x, 0, (StringOption) option);
            else if (option instanceof ColorOption) return new ColorOptionWidget(i, x, 0, (ColorOption) option);
            else if (option instanceof EnumOption) return new EnumOptionWidget(i, x, 0, (EnumOption) option);
            return null;
        }
    }

    @Override
    public Entry getEntry(int index) {
        return entries.get(index);
    }

    @Override
    protected int getEntryCount() {
        return entries.size();
    }

    public void keyPressed(char c, int code){
        entries.forEach(pair -> {
            if(pair.left instanceof StringOptionWidget && ((StringOptionWidget) pair.left).textField.isFocused()){
                ((StringOptionWidget) pair.left).textField.keyPressed(c, code);
            } else if(pair.right instanceof StringOptionWidget && ((StringOptionWidget) pair.right).textField.isFocused()){
                ((StringOptionWidget) pair.right).textField.keyPressed(c, code);
            }
        });
    }

    @Environment(EnvType.CLIENT)
    public static class Pair implements Entry {
        private final MinecraftClient client = MinecraftClient.getInstance();
        private final ButtonWidget left;
        private final ButtonWidget right;

        public Pair(ButtonWidget left, ButtonWidget right) {
            this.left = left;
            this.right = right;
        }

        public void render(int index, int x, int y, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean hovered) {
            if (this.left != null) {
                this.left.y = y;
                this.left.render(this.client, mouseX, mouseY);
            }

            if (this.right != null) {
                this.right.y = y;
                this.right.render(this.client, mouseX, mouseY);
            }

        }

        public boolean mouseClicked(int index, int mouseX, int mouseY, int button, int x, int y) {
            if (this.left.isMouseOver(this.client, mouseX, mouseY)) {
                onClick(this.left, mouseX, mouseY);

                return true;
            } else if (this.right != null && this.right.isMouseOver(this.client, mouseX, mouseY)) {
                onClick(this.right, mouseX, mouseY);

                return true;
            } else {
                return false;
            }
        }

        private void onClick(ButtonWidget button, int mouseX, int mouseY){
            if (button instanceof OptionButtonWidget) {
                ((BooleanOption)((OptionButtonWidget) button).getOption()).toggle();
                button.playDownSound(client.getSoundManager());
                button.message = I18n.translate(((OptionButtonWidget) button).getOption().getName())+": "+ I18n.translate ("options."+((((BooleanOption) ((OptionButtonWidget) button).getOption()).get()?"on":"off")));
            } else if (button instanceof OptionSliderWidget){
                button.isMouseOver(client, mouseX, mouseY);
            } else if (button instanceof CategoryWidget) {
                button.playDownSound(client.getSoundManager());
                client.openScreen(new OptionsScreenBuilder(client.currentScreen, ((CategoryWidget) button).category));
            } else if (button instanceof EnumOptionWidget) {
                button.playDownSound(client.getSoundManager());
                ((EnumOptionWidget) button).mouseClicked();
            } else if (button instanceof StringOptionWidget) {
                ((StringOptionWidget) button).textField.mouseClicked(mouseX, mouseY, 0);
            }
        }

        public void mouseReleased(int index, int mouseX, int mouseY, int button, int x, int y) {
            if (this.left != null) {
                this.left.mouseReleased(mouseX, mouseY);
            }

            if (this.right != null) {
                this.right.mouseReleased(mouseX, mouseY);
            }

        }

        public void updatePosition(int index, int x, int y) {
        }
    }
}
