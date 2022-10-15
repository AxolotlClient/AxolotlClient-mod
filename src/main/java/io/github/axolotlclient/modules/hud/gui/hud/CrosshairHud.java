package io.github.axolotlclient.modules.hud.gui.hud;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormats;
import io.github.axolotlclient.AxolotlclientConfig.Color;
import io.github.axolotlclient.AxolotlclientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlclientConfig.options.ColorOption;
import io.github.axolotlclient.AxolotlclientConfig.options.EnumOption;
import io.github.axolotlclient.AxolotlclientConfig.options.OptionBase;
import io.github.axolotlclient.modules.hud.gui.AbstractHudEntry;
import io.github.axolotlclient.modules.hud.util.DrawPosition;
import io.github.axolotlclient.modules.hud.util.Rectangle;
import net.minecraft.block.AbstractChestBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.option.AttackIndicator;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 * @license GPL-3.0
 */

public class CrosshairHud extends AbstractHudEntry {
    public static final Identifier ID = new Identifier("kronhud", "crosshairhud");

    private final EnumOption type = new EnumOption("crosshair_type", CrosshairOption.values(), CrosshairOption.TEXTURE.toString());
    private final BooleanOption showInF5 = new BooleanOption("showInF5", false);
    public final BooleanOption showInF3 = new BooleanOption("showInF3", false);
    private final ColorOption defaultColor = new ColorOption("defaultcolor",  "#FFFFFFFF");
    private final ColorOption entityColor = new ColorOption("entitycolor", Color.SELECTOR_RED);
    private final ColorOption containerColor = new ColorOption("blockcolor", Color.SELECTOR_BLUE);
	private final ColorOption attackIndicatorBackgroundColor = new ColorOption("attackindicatorbg",
		"#FF141414");
	private final ColorOption attackIndicatorForegroundColor = new ColorOption("attackindicatorfg",
		"#FFFFFFFF");

    public CrosshairHud() {
        super(17, 17);
    }

    @Override
    protected double getDefaultX() {
        return 0.5;
    }

    @Override
    protected float getDefaultY() {
        return 0.5F;
    }

    @Override
	public void render(MatrixStack matrices) {
		if(!client.options.getPerspective().isFirstPerson() && !showInF5.get())return;

		scale(matrices);
        DrawPosition pos = new DrawPosition(MinecraftClient.getInstance().getWindow().getScaledWidth()/2 - width/2, MinecraftClient.getInstance().getWindow().getScaledHeight()/2 - height/2);
		Color color = getColor();
		if (Objects.equals(type.get(), CrosshairOption.DOT.toString())) {
            if(color==defaultColor.get()) {
                RenderSystem.enableBlend();
                RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            }
			fillRect(matrices, new Rectangle(pos.x + (width / 2) - 2, pos.y + (height / 2) - 2, 3, 3), color);
            if(color==defaultColor.get()) {
                RenderSystem.defaultBlendFunc();
                RenderSystem.disableBlend();
            }
		} else if (Objects.equals(type.get(), CrosshairOption.CROSS.toString())) {
            if(color==defaultColor.get()) {
                RenderSystem.enableBlend();
                RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            }
			fillRect(matrices, new Rectangle(pos.x + (width / 2) - 6, pos.y + (height / 2) - 1, 6, 1), color);
			fillRect(matrices, new Rectangle(pos.x + (width / 2), pos.y + (height / 2) - 1, 5, 1), color);
			fillRect(matrices, new Rectangle(pos.x + (width / 2) - 1, pos.y + (height / 2) - 6, 1, 6), color);
			fillRect(matrices, new Rectangle(pos.x + (width / 2) - 1, pos.y + (height / 2), 1, 5), color);
            if(color==defaultColor.get()) {
                RenderSystem.defaultBlendFunc();
                RenderSystem.disableBlend();
            }
		} else if (Objects.equals(type.get(), CrosshairOption.DIRECTION.toString())) {
			Camera camera = this.client.gameRenderer.getCamera();
			MatrixStack matrixStack = RenderSystem.getModelViewStack();
			matrixStack.push();
			matrixStack.translate(pos.x + (width/2F), pos.y + (height / 2F), 0);
			matrixStack.multiply(Vec3f.NEGATIVE_X.getDegreesQuaternion(camera.getPitch()));
			matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(camera.getYaw()));
			matrixStack.scale(-getScale(), -getScale(), getScale());
			RenderSystem.applyModelViewMatrix();
			RenderSystem.renderCrosshair(10);
			matrixStack.pop();
			RenderSystem.applyModelViewMatrix();
		} else if (Objects.equals(type.get(), CrosshairOption.TEXTURE.toString())) {
            if(color==defaultColor.get()) {
                RenderSystem.enableBlend();
                RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            }
			RenderSystem.setShaderTexture(0, DrawableHelper.GUI_ICONS_TEXTURE);

			// Draw crosshair
			RenderSystem.setShaderColor((float) color.getRed() / 255, (float) color.getGreen() / 255, (float) color.getBlue() / 255, (float) color.getAlpha() / 255);
			client.inGameHud.drawTexture(matrices, (int) (((client.getWindow().getScaledWidth() / getScale()) - 15) / 2), (int) (((client.getWindow().getScaledHeight() / getScale()) - 15) / 2), 0, 0, 15, 15);
			RenderSystem.setShaderColor(1, 1, 1, 1);

            if(color==defaultColor.get()) {
                RenderSystem.defaultBlendFunc();
                RenderSystem.disableBlend();
            }
			// Draw attack indicator
			if (this.client.options.getAttackIndicator().get() == AttackIndicator.CROSSHAIR) {

				float progress = this.client.player.getAttackCooldownProgress(0.0F);

				// Whether a cross should be displayed under the indicator
				boolean targetingEntity = false;
				if (this.client.targetedEntity != null && this.client.targetedEntity instanceof LivingEntity
					&& progress >= 1.0F) {
					targetingEntity = this.client.player.getAttackCooldownProgressPerTick() > 5.0F;
					targetingEntity &= this.client.targetedEntity.isAlive();
				}

				int x = (int) ((client.getWindow().getScaledWidth() / getScale()) / 2 - 8);
				int y = (int) ((client.getWindow().getScaledHeight() / getScale()) / 2 - 7 + 16);

				if (targetingEntity) {
					client.inGameHud.drawTexture(matrices, x, y, 68, 94, 16, 16);
				} else if (progress < 1.0F) {
					int k = (int)(progress * 17.0F);
					client.inGameHud.drawTexture(matrices, x, y, 36, 94, 16, 4);
					client.inGameHud.drawTexture(matrices, x, y, 52, 94, k, 4);
				}
			}
		}

		if (this.client.options.getAttackIndicator().get() == AttackIndicator.CROSSHAIR) {
			float progress = this.client.player.getAttackCooldownProgress(0.0F);
			if (progress != 1.0F) {
				fill(matrices.peek().getModel(), pos.x + (width / 2F) - 6, pos.y + (height / 2F) + 9, 11, 1,
					attackIndicatorBackgroundColor.get().getAsInt());
				fill(matrices.peek().getModel(), pos.x + (width / 2F) - 6, pos.y + (height / 2F) + 9,
					progress * 11, 1, attackIndicatorForegroundColor.get().getAsInt());
			}
		}
		matrices.pop();
	}

	private static void fill(Matrix4f matrix, float x, float y, float width, float height, int color) {
		float x2 = x + width;
		float y2 = y + height;
		float swap;
		if (x < x2) {
			swap = x;
			x = x2;
			x2 = swap;
		}

		if (y < y2) {
			swap = y;
			y = y2;
			y2 = swap;
		}

		float alpha = (float) (color >> 24 & 255) / 255.0F;
		float r = (float) (color >> 16 & 255) / 255.0F;
		float g = (float) (color >> 8 & 255) / 255.0F;
		float b = (float) (color & 255) / 255.0F;
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBufferBuilder();
		RenderSystem.enableBlend();
		RenderSystem.disableTexture();
		RenderSystem.defaultBlendFunc();
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex(matrix, x, y2, 0.0F).color(r, g, b, alpha).next();
		bufferBuilder.vertex(matrix, x2, y2, 0.0F).color(r, g, b, alpha).next();
		bufferBuilder.vertex(matrix, x2, y, 0.0F).color(r, g, b, alpha).next();
		bufferBuilder.vertex(matrix, x, y, 0.0F).color(r, g, b, alpha).next();

		Tessellator.getInstance().draw();
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}

	public Color getColor() {
		HitResult hit = client.crosshairTarget;
		if (hit.getType() == null) {
			return defaultColor.get();
		} else if (hit.getType() == HitResult.Type.ENTITY) {
			return entityColor.get();
		} else if (hit.getType() == HitResult.Type.BLOCK) {
			BlockPos blockPos = ((BlockHitResult) hit).getBlockPos();
			World world = this.client.world;
			if (world.getBlockState(blockPos).createScreenHandlerFactory(world, blockPos) != null || world.getBlockState(blockPos).getBlock() instanceof AbstractChestBlock<?>) {
				return containerColor.get();
			}
		}
		return defaultColor.get();
	}

	@Override
	public void renderPlaceholder(MatrixStack matrices) {
		// Shouldn't need this...
	}

    @Override
    public boolean movable() {
        return false;
    }

	@Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public void addConfigOptions(List<OptionBase<?>> options) {
        super.addConfigOptions(options);
        options.add(type);
        options.add(showInF5);
        options.add(showInF3);
        options.add(defaultColor);
        options.add(entityColor);
        options.add(containerColor);
        options.add(attackIndicatorBackgroundColor);
        options.add(attackIndicatorForegroundColor);
    }

    public enum CrosshairOption{
        CROSS,
        DOT,
        TEXTURE,
        DIRECTION
    }

}
