package io.github.moehreag.axolotlclient.config.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormats;
import io.github.moehreag.axolotlclient.AxolotlClient;
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
                CategoryWidget buttonWidget = this.createCategoryWidget(width / 2 - 155, subCat);

                OptionCategory subCat2 = i < category.getSubCategories().size() - 1 ? category.getSubCategories().get(i + 1) : null;
                CategoryWidget buttonWidget2 = this.createCategoryWidget(width / 2 - 155 + 160, subCat2);

	            this.addEntry(new CategoryPair(subCat, buttonWidget, subCat2, buttonWidget2));
	            this.entries.add(new CategoryPair(subCat, buttonWidget, subCat2, buttonWidget2));
            }
            this.addEntry(new Spacer());
			this.entries.add(new Spacer());
        }

        for (int i = 0; i < (category.getOptions().size()); i ++) {

            Option option = category.getOptions().get(i);
            if(option.getName().equals("x")||option.getName().equals("y")) continue;
            ClickableWidget buttonWidget = this.createWidget(width / 2 - 155+160, option);

            addEntry(new OptionEntry(buttonWidget, option, width));
			this.entries.add(new OptionEntry(buttonWidget, option, width));
        }
    }

	@Override
	protected int addEntry(ButtonEntry entry) {
		if(entry instanceof Pair) this.entries.add((Pair)entry);
		return super.addEntry(entry);
	}

	private CategoryWidget createCategoryWidget(int x, OptionCategory cat){
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

		int k = this.getRowLeft();
		int l = this.top + 4 - (int)this.getScrollAmount();

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

		RenderSystem.enableTexture();
		this.renderList(matrices, k, l, mouseX, mouseY, delta);

		RenderSystem.disableBlend();
    }

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		setFocused(null);
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public int getRowLeft() {
		return this.width / 2 -155;
	}

	@Override
	protected void renderList(MatrixStack matrices, int x, int y, int mouseX, int mouseY, float delta) {
		Util.applyScissor(new Rectangle(0, top, this.width, bottom-top));
		super.renderList(matrices, x, y, mouseX, mouseY, delta);
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
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
	    return false;
    }

	public boolean charTyped(char c, int modifiers){
		for(Pair pair:entries){
			if(pair.charTyped(c, modifiers)) return true;
		}
		return false;
	}

	@Environment(EnvType.CLIENT)
    public class Pair extends ButtonListWidget.ButtonEntry {
        protected final MinecraftClient client = MinecraftClient.getInstance();
        protected final ClickableWidget left;
        protected final ClickableWidget right;

		protected final boolean tickable;

        public Pair(ClickableWidget left, ClickableWidget right) {
			super(new HashMap<>());
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

		protected void renderTooltip(MatrixStack matrices, Option option, int x, int y){
			if(MinecraftClient.getInstance().currentScreen instanceof OptionsScreenBuilder &&
				AxolotlClient.CONFIG.showOptionTooltips.get() && option.getTooltip()!=null){
				GL11.glDisable(GL11.GL_SCISSOR_TEST);
				((OptionsScreenBuilder) MinecraftClient.getInstance().currentScreen).renderTooltip(matrices, option, x, y);
				Util.applyScissor(new Rectangle(0, top, width, bottom-top));
			}
		}

		protected void renderTooltip(MatrixStack matrices, OptionCategory category, int x, int y){
			if(MinecraftClient.getInstance().currentScreen instanceof OptionsScreenBuilder &&
				AxolotlClient.CONFIG.showCategoryTooltips.get() && category.getTooltip()!=null){
				GL11.glDisable(GL11.GL_SCISSOR_TEST);
				((OptionsScreenBuilder) MinecraftClient.getInstance().currentScreen).renderTooltip(matrices, category, x, y);
				Util.applyScissor(new Rectangle(0, top, width, bottom-top));
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
    }

	public class CategoryPair extends Pair {

		protected OptionCategory left;
		protected OptionCategory right;

		public CategoryPair(OptionCategory catLeft, CategoryWidget btnLeft, OptionCategory catRight, CategoryWidget btnRight) {
			super(btnLeft, btnRight);
			left = catLeft;
			right = catRight;
		}

		@Override
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			super.render(matrices, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickDelta);

			if(super.left!=null && super.left.isMouseOver(mouseX, mouseY)){
				renderTooltip(matrices, left, mouseX, mouseY);
			}
			if(super.right !=null && super.right.isMouseOver(mouseX, mouseY)){
				renderTooltip(matrices, right, mouseX, mouseY);
			}
		}
	}

    public class OptionEntry extends Pair {

        private final Option option;
		protected int width;

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

		    if(mouseX>=x && mouseX<=left.x + left.getWidth() && mouseY>= y && mouseY<= y + 20){
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
