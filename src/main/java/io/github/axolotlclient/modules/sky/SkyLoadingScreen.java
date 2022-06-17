package io.github.axolotlclient.modules.sky;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.config.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.client.util.ColorUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.pack.ResourcePack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

import java.util.Objects;

public class SkyLoadingScreen extends Overlay {

	static final Identifier LOGO = new Identifier("axolotlclient","icon.png");
	/*private static final int MOJANG_RED = ColorUtil.ARGB32.getArgb(255, 239, 50, 61);
	private static final int MONOCHROME_BLACK = ColorUtil.ARGB32.getArgb(255, 0, 0, 0);
	private static final IntSupplier BRAND_ARGB = () -> MinecraftClient.getInstance().options.getMonochromeLogo().get() ? MONOCHROME_BLACK : MOJANG_RED;
	private static final int LOGO_SCALE = 240;
	private static final float LOGO_QUARTER_FLOAT = 60.0F;
	private static final int LOGO_QUARTER = 60;
	private static final int LOGO_HALF = 120;
	private static final float LOGO_OVERLAP = 0.0625F;
	private static final float PROGRESS_LERP_DELTA = 0.95F;
	public static final long RELOAD_COMPLETE_FADE_DURATION = 1000L;
	public static final long RELOAD_START_FADE_DURATION = 500L;*/
	private MutableText currentPack = Text.empty();
	public static boolean currentlyShown = false;
	private String description="";
	private boolean loadingFinished = false;
	private final MinecraftClient client;
	private float progress;

	public SkyLoadingScreen() {
		this.client = MinecraftClient.getInstance();
		MinecraftClient.getInstance().getTextureManager().registerTexture(LOGO, new SkyLoadingScreen.LogoTexture());
	}

	private static int withAlpha(int color, int alpha) {
		return color & 16777215 | alpha << 24;
	}

	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {

		if(!currentlyShown){
			currentlyShown=true;
		}

		int i = this.client.getWindow().getScaledWidth();
		int j = this.client.getWindow().getScaledHeight();
		long l = Util.getMeasuringTimeMs();

		float h;
			/*if (this.client.currentScreen != null) {
				this.client.currentScreen.render(matrices, 0, 0, delta);
			}

			int k = MathHelper.ceil((1.0F - MathHelper.clamp(f - 1.0F, 0.0F, 1.0F)) * 255.0F);
			fill(matrices, 0, 0, i, j, withAlpha(BRAND_ARGB.getAsInt(), k));
			h = 1.0F - MathHelper.clamp(f - 1.0F, 0.0F, 1.0F);
		} else if (this.reloading) {
			if (this.client.currentScreen != null && g < 1.0F) {
				this.client.currentScreen.render(matrices, mouseX, mouseY, delta);
			}

			int k = MathHelper.ceil(MathHelper.clamp((double)g, 0.15, 1.0) * 255.0);
			fill(matrices, 0, 0, i, j, withAlpha(BRAND_ARGB.getAsInt(), k));
			h = MathHelper.clamp(g, 0.0F, 1.0F);
		} else {
			int k = BRAND_ARGB.getAsInt();
			float m = (float)(k >> 16 & 0xFF) / 255.0F;
			float n = (float)(k >> 8 & 0xFF) / 255.0F;
			float o = (float)(k & 0xFF) / 255.0F;
			GlStateManager._clearColor(m, n, o, 1.0F);
			GlStateManager._clear(16384, MinecraftClient.IS_SYSTEM_MAC);
			h = 1.0F;
		}*/


		RenderSystem.setShaderTexture(0, LOGO);
		RenderSystem.enableBlend();
		RenderSystem.blendEquation(32774);
		RenderSystem.blendFunc(770, 1);
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1F);
		drawTexture(matrices, (client.getWindow().getWidth()/2)-50,
			(client.getWindow().getHeight()/2)-50,
			0, 0,
			100, 100,
			100, 100);
		RenderSystem.defaultBlendFunc();
		RenderSystem.disableBlend();
		int s = (int)((double)this.client.getWindow().getScaledHeight() * 0.8325);
		//float t = this.reload.getProgress();
		//this.progress = MathHelper.clamp(this.progress * 0.95F + t * 0.050000012F, 0.0F, 1.0F);
		//if (f < 1.0F) {
		double d = Math.min((double)this.client.getWindow().getScaledWidth() * 0.75, this.client.getWindow().getScaledHeight()) * 0.25;
		double e = d * 4.0;
		int r = (int)(e * 0.5);
		this.renderProgressBar(matrices, i / 2 - r, s - 5, i / 2 + r, s + 5, 1.0F - MathHelper.clamp(progress, 0.0F, 1.0F));
		//}

		if(!loadingFinished) {
			this.client.textRenderer.draw(matrices, Text.translatable("sky_loading_text")
					.append(" ").append(currentPack.setStyle(Style.EMPTY.withItalic(true)))
					.append(!Objects.equals(description, "") ?" "+description:"")
					.formatted(Formatting.ITALIC).append("..."),
				20, client.getWindow().getHeight()-20,
				Color.getChroma().getAsInt());
		} else if(!AxolotlClient.initalized) {
			this.client.textRenderer.draw(matrices, Text.translatable("resource_loading_finished").formatted(Formatting.BOLD),
				20, client.getWindow().getHeight()-20, Color.getChroma().getAsInt());
		}

		/*if (f >= 2.0F) {
			this.client.setOverlay(null);
		}*/

		/*if (this.reloadCompleteTime == -1L && this.reload.isComplete() && (!this.reloading || g >= 2.0F)) {
			try {
				this.reload.throwException();
				this.exceptionHandler.accept(Optional.empty());
			} catch (Throwable var23) {
				this.exceptionHandler.accept(Optional.of(var23));
			}

			this.reloadCompleteTime = Util.getMeasuringTimeMs();
			if (this.client.currentScreen != null) {
				this.client.currentScreen.init(this.client, this.client.getWindow().getScaledWidth(), this.client.getWindow().getScaledHeight());
			}
		}*/

	}

	private void renderProgressBar(MatrixStack matrices, int minX, int minY, int maxX, int maxY, float opacity) {
		int i = MathHelper.ceil((float)(maxX - minX - 2) * this.progress);
		int j = Math.round(opacity * 255.0F);
		int k = ColorUtil.ARGB32.getArgb(j, 255, 255, 255);
		fill(matrices, minX + 2, minY + 2, minX + i, maxY - 2, k);
		fill(matrices, minX + 1, minY, maxX - 1, minY + 1, k);
		fill(matrices, minX + 1, maxY, maxX - 1, maxY - 1, k);
		fill(matrices, minX, minY, minX + 1, maxY, k);
		fill(matrices, maxX, minY, maxX - 1, maxY, k);
	}

	public boolean pausesGame() {
		return true;
	}

	public void finish(MatrixStack matrices){
		loadingFinished=true;
		Color.tickChroma();
		render(matrices, 0, 0, 0);
		AxolotlClient.initalized=true;
	}

	public void update(ResourcePack pack, MatrixStack matrices){
		currentPack = Text.literal(pack.getName());
		Color.tickChroma();
		render(matrices, 0, 0, 0);
	}

	public void setDesc(String desc, MatrixStack matrices){
		this.description="("+desc+")";
		//Color.tickChroma();
		render(matrices, 0, 0, 0);
	}

	@Environment(EnvType.CLIENT)
	static class LogoTexture extends ResourceTexture {
		public LogoTexture() {
			super(SkyLoadingScreen.LOGO);
		}

		/*protected TextureData loadTextureData(ResourceManager resourceManager) {
			MinecraftClient minecraftClient = MinecraftClient.getInstance();
			DefaultResourcePack defaultResourcePack = minecraftClient.getResourcePackProvider().getPack();

			try {
				InputStream inputStream = defaultResourcePack.open(ResourceType.CLIENT_RESOURCES, SkyLoadingScreen.LOGO);

				TextureData var5;
				try {
					var5 = new TextureData(new TextureResourceMetadata(true, true), NativeImage.read(inputStream));
				} catch (Throwable var8) {
					if (inputStream != null) {
						try {
							inputStream.close();
						} catch (Throwable var7) {
							var8.addSuppressed(var7);
						}
					}

					throw var8;
				}

				inputStream.close();

				return var5;
			} catch (IOException var9) {
				return new TextureData(var9);
			}
		}*/
	}
    /*private String currentPack = "";
    public static boolean currentlyShown = false;
    private String description="";
    private boolean loadingFinished = false;

    MinecraftClient client = MinecraftClient.getInstance();

    public void render() {
        if(!currentlyShown){
            currentlyShown=true;
        }
        Window window = new Window(MinecraftClient.getInstance());
        int i = window.getScaleFactor();
        Framebuffer framebuffer = new Framebuffer(window.getWidth() * i, window.getHeight() * i, true);
        framebuffer.bind(false);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0D, window.getWidth(), window.getHeight(), 0.0D, 1000.0D, 3000.0D);
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        GlStateManager.translatef(0.0F, 0.0F, -2000.0F);
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        GlStateManager.disableDepthTest();

        DrawableHelper.fill(0, 0, MinecraftClient.getInstance().width, MinecraftClient.getInstance().height, AxolotlClient.CONFIG.loadingScreenColor.get().getAsInt());

        GlStateManager.enableTexture();

        MinecraftClient.getInstance().getTextureManager().bindTexture(AxolotlClient.badgeIcon);

        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1F);
        DrawableHelper.drawTexture((window.getWidth()/2)-50,
                (window.getHeight()/2)-50,
                0, 0,
                100, 100,
                100, 100);

        if(!loadingFinished) {
            this.client.textRenderer.draw(I18n.translate("sky_loading_text") +
                            " "+Formatting.ITALIC +
                            currentPack+Formatting.RESET+Formatting.ITALIC +
                            (!Objects.equals(description, "")? " "+description:"") + Formatting.RESET+"...",
                    20, window.getHeight() - 20,
                    Color.getChroma().getAsInt());
        } else if(!AxolotlClient.initalized) {
            this.client.textRenderer.draw(Formatting.BOLD+ I18n.translate("resource_loading_finished"),
                    20, window.getHeight()-20, Color.getChroma().getAsInt());
        }
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        framebuffer.endWrite();
        framebuffer.draw(window.getWidth() * i, window.getHeight() * i);
        GlStateManager.enableAlphaTest();
        GlStateManager.alphaFunc(516, 0.1F);
        MinecraftClient.getInstance().updateDisplay();

    }

    public void update(ResourcePack pack){
        currentPack = pack.getName();
        Color.tickChroma();
        render();
    }

    public void setDesc(String desc){
        this.description="("+desc+")";
        //Color.tickChroma();
        render();
    }

    public void finish(){
        loadingFinished=true;
        Color.tickChroma();
        render();
        AxolotlClient.initalized=true;
    }*/

}
