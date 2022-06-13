package io.github.moehreag.axolotlclient.config.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormats;
import io.github.moehreag.axolotlclient.config.ConfigManager;
import io.github.moehreag.axolotlclient.config.options.BooleanOption;
import io.github.moehreag.axolotlclient.config.options.ColorOption;
import io.github.moehreag.axolotlclient.config.options.DoubleOption;
import io.github.moehreag.axolotlclient.config.options.EnumOption;
import io.github.moehreag.axolotlclient.config.options.FloatOption;
import io.github.moehreag.axolotlclient.config.options.IntegerOption;
import io.github.moehreag.axolotlclient.config.options.Option;
import io.github.moehreag.axolotlclient.config.options.OptionCategory;
import io.github.moehreag.axolotlclient.config.options.StringOption;
import io.github.moehreag.axolotlclient.config.screen.widgets.BooleanWidget;
import io.github.moehreag.axolotlclient.config.screen.widgets.CategoryWidget;
import io.github.moehreag.axolotlclient.config.screen.widgets.ColorOptionWidget;
import io.github.moehreag.axolotlclient.config.screen.widgets.EnumOptionWidget;
import io.github.moehreag.axolotlclient.config.screen.widgets.OptionSliderWidget;
import io.github.moehreag.axolotlclient.config.screen.widgets.StringOptionWidget;
import io.github.moehreag.axolotlclient.mixin.AccessorButtonListWidget;
import io.github.moehreag.axolotlclient.modules.hud.util.Rectangle;
import io.github.moehreag.axolotlclient.util.Util;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ButtonWidgetList extends ButtonListWidget {

    public final List<Pair> entries = new ArrayList<>();

    //private final OptionCategory category; // Uncomment if needed one day

    public ButtonWidgetList(MinecraftClient minecraftClient, int width, int height, int top, int bottom, int entryHeight, OptionCategory category) {
        super(minecraftClient, width, height, top, bottom, entryHeight);
        //this.category=category; // same as above

        if(!category.getSubCategories().isEmpty()) {
            for (int i = 0; i < category.getSubCategories().size(); i += 2) {
                OptionCategory subCat = category.getSubCategories().get(i);
                ButtonWidget buttonWidget = this.createCategoryWidget(width / 2 - 155, subCat);

                OptionCategory subCat2 = i < category.getSubCategories().size() - 1 ? category.getSubCategories().get(i + 1) : null;
                ButtonWidget buttonWidget2 = this.createCategoryWidget(width / 2 - 155 + 160, subCat2);

	            this.addEntry(new Pair(buttonWidget, buttonWidget2));
				this.entries.add(new Pair(buttonWidget, buttonWidget2));
            }
            this.addEntry(new Spacer());
			this.entries.add(new Spacer());
        }

        for (int i = 0; i < (category.getOptions().size()); i ++) {

            Option option = category.getOptions().get(i);
            if(option.getName().equals("x")||option.getName().equals("y")) continue;
            ClickableWidget buttonWidget = this.createWidget(width / 2 - 155, option);

            addEntry(new OptionEntry(buttonWidget, option, width));
			this.entries.add(new OptionEntry(buttonWidget, option, width));
        }
    }

	@Override
	protected int addEntry(ButtonEntry entry) {
		if(entry instanceof Pair) this.entries.add((Pair)entry);
		return super.addEntry(entry);
	}

	private ButtonWidget createCategoryWidget(int x, OptionCategory cat){
        if(cat==null) {
            return null;
        } else {
            return new CategoryWidget(cat, x, 0,150, 20);
        }
    }

    private ClickableWidget createWidget(int x, Option option) {
        if (option != null) {
            if (option instanceof FloatOption) return new OptionSliderWidget(x, 0, (FloatOption) option);
            else if (option instanceof IntegerOption) return new OptionSliderWidget(x, 0, (IntegerOption) option);
            else if (option instanceof DoubleOption) return new OptionSliderWidget(x, 0, (DoubleOption) option);
            else if (option instanceof BooleanOption) return new BooleanWidget(x, 0, 35, 20, (BooleanOption) option);
            else if (option instanceof StringOption) return new StringOptionWidget(x, 0, (StringOption) option);
            else if (option instanceof ColorOption) return new ColorOptionWidget(x, 0, (ColorOption) option);
            else if (option instanceof EnumOption) return new EnumOptionWidget(x, 0, (EnumOption) option);
        }
        return null;
    }

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		int i = this.getScrollbarPositionX();
		int j = i + 6;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
		((AccessorButtonListWidget)this).setHoveredEntry(this.isMouseOver(mouseX, mouseY) ? this.getEntryAtPosition(mouseX, mouseY) : null);
		/*if (this.renderBackground) {
			RenderSystem.setShaderTexture(0, DrawableHelper.OPTIONS_BACKGROUND_TEXTURE);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			float f = 32.0F;
			bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
			bufferBuilder.vertex(this.left, this.bottom, 0.0)
				.uv((float)this.left / 32.0F, (float)(this.bottom + (int)this.getScrollAmount()) / 32.0F)
				.color(32, 32, 32, 255)
				.next();
			bufferBuilder.vertex(this.right, this.bottom, 0.0)
				.uv((float)this.right / 32.0F, (float)(this.bottom + (int)this.getScrollAmount()) / 32.0F)
				.color(32, 32, 32, 255)
				.next();
			bufferBuilder.vertex(this.right, this.top, 0.0)
				.uv((float)this.right / 32.0F, (float)(this.top + (int)this.getScrollAmount()) / 32.0F)
				.color(32, 32, 32, 255)
				.next();
			bufferBuilder.vertex(this.left, this.top, 0.0)
				.uv((float)this.left / 32.0F, (float)(this.top + (int)this.getScrollAmount()) / 32.0F)
				.color(32, 32, 32, 255)
				.next();
			tessellator.draw();
		}*/

		int k = this.getRowLeft();
		int l = this.top + 4 - (int)this.getScrollAmount();

		this.renderList(matrices, k, l, mouseX, mouseY, delta);
		if (MinecraftClient.getInstance().world == null) {
			RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
			RenderSystem.setShaderTexture(0, DrawableHelper.OPTIONS_BACKGROUND_TEXTURE);
			RenderSystem.enableDepthTest();
			RenderSystem.depthFunc(519);
			bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
			bufferBuilder.vertex(this.left, this.top, -100.0).uv(0.0F, (float)this.top / 32.0F).color(64, 64, 64, 255).next();
			bufferBuilder.vertex((this.left + this.width), this.top, -100.0)
				.uv((float)this.width / 32.0F, (float)this.top / 32.0F)
				.color(64, 64, 64, 255)
				.next();
			bufferBuilder.vertex((this.left + this.width), 0.0, -100.0).uv((float)this.width / 32.0F, 0.0F).color(64, 64, 64, 255).next();
			bufferBuilder.vertex(this.left, 0.0, -100.0).uv(0.0F, 0.0F).color(64, 64, 64, 255).next();
			bufferBuilder.vertex(this.left, this.height, -100.0).uv(0.0F, (float)this.height / 32.0F).color(64, 64, 64, 255).next();
			bufferBuilder.vertex((this.left + this.width), this.height, -100.0)
				.uv((float)this.width / 32.0F, (float)this.height / 32.0F)
				.color(64, 64, 64, 255)
				.next();
			bufferBuilder.vertex((this.left + this.width), this.bottom, -100.0)
				.uv((float)this.width / 32.0F, (float)this.bottom / 32.0F)
				.color(64, 64, 64, 255)
				.next();
			bufferBuilder.vertex(this.left, this.bottom, -100.0).uv(0.0F, (float)this.bottom / 32.0F).color(64, 64, 64, 255).next();
			tessellator.draw();
			RenderSystem.depthFunc(515);
			RenderSystem.disableDepthTest();
			RenderSystem.enableBlend();
			RenderSystem.blendFuncSeparate(GlStateManager.class_4535.SRC_ALPHA, GlStateManager.class_4534.ONE_MINUS_SRC_ALPHA, GlStateManager.class_4535.ZERO, GlStateManager.class_4534.ONE);
			RenderSystem.disableTexture();
			RenderSystem.setShader(GameRenderer::getPositionColorShader);
			bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
			bufferBuilder.vertex(this.left, (this.top + 4), 0.0).color(0, 0, 0, 0).next();
			bufferBuilder.vertex(this.right, (this.top + 4), 0.0).color(0, 0, 0, 0).next();
			bufferBuilder.vertex(this.right, this.top, 0.0).color(0, 0, 0, 255).next();
			bufferBuilder.vertex(this.left, this.top, 0.0).color(0, 0, 0, 255).next();
			bufferBuilder.vertex(this.left, this.bottom, 0.0).color(0, 0, 0, 255).next();
			bufferBuilder.vertex(this.right, this.bottom, 0.0).color(0, 0, 0, 255).next();
			bufferBuilder.vertex(this.right, (this.bottom - 4), 0.0).color(0, 0, 0, 0).next();
			bufferBuilder.vertex(this.left, (this.bottom - 4), 0.0).color(0, 0, 0, 0).next();
			tessellator.draw();
		}

		int o = this.getMaxScroll();
		if (o > 0) {
			RenderSystem.disableTexture();
			RenderSystem.setShader(GameRenderer::getPositionColorShader);
			int m = (int)((float)((this.bottom - this.top) * (this.bottom - this.top)) / (float)this.getMaxPosition());
			m = MathHelper.clamp(m, 32, this.bottom - this.top - 8);
			int n = (int)this.getScrollAmount() * (this.bottom - this.top - m) / o + this.top;
			if (n < this.top) {
				n = this.top;
			}

			bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
			bufferBuilder.vertex(i, this.bottom, 0.0).color(0, 0, 0, 255).next();
			bufferBuilder.vertex(j, this.bottom, 0.0).color(0, 0, 0, 255).next();
			bufferBuilder.vertex(j, this.top, 0.0).color(0, 0, 0, 255).next();
			bufferBuilder.vertex(i, this.top, 0.0).color(0, 0, 0, 255).next();
			bufferBuilder.vertex(i, (n + m), 0.0).color(128, 128, 128, 255).next();
			bufferBuilder.vertex(j, (n + m), 0.0).color(128, 128, 128, 255).next();
			bufferBuilder.vertex(j, n, 0.0).color(128, 128, 128, 255).next();
			bufferBuilder.vertex(i, n, 0.0).color(128, 128, 128, 255).next();
			bufferBuilder.vertex(i, (n + m - 1), 0.0).color(192, 192, 192, 255).next();
			bufferBuilder.vertex((j - 1), (n + m - 1), 0.0).color(192, 192, 192, 255).next();
			bufferBuilder.vertex((j - 1), n, 0.0).color(192, 192, 192, 255).next();
			bufferBuilder.vertex(i, n, 0.0).color(192, 192, 192, 255).next();
			tessellator.draw();
		}

		this.renderDecorations(matrices, mouseX, mouseY);
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
    }

	@Override
	protected void renderList(MatrixStack matrices, int x, int y, int mouseX, int mouseY, float delta) {
		Util.applyScissor(new Rectangle(0, top, this.width, bottom-top));
		super.renderList(matrices, x, y, mouseX, mouseY, delta);
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

    public boolean keyPressed(int keyCode, int scanCode, int modifiers){
        entries.forEach(pair -> {
            if(pair.left instanceof StringOptionWidget && ((StringOptionWidget) pair.left).textField.isFocused()){
                pair.left.keyPressed(keyCode, scanCode, modifiers);
            } else if(pair.right instanceof StringOptionWidget && ((StringOptionWidget) pair.right).textField.isFocused()){
                pair.right.keyPressed(keyCode, scanCode, modifiers);
            }

            if(pair.left instanceof ColorOptionWidget) {
                pair.left.keyPressed(keyCode, scanCode, modifiers);
            }
        });
	    return false;
    }

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Environment(EnvType.CLIENT)
    public static class Pair extends ButtonListWidget.ButtonEntry {
        protected final MinecraftClient client = MinecraftClient.getInstance();
        protected final ClickableWidget left;
        private final ClickableWidget right;

        public Pair(ClickableWidget left, ClickableWidget right) {
			super(new HashMap<>());
	        this.left = left;
            this.right = right;
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

	    @Override
	    public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (this.left.isMouseOver(mouseX, mouseY)) {
                onClick(this.left, mouseX, mouseY);

                return true;
            } else if (this.right != null && this.right.isMouseOver(mouseX, mouseY)) {
                onClick(this.right, mouseX, mouseY);

                return true;
            }
            return false;
        }

        protected void onClick(ClickableWidget button, double mouseX, double mouseY){
            if (button instanceof OptionSliderWidget){
                button.isMouseOver(mouseX, mouseY);
                ConfigManager.save();
            } else if (button instanceof CategoryWidget) {
                button.mouseClicked(mouseX, mouseY, 0);

            } else if (button instanceof EnumOptionWidget) {
                button.playDownSound(client.getSoundManager());
                button.mouseClicked(mouseX, mouseY, 0);
                ConfigManager.save();

            } else if (button instanceof StringOptionWidget) {
                ((StringOptionWidget) button).textField.mouseClicked(mouseX, mouseY, 0);
                ConfigManager.save();

            } else if (button instanceof BooleanWidget) {
                button.playDownSound(client.getSoundManager());
                ((BooleanWidget) button).option.toggle();
                //((BooleanWidget) button).updateMessage();
                ConfigManager.save();

            } else if (button instanceof ColorOptionWidget) {
                button.mouseClicked(mouseX, mouseY, 0);
                ConfigManager.save();

            }
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
    }

    public static class OptionEntry extends Pair {

        private final Option option;

        public OptionEntry(ClickableWidget left, Option option, int width) {
            super(left, null);
            this.option = option;
            if(left instanceof BooleanWidget) left.x = width / 2 - 155 + 160 + 57;
            else left.x = width / 2 - 155 + 160;
        }

	    @Override
	    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {

            drawCenteredText(matrices, client.textRenderer, option.getTranslatedName(), x, y, -1);
            left.y = y;
            left.render(matrices, mouseX, mouseY, tickDelta);

        }
    }

    public static class Spacer extends Pair {

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
