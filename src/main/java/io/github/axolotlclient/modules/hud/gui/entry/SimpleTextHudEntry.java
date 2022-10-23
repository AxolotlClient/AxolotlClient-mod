package io.github.axolotlclient.modules.hud.gui.entry;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.axolotlclient.AxolotlclientConfig.Color;
import io.github.axolotlclient.AxolotlclientConfig.options.EnumOption;
import io.github.axolotlclient.AxolotlclientConfig.options.IntegerOption;
import io.github.axolotlclient.AxolotlclientConfig.options.OptionBase;
import io.github.axolotlclient.modules.hud.gui.component.DynamicallyPositionable;
import io.github.axolotlclient.modules.hud.gui.layout.AnchorPoint;
import io.github.axolotlclient.modules.hud.gui.layout.Justification;
import io.github.axolotlclient.modules.hud.util.DrawPosition;
import net.minecraft.client.util.math.MatrixStack;

import java.util.List;

public abstract class SimpleTextHudEntry extends TextHudEntry implements DynamicallyPositionable {

    protected final EnumOption justification = new EnumOption("justification", Justification.values(), Justification.CENTER.toString());
    protected final EnumOption anchor = new EnumOption("anchor", AnchorPoint.values(), AnchorPoint.TOP_MIDDLE.toString());

    private final IntegerOption minWidth;

    public SimpleTextHudEntry() {
        this(53, 13, true);
    }

    protected SimpleTextHudEntry(int width, int height) {
        this(width, height, true);
    }

    protected SimpleTextHudEntry(int width, int height, boolean backgroundAllowed) {
        super(width, height, backgroundAllowed);
        minWidth = new IntegerOption("minwidth", width, 1, 300);
    }

    @Override
    public void renderComponent(MatrixStack matrices, float delta) {
        //RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.disableTexture();
        DrawPosition pos = getPos();
        String value = getValue();

        int valueWidth = client.textRenderer.getWidth(value);
        int elementWidth = valueWidth + 4;

        int min = minWidth.get();
        if (elementWidth < min) {
            if (width != min) {
                setWidth(min);
                onBoundsUpdate();
            }
        } else if (elementWidth != width) {
            setWidth(elementWidth);
            onBoundsUpdate();
        }
        drawString(
                matrices, value,
                pos.x() + Justification.valueOf(justification.get()).getXOffset(valueWidth, getWidth() - 4) + 2,
                pos.y() + (Math.round((float) getHeight() / 2)) - 4,
                getTextColor().getAsInt(), shadow.get()
        );
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    public Color getTextColor() {
        return textColor.get();
    }

    @Override
    public void renderPlaceholderComponent(MatrixStack matrices, float delta) {
        DrawPosition pos = getPos();
        String value = getPlaceholder();
        drawString(
                matrices, value,
                pos.x() + Justification.valueOf(justification.get()).getXOffset(value, getWidth() - 4) + 2,
                pos.y() + (Math.round((float) getHeight() / 2)) - 4,
                textColor.get().getAsInt(), shadow.get()
        );
    }

    @Override
    public List<OptionBase<?>> getConfigurationOptions() {
        List<OptionBase<?>> options = super.getConfigurationOptions();
        options.add(justification);
        options.add(anchor);
        options.add(minWidth);
        return options;
    }

    @Override
    public boolean movable() {
        return true;
    }

    public abstract String getValue();

    public abstract String getPlaceholder();

    @Override
    public AnchorPoint getAnchor() {
        return AnchorPoint.valueOf(anchor.get());
    }
}
