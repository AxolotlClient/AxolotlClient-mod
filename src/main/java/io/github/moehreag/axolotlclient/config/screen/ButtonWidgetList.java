package io.github.moehreag.axolotlclient.config.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import io.github.moehreag.axolotlclient.config.options.*;
import io.github.moehreag.axolotlclient.config.screen.widgets.*;
import io.github.moehreag.axolotlclient.modules.hud.util.Rectangle;
import io.github.moehreag.axolotlclient.util.Util;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class ButtonWidgetList extends EntryListWidget {

    private final List<Pair> entries = Lists.newArrayList();

    //private final OptionCategory category; // Uncomment if needed one day

    public ButtonWidgetList(MinecraftClient minecraftClient, int width, int height, int top, int bottom, int entryHeight, OptionCategory category) {
        super(minecraftClient, width, height, top, bottom, entryHeight);
        this.field_7735 = false;
        //this.category=category; // same as above

        if(!category.getSubCategories().isEmpty()) {
            for (int i = 0; i < category.getSubCategories().size(); i += 2) {
                OptionCategory subCat = category.getSubCategories().get(i);
                ButtonWidget buttonWidget = this.createCategoryWidget(width / 2 - 155, subCat);

                OptionCategory subCat2 = i < category.getSubCategories().size() - 1 ? category.getSubCategories().get(i + 1) : null;
                ButtonWidget buttonWidget2 = this.createCategoryWidget(width / 2 - 155 + 160, subCat2);

                this.entries.add(new Pair(buttonWidget, buttonWidget2));
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
            return new CategoryWidget(cat, x, 0,150, 20);
        }
    }

    protected int getScrollbarPosition() {
        return super.getScrollbarPosition() + 32;
    }

    private ButtonWidget createWidget(int x, Option option) {
        if (option != null) {
            int i = 0;
            if (option instanceof FloatOption) return new OptionSliderWidget(i, x, 0, (FloatOption) option);
            else if (option instanceof IntegerOption) return new OptionSliderWidget(i, x, 0, (IntegerOption) option);
            else if (option instanceof DoubleOption) return new OptionSliderWidget(i, x, 0, (DoubleOption) option);
            else if (option instanceof BooleanOption) return new BooleanWidget(i, x, 0, 35, 20, (BooleanOption) option);
            else if (option instanceof StringOption) return new StringOptionWidget(i, x, 0, (StringOption) option);
            else if (option instanceof ColorOption) return new ColorOptionWidget(i, x, 0, (ColorOption) option);
            else if (option instanceof EnumOption) return new EnumOptionWidget(i, x, 0, (EnumOption) option);
        }
        return null;
    }

    @Override
    public void render(int mouseX, int mouseY, float delta){
        if (this.visible) {
            this.lastMouseX = mouseX;
            this.lastMouseY = mouseY;
            int i = this.getScrollbarPosition();
            int j = i + 6;
            this.capYPosition();
            GlStateManager.disableLighting();
            GlStateManager.disableFog();
            int k = this.xStart + this.width / 2 - 220 / 2 + 2;
            int l = this.yStart + 4 - (int)this.scrollAmount;

            this.renderList(k, l, mouseX, mouseY);
            GlStateManager.disableDepthTest();

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
            
            this.renderDecorations(mouseX, mouseY);
            GlStateManager.enableTexture();
            GlStateManager.shadeModel(7424);
            GlStateManager.enableAlphaTest();
            GlStateManager.disableBlend();
        }
    }

    @Override
    public int getRowWidth() {
        return 500;
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

            if(pair.left instanceof ColorOptionWidget) {
                ((ColorOptionWidget) pair.left).keyPressed(c, code);
            }
        });
    }

    @Environment(EnvType.CLIENT)
    public static class Pair extends DrawableHelper implements Entry {
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

        public boolean mouseClicked(int index, int mouseX, int mouseY, int button, int x, int y) {
            if (this.left.isMouseOver(this.client, mouseX, mouseY)) {
                onClick(this.left, mouseX, mouseY);

                return true;
            } else if (this.right != null && this.right.isMouseOver(this.client, mouseX, mouseY)) {
                onClick(this.right, mouseX, mouseY);

                return true;
            } else if (this.left instanceof StringOptionWidget && ((StringOptionWidget) this.left).textField.isFocused()) {
                ((StringOptionWidget) this.left).textField.setFocused(false);
                return true;
            }
            return false;
        }

        protected void onClick(ButtonWidget button, int mouseX, int mouseY){
            if (button instanceof OptionSliderWidget){
                button.isMouseOver(client, mouseX, mouseY);

            } else if (button instanceof CategoryWidget) {
                ((CategoryWidget) button).mouseClicked(mouseX, mouseY);

            } else if (button instanceof EnumOptionWidget) {
                button.playDownSound(client.getSoundManager());
                ((EnumOptionWidget) button).mouseClicked();

            } else if (button instanceof StringOptionWidget) {
                ((StringOptionWidget) button).textField.mouseClicked(mouseX, mouseY, 0);

            } else if (button instanceof BooleanWidget) {
                button.playDownSound(client.getSoundManager());
                ((BooleanWidget) button).option.toggle();
                ((BooleanWidget) button).updateMessage();

            } else if (button instanceof ColorOptionWidget) {
                ((ColorOptionWidget) button).mouseClicked(mouseX, mouseY);

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

    public static class OptionEntry extends Pair {

        private final Option option;

        public OptionEntry(ButtonWidget left, Option option, int width) {
            super(left, null);
            this.option = option;
            if(left instanceof BooleanWidget) left.x = width / 2 - 155 + 160 + 57;
            else left.x = width / 2 - 155 + 160;
        }

        @Override
        public void render(int index, int x, int y, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean hovered) {

            drawCenteredString(client.textRenderer, option.getTranslatedName(), x, y, -1);
            left.y = y;
            left.render(client, mouseX, mouseY);

        }
    }

    public static class Spacer extends Pair {

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
