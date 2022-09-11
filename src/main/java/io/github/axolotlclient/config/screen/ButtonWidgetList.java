package io.github.axolotlclient.config.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.config.ConfigManager;
import io.github.axolotlclient.config.options.*;
import io.github.axolotlclient.config.screen.widgets.*;
import io.github.axolotlclient.modules.hud.util.DrawUtil;
import io.github.axolotlclient.modules.hud.util.Rectangle;
import io.github.axolotlclient.util.Util;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ButtonWidgetList extends EntryListWidget {

    public List<Pair> entries = Lists.newArrayList();

    private final OptionCategory category;

    public ButtonWidgetList(MinecraftClient minecraftClient, int width, int height, int top, int bottom, int entryHeight, OptionCategory category) {
        super(minecraftClient, width, height, top, bottom, entryHeight);
        this.category=category;

        if(!category.getSubCategories().isEmpty()) {
            for (int i = 0; i < category.getSubCategories().size(); i += 2) {
                OptionCategory subCat = category.getSubCategories().get(i);
                ButtonWidget buttonWidget = this.createCategoryWidget(width / 2 - 155, subCat);

                OptionCategory subCat2 = i < category.getSubCategories().size() - 1 ? category.getSubCategories().get(i + 1) : null;
                ButtonWidget buttonWidget2 = this.createCategoryWidget(width / 2 - 155 + 160, subCat2);

                this.entries.add(new CategoryPair(subCat, buttonWidget, subCat2, buttonWidget2));
            }
            this.entries.add(new Spacer());
        }

        for (int i = 0; i < (category.getOptions().size()); i ++) {

            Option option = category.getOptions().get(i);
            if(option.getName().equals("x")||option.getName().equals("y")) continue;
            ButtonWidget buttonWidget = this.createWidget(width / 2 - 155, option);

            this.entries.add(new OptionEntry(buttonWidget, option, width));
        }
    }

    private ButtonWidget createCategoryWidget(int x, OptionCategory cat){
        if(cat==null) {
            return null;
        } else {
            return OptionWidgetProvider.getCategoryWidget(x, 0,150, 20, cat);
        }
    }

    protected int getScrollbarPosition() {
        return super.getScrollbarPosition() + 32;
    }

    private ButtonWidget createWidget(int x, Option option) {
        if (option != null) {
            if (option instanceof NumericOption) return OptionWidgetProvider.getSliderWidget(x, 0, (NumericOption<?>) option);
            else if (option instanceof BooleanOption) return OptionWidgetProvider.getBooleanWidget(x, 0, 35, 20, (BooleanOption) option);
            else if (option instanceof StringOption) return OptionWidgetProvider.getStringWidget(x, 0, (StringOption) option);
            else if (option instanceof ColorOption) return OptionWidgetProvider.getColorWidget(x, 0, (ColorOption) option);
            else if (option instanceof EnumOption) return OptionWidgetProvider.getEnumWidget(x, 0, (EnumOption) option);
        }
        return null;
    }

    @Override
    public void render(int mouseX, int mouseY, float delta){
        if (this.visible) {
            GlStateManager.enableDepthTest();
            GlStateManager.pushMatrix();
            GlStateManager.translatef(0, 0, 1F);
            this.lastMouseX = mouseX;
            this.lastMouseY = mouseY;
            int i = this.getScrollbarPosition();
            int j = i + 6;
            this.capYPosition();
            GlStateManager.disableLighting();
            GlStateManager.disableFog();
            int k = this.xStart + this.width / 2 - 125;
            int l = this.yStart + 4 - (int)this.scrollAmount;


            int n = this.getMaxScroll();
            if (n > 0) {
                int o = (this.yEnd - this.yStart) * (this.yEnd - this.yStart) / this.getMaxPosition();
                o = MathHelper.clamp(o, 32, this.yEnd - this.yStart - 8);
                int p = (int)this.scrollAmount * (this.yEnd - this.yStart - o) / n + this.yStart;
                if (p < this.yStart) {
                    p = this.yStart;
                }
                GlStateManager.disableTexture();
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferBuilder = tessellator.getBuffer();

                bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
                bufferBuilder.vertex(i, this.yEnd, 0.0).texture(0.0, 1.0).color(0, 0, 0, 255).next();
                bufferBuilder.vertex(j, this.yEnd, 0.0).texture(1.0, 1.0).color(0, 0, 0, 255).next();
                bufferBuilder.vertex(j, this.yStart, 0.0).texture(1.0, 0.0).color(0, 0, 0, 255).next();
                bufferBuilder.vertex(i, this.yStart, 0.0).texture(0.0, 0.0).color(0, 0, 0, 255).next();
                tessellator.draw();
                bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
                bufferBuilder.vertex(i, (p + o), 0.0).texture(0.0, 1.0).color(128, 128, 128, 255).next();
                bufferBuilder.vertex(j, (p + o), 0.0).texture(1.0, 1.0).color(128, 128, 128, 255).next();
                bufferBuilder.vertex(j, p, 0.0).texture(1.0, 0.0).color(128, 128, 128, 255).next();
                bufferBuilder.vertex(i, p, 0.0).texture(0.0, 0.0).color(128, 128, 128, 255).next();
                tessellator.draw();
                bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
                bufferBuilder.vertex(i, (p + o - 1), 0.0).texture(0.0, 1.0).color(192, 192, 192, 255).next();
                bufferBuilder.vertex((j - 1), (p + o - 1), 0.0).texture(1.0, 1.0).color(192, 192, 192, 255).next();
                bufferBuilder.vertex((j - 1), p, 0.0).texture(1.0, 0.0).color(192, 192, 192, 255).next();
                bufferBuilder.vertex(i, p, 0.0).texture(0.0, 0.0).color(192, 192, 192, 255).next();
                tessellator.draw();
            }
            GlStateManager.enableTexture();

            this.renderList(k, l, mouseX, mouseY);

            GlStateManager.shadeModel(7424);
            GlStateManager.enableAlphaTest();
            //GlStateManager.disableDepthTest();
            GlStateManager.popMatrix();
            GlStateManager.disableBlend();
        }
    }

    protected void renderTooltips(int mouseX, int mouseY){
        Util.applyScissor(new Rectangle(0, yStart, this.width, yEnd-yStart));
        entries.forEach(pair -> pair.renderTooltips(mouseX, mouseY));
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    @Override
    public int getRowWidth() {
        return 400;
    }

    @Override
    protected void renderList(int x, int y, int mouseX, int mouseY) {
        Util.applyScissor(new Rectangle(0, yStart, this.width, yEnd-yStart));
        super.renderList(x, y, mouseX, mouseY);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    public void tick(){
        entries.forEach(pair -> {
            if(pair.left instanceof StringOptionWidget && ((StringOptionWidget) pair.left).textField.isFocused()){
                ((StringOptionWidget) pair.left).textField.tick();
            } else if(pair.right instanceof StringOptionWidget && ((StringOptionWidget) pair.right).textField.isFocused()){
                ((StringOptionWidget) pair.right).textField.tick();
            }

            if(pair.left instanceof ColorOptionWidget) {
                ((ColorOptionWidget) pair.left).tick();
            }
        });
    }

    public void filter(final String searchTerm) {
        entries.clear();

        Collection<Tooltippable> children = getEntries();

        List<Tooltippable> matched = children.stream().filter(tooltippable -> {
            if(AxolotlClient.CONFIG.searchForOptions.get() && tooltippable instanceof OptionCategory){
                if(((OptionCategory) tooltippable).getOptions().stream().anyMatch(option -> passesSearch(option.toString(), searchTerm))){
                    return true;
                }
            }
            return passesSearch(tooltippable.toString(), searchTerm);
        }).collect(Collectors.toList());

        if(!searchTerm.isEmpty() && AxolotlClient.CONFIG.searchSort.get()){
            if(AxolotlClient.CONFIG.searchSortOrder.get().equals("ASCENDING")) {
                matched.sort(new Tooltippable.AlphabeticalComparator());
            } else {
                matched.sort(new Tooltippable.AlphabeticalComparator().reversed());
            }
        }

        OptionCategory filtered = new OptionCategory(category.getName(), false);
        for (Tooltippable tooltippable : matched) {

            if(tooltippable instanceof OptionBase<?>){
                filtered.add((OptionBase<?>) tooltippable);
            } else if (tooltippable instanceof OptionCategory){
                filtered.addSubCategory((OptionCategory) tooltippable);
            }
        }
        entries = constructEntries(filtered);

        if (getScrollAmount() > Math.max(0, this.getMaxPosition() - (yEnd - this.yStart - 4))) {
            scrollAmount = Math.max(0, this.getMaxPosition() - (this.yEnd - this.yStart - 4));
        }
    }

    protected boolean passesSearch(String string, String search){
        if(AxolotlClient.CONFIG.searchIgnoreCase.get()) {
            return string.toLowerCase(Locale.ROOT).contains(search.toLowerCase(Locale.ROOT));
        }
        return string.contains(search);
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
                ButtonWidget buttonWidget = this.createCategoryWidget(width / 2 - 155, subCat);

                OptionCategory subCat2 = i < category.getSubCategories().size() - 1 ? category.getSubCategories().get(i + 1) : null;
                ButtonWidget buttonWidget2 = this.createCategoryWidget(width / 2 - 155 + 160, subCat2);

                entries.add(new CategoryPair(subCat, buttonWidget, subCat2, buttonWidget2));
            }
            entries.add(new Spacer());
        }

        for (int i = 0; i < (category.getOptions().size()); i ++) {

            Option option = category.getOptions().get(i);
            if(option.getName().equals("x")||option.getName().equals("y")) continue;
            ButtonWidget buttonWidget = this.createWidget(width / 2 - 155, option);

            entries.add(new OptionEntry(buttonWidget, option, width));
        }
        return entries;
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
                ((StringOptionWidget) pair.left).keyPressed(c, code);
            } else if(pair.right instanceof StringOptionWidget && ((StringOptionWidget) pair.right).textField.isFocused()){
                ((StringOptionWidget) pair.right).keyPressed(c, code);
            }

            if(pair.left instanceof ColorOptionWidget) {
                ((ColorOptionWidget) pair.left).keyPressed(c, code);
            }
        });
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
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

    @Environment(EnvType.CLIENT)
    public class Pair extends DrawUtil implements Entry {
        protected final MinecraftClient client = MinecraftClient.getInstance();
        protected final ButtonWidget left;
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

        public void renderTooltips(int mouseX, int mouseY){

        }

        protected void renderTooltip(Tooltippable option, int x, int y){
            if(isMouseInList(y) && MinecraftClient.getInstance().currentScreen instanceof OptionsScreenBuilder &&
                    AxolotlClient.CONFIG.showOptionTooltips.get() && option.getTooltip()!=null){
                GL11.glDisable(GL11.GL_SCISSOR_TEST);
                ((OptionsScreenBuilder) MinecraftClient.getInstance().currentScreen).renderTooltip(option, x, y);
                Util.applyScissor(new Rectangle(0, yStart, width, yEnd-yStart));
            }
        }

        public boolean mouseClicked(int index, int mouseX, int mouseY, int button, int x, int y) {
            if (this.left.isMouseOver(this.client, mouseX, mouseY)) {
                onClick(this.left, mouseX, mouseY, button);

                return true;
            } else if (this.right != null && this.right.isMouseOver(this.client, mouseX, mouseY)) {
                onClick(this.right, mouseX, mouseY, button);

                return true;
            }
            return false;
        }

        protected void onClick(ButtonWidget button, int mouseX, int mouseY, int mB){
            if (button instanceof OptionSliderWidget){
                button.isMouseOver(client, mouseX, mouseY);
                ConfigManager.save();
            } else if (button instanceof CategoryWidget) {
                ((CategoryWidget) button).mouseClicked(mouseX, mouseY);

            } else if (button instanceof EnumOptionWidget) {
                button.playDownSound(client.getSoundManager());
                ((EnumOptionWidget) button).mouseClicked(mB);
                ConfigManager.save();

            } else if (button instanceof StringOptionWidget) {
                ((StringOptionWidget) button).textField.mouseClicked(mouseX, mouseY, 0);
                ConfigManager.save();

            } else if (button instanceof BooleanWidget) {
                button.playDownSound(client.getSoundManager());
                ((BooleanWidget) button).mouseClicked(mouseX, mouseY, mB);
                ConfigManager.save();

            } else if (button instanceof ColorOptionWidget) {
                ((ColorOptionWidget) button).mouseClicked(mouseX, mouseY);
                ConfigManager.save();

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

    public class CategoryPair extends Pair {

        protected OptionCategory left;
        protected OptionCategory right;

        public CategoryPair(OptionCategory catLeft, ButtonWidget btnLeft, OptionCategory catRight, ButtonWidget btnRight) {
            super(btnLeft, btnRight);
            left = catLeft;
            right = catRight;
        }

        @Override
        public void render(int index, int x, int y, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean hovered) {
            super.render(index, x, y, rowWidth, rowHeight, mouseX, mouseY, hovered);

        }

        public void renderTooltips(int mouseX, int mouseY){
            if(AxolotlClient.CONFIG.showCategoryTooltips.get()) {
                if (super.left != null && super.left.isMouseOver(client, mouseX, mouseY)) {
                    if(AxolotlClient.CONFIG.quickToggles.get() && ((CategoryWidget)super.left).enabledButton != null && ((CategoryWidget)super.left).enabledButton.isMouseOver(client, mouseX, mouseY)){
                        renderTooltip(((CategoryWidget) super.left).enabledButton.option, mouseX, mouseY);
                    } else {
                        renderTooltip(left, mouseX, mouseY);
                    }
                }
                if (super.right != null && super.right.isMouseOver(client, mouseX, mouseY)) {
                    if(AxolotlClient.CONFIG.quickToggles.get() && ((CategoryWidget)super.right).enabledButton != null && ((CategoryWidget)super.right).enabledButton.isMouseOver(client, mouseX, mouseY)){
                        renderTooltip(((CategoryWidget) super.right).enabledButton.option, mouseX, mouseY);
                    } else {
                        renderTooltip(right, mouseX, mouseY);
                    }
                }
            }
        }
    }

    public class OptionEntry extends Pair {

        private final Option option;
        protected int renderX;

        public OptionEntry(ButtonWidget left, Option option, int width) {
            super(left, null);
            this.option = option;
            if(left instanceof BooleanWidget) left.x = width / 2 + 5 + 57;
            else left.x = width / 2 + 5;
        }

        @Override
        public void render(int index, int x, int y, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean hovered) {

            drawString(client.textRenderer, option.getTranslatedName(), x, y+5, -1, true);
            left.y = y;
            left.render(client, mouseX, mouseY);

            renderX = x;
        }

        public void renderTooltips(int mouseX, int mouseY){
            if(AxolotlClient.CONFIG.showOptionTooltips.get() &&
                    (mouseX>=renderX && mouseX<=left.x + left.getWidth() && mouseY>= left.y && mouseY<= left.y + 20)){
                renderTooltip(option, mouseX, mouseY);
            }
        }
    }

    public class Spacer extends Pair {

        public Spacer() {
            super(null, null);
        }

        @Override
        public void render(int index, int x, int y, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean hovered) {
        }

        @Override
        public boolean mouseClicked(int index, int mouseX, int mouseY, int button, int x, int y) {
            return false;
        }

        @Override
        public void mouseReleased(int index, int mouseX, int mouseY, int button, int x, int y) {
        }
    }
}
