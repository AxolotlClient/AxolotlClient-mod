package io.github.axolotlclient.config.screen.widgets;

import com.mojang.blaze3d.glfw.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.axolotlclient.config.Color;
import io.github.axolotlclient.config.options.ColorOption;
import io.github.axolotlclient.modules.hud.util.DrawUtil;
import io.github.axolotlclient.modules.hud.util.Rectangle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class ColorSelectionWidget extends ButtonWidget {
    private final ColorOption option;

    protected Rectangle pickerImage;
    //private final Rectangle rect;

    public ColorSelectionWidget(ColorOption option) {
        super(0, 0, 100, 50, Text.empty(), buttonWidget -> {});
        this.option=option;
        Window window= MinecraftClient.getInstance().getWindow();
        width=window.getScaledWidth()-200;
        height=window.getScaledHeight()-100;

        pickerImage = new Rectangle(120, 70, width/2, height/3);

        //rect = new Rectangle(100, 50, width-200, height-100);
    }

	@Override
	public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {

        DrawUtil.fillRect(matrices, new Rectangle(100, 50, width, height), Color.DARK_GRAY.withAlpha(127));
        DrawUtil.outlineRect(matrices, new Rectangle(100, 50, width, height), Color.BLACK);

        DrawUtil.outlineRect(matrices, pickerImage, Color.DARK_GRAY.withAlpha(127));

		RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.setShaderTexture(0, new Identifier("axolotlclient", "textures/gui/colorwheel.png"));
        DrawableHelper.drawTexture(matrices, pickerImage.x, pickerImage.y, 0, 0, pickerImage.width, pickerImage.height, pickerImage.width, pickerImage.height);

        //super.render(client, mouseX, mouseY);
    }

    public void onClick(int mouseX, int mouseY){
        if(pickerImage.isMouseOver(mouseX, mouseY)) {
            ByteBuffer buf = ByteBuffer.allocateDirect(4);
            IntBuffer color = buf.asIntBuffer();

            //MinecraftClient.getInstance().getFramebuffer().bind(true);
            GL11.glReadPixels(mouseX, mouseY, 1, 1, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buf);

            System.out.println(buf.get());

            option.set(new Color(buf.get(0) & 0xFF, buf.get(1) & 0xFF, buf.get(2) & 0xFF, buf.get(3) & 0xFF));
        }
    }


}
