package io.github.axolotlclient.config.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.config.Color;
import io.github.axolotlclient.config.ConfigManager;
import io.github.axolotlclient.modules.hud.util.DrawUtil;
import io.github.axolotlclient.modules.hud.util.Rectangle;
import io.github.axolotlclient.util.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CreditsScreen extends Screen {

    private final Screen parent;

    private final List<Credit> credits = new ArrayList<>();

    private Overlay creditOverlay;

    private EntryListWidget<Credit> creditsList;

    private final SoundInstance bgm = PositionedSoundInstance.master(SoundEvents.MUSIC_DISC_CHIRP, 1, 1);

    public CreditsScreen(Screen parent){
        super(new LiteralText(""));
        this.parent=parent;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float tickDelta) {

        if(AxolotlClient.CONFIG.creditsBGM.get() && !MinecraftClient.getInstance().getSoundManager().isPlaying(bgm)){
            MinecraftClient.getInstance().getSoundManager().play(bgm);
        }

        if(MinecraftClient.getInstance().world!=null)fillGradient(matrices, 0,0, width, height, new Color(0xB0100E0E).getAsInt(), new Color(0x46212020).getAsInt());
        else renderBackgroundTexture(0);
        if(AxolotlClient.someNiceBackground.get()) { // Credit to pridelib for the colors
            DrawUtil.fill(matrices, 0, 0, width, height/6, 0xFFff0018);
            DrawUtil.fill(matrices, 0, height/6, width, height*2/6, 0xFFffa52c);
            DrawUtil.fill(matrices, 0, height*2/6, width, height/2, 0xFFffff41);
            DrawUtil.fill(matrices, 0, height*2/3, width, height*5/6, 0xFF0000f9);
            DrawUtil.fill(matrices, 0, height/2, width, height*2/3, 0xFF008018);
            DrawUtil.fill(matrices, 0, height*5/6, width, height, 0xFF86007d);
        }

        super.render(matrices, mouseX, mouseY, tickDelta);

        DrawUtil.drawCenteredString(matrices, this.textRenderer, I18n.translate("credits"), width/2, 20, -1);

        if(creditOverlay!=null){
            creditOverlay.render(matrices);
        } else {
            creditsList.render(matrices, mouseX, mouseY, tickDelta);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);

        if(creditOverlay!=null){
            if(!creditOverlay.isMouseOver(mouseX, mouseY)) {
                creditOverlay=null;
                this.creditsList.mouseClicked(mouseX, mouseY, button);

            } else {
                creditOverlay.mouseClicked(mouseX, mouseY);
            }
        } else {
            this.creditsList.mouseClicked(mouseX, mouseY, button);

        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return creditsList.mouseReleased(mouseX, mouseY, button) || super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        if(creditOverlay!=null)creditOverlay.init();
        super.resize(client, width, height);
    }

    @Override
    public void init() {
        this.addButton(new ButtonWidget(
            width/2 -75, height - 50 + 22, 150, 20,
            new TranslatableText("back"), buttonWidget -> {
            if(creditOverlay==null) {
                MinecraftClient.getInstance().openScreen(parent);
                stopBGM();
            } else {
                creditOverlay=null;
            }
        }));

        this.addButton(new ButtonWidget(6, this.height-26, 100, 20,
            new TranslatableText("creditsBGM").append(": ").append(new TranslatableText(AxolotlClient.CONFIG.creditsBGM.get()?"options.on":"options.off")),
            buttonWidget -> {
                AxolotlClient.CONFIG.creditsBGM.toggle();
                ConfigManager.save();
                stopBGM();
                buttonWidget.setMessage(new TranslatableText("creditsBGM").append(": ")
                    .append(new TranslatableText(AxolotlClient.CONFIG.creditsBGM.get()?"options.on":"options.off")));
            }
        ));

        credits.clear();
        initCredits();

        creditsList = new CreditsList(client, width, height, 50, height - 50, 25);
    }

    private void initCredits(){

        credits.add(new SpacerTitle("- - - - - - "+I18n.translate("contributors")+" - - - - - -"));

        credits.add(new Credit("moehreag", "Author, Programming", "https://github.com/moehreag"));
        credits.add(new Credit("YakisikliBaran", "Turkish Translation"));
        credits.add(new Credit("TheKodeToad", "Contributor", "Motion Blur", "Freelook", "Zoom"));
        credits.add(new Credit("DragonEggBedrockBreaking", "Bugfixing", "Inspiration of new Features"));

        credits.add(new SpacerTitle("- - - - - - "+I18n.translate("other_people")+" - - - - - -"));

        credits.add(new Credit("DarkKronicle", "Author of KronHUD, the best HUD mod!"));
        credits.add(new Credit("AMereBagatelle", "Author of the excellent FabricSkyBoxes Mod"));

    }

    private void stopBGM(){
        MinecraftClient.getInstance().getSoundManager().stop(bgm);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)|| creditsList.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return super.mouseScrolled(mouseX, mouseY, amount) || creditsList.mouseScrolled(mouseX, mouseY, amount);
    }

    private class CreditsList extends EntryListWidget<Credit> {

        public CreditsList(MinecraftClient minecraftClient, int width, int height, int top, int bottom, int entryHeight) {
            super(minecraftClient, width, height, top, bottom, entryHeight);
            
            this.setRenderHeader(false, 0);

            for(Credit c:credits){
                addEntry(c);
            }
        }

        @Override
        public void render(MatrixStack matrixStack, int i, int j, float f) {
            int k = this.getScrollbarPositionX();
            int l = k + 6;
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
            this.client.getTextureManager().bindTexture(DrawableHelper.OPTIONS_BACKGROUND_TEXTURE);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

            int m = this.getRowLeft();
            int n = this.top + 4 - (int)this.getScrollAmount();

            this.renderList(matrixStack, m, n, i, j, f);

            RenderSystem.disableTexture();
            int q = Math.max(0, this.getMaxPosition() - (this.bottom - this.top - 4));
            if (q > 0) {
                int r = (int)((float)((this.bottom - this.top) * (this.bottom - this.top)) / (float)this.getMaxPosition());
                r = MathHelper.clamp(r, 32, this.bottom - this.top - 8);
                int s = (int)this.getScrollAmount() * (this.bottom - this.top - r) / q + this.top;
                if (s < this.top) {
                    s = this.top;
                }

                bufferBuilder.begin(7, VertexFormats.POSITION_COLOR_TEXTURE);
                bufferBuilder.vertex(k, this.bottom, 0.0).color(0, 0, 0, 255).texture(0.0F, 1.0F).next();
                bufferBuilder.vertex(l, this.bottom, 0.0).color(0, 0, 0, 255).texture(1.0F, 1.0F).next();
                bufferBuilder.vertex(l, this.top, 0.0).color(0, 0, 0, 255).texture(1.0F, 0.0F).next();
                bufferBuilder.vertex(k, this.top, 0.0).color(0, 0, 0, 255).texture(0.0F, 0.0F).next();
                bufferBuilder.vertex(k, (s + r), 0.0).color(128, 128, 128, 255).texture(0.0F, 1.0F).next();
                bufferBuilder.vertex(l, (s + r), 0.0).color(128, 128, 128, 255).texture(1.0F, 1.0F).next();
                bufferBuilder.vertex(l, s, 0.0).color(128, 128, 128, 255).texture(1.0F, 0.0F).next();
                bufferBuilder.vertex(k, s, 0.0).color(128, 128, 128, 255).texture(0.0F, 0.0F).next();
                bufferBuilder.vertex(k, (s + r - 1), 0.0).color(192, 192, 192, 255).texture(0.0F, 1.0F).next();
                bufferBuilder.vertex((l - 1), (s + r - 1), 0.0).color(192, 192, 192, 255).texture(1.0F, 1.0F).next();
                bufferBuilder.vertex((l - 1), s, 0.0).color(192, 192, 192, 255).texture(1.0F, 0.0F).next();
                bufferBuilder.vertex(k, s, 0.0).color(192, 192, 192, 255).texture(0.0F, 0.0F).next();
                tessellator.draw();
            }

            RenderSystem.enableTexture();
            RenderSystem.shadeModel(7424);
            RenderSystem.enableAlphaTest();
            RenderSystem.disableBlend();
        }

        @Override
        protected void renderList(MatrixStack matrixStack, int i, int j, int k, int l, float f) {
            Util.applyScissor(new Rectangle(0, top, this.width, bottom - top));
            super.renderList(matrixStack, i, j, k, l, f);
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }

        @Override
        public int getRowLeft() {
            return width/2;
        }
    }

    private class Credit extends EntryListWidget.Entry<Credit> {

        private final String name;
        private final String[] things;

        private boolean hovered;

        public Credit(String name, String... things){
            this.name=name;
            this.things=things;
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            if(hovered) {
                drawVerticalLine(matrices, x - 100, y, y + 20, io.github.axolotlclient.config.Color.ERROR.getAsInt());
                drawVerticalLine(matrices, x + 100, y, y + 20, io.github.axolotlclient.config.Color.ERROR.getAsInt());
                drawHorizontalLine(matrices, x - 100, x + 100, y + 20, io.github.axolotlclient.config.Color.ERROR.getAsInt());
                drawHorizontalLine(matrices, x - 100, x + 100, y, io.github.axolotlclient.config.Color.ERROR.getAsInt());
            }
            this.hovered=hovered;
            DrawUtil.drawCenteredString(matrices, MinecraftClient.getInstance().textRenderer, name, x, y + 5, hovered ? io.github.axolotlclient.config.Color.SELECTOR_RED.getAsInt() : -1);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if(hovered) {
                creditOverlay = new Overlay(this);
                return true;
            }
            return false;
        }
    }

    private class Overlay extends DrawUtil {

        private Window window;
        Credit credit;
        private final int x;
        private final int y;
        private int width;
        private int height;

        protected HashMap<String, ClickEvent> effects = new HashMap<>();
        protected HashMap<Integer, String> lines = new HashMap<>();

        public Overlay(Credit credit) {
            x=100;
            y=50;
            this.credit=credit;

            init();
        }

        public void init(){
            window = MinecraftClient.getInstance().getWindow();
            this.width=window.getScaledWidth()-200;
            this.height=window.getScaledHeight()-100;

            int startY=y+50;
            for(String t:credit.things){

                if(t.startsWith("http")){
                    effects.put(t, new ClickEvent(ClickEvent.Action.OPEN_URL, t));
                    lines.put(startY, Formatting.UNDERLINE + t);
                } else {
                    lines.put(startY, t);
                }
                startY+=12;
            }
        }

        public void render(MatrixStack matrices) {
            DrawUtil.fillRect(matrices, new io.github.axolotlclient.modules.hud.util.Rectangle(x, y, width, height), io.github.axolotlclient.config.Color.DARK_GRAY.withAlpha(127));
            DrawUtil.outlineRect(matrices, new Rectangle(x, y, width, height), io.github.axolotlclient.config.Color.BLACK);

            drawCenteredString(matrices, MinecraftClient.getInstance().textRenderer, credit.name, window.getScaledWidth()/2, y+7, -16784327);


            lines.forEach((integer, s) ->
                drawCenteredString(matrices, MinecraftClient.getInstance().textRenderer,
                    s, x+width/2, integer,
                    io.github.axolotlclient.config.Color.SELECTOR_GREEN.getAsInt())
            );
        }

        public boolean isMouseOver(double mouseX, double mouseY){
            return mouseX>=x && mouseX<=x+width && mouseY >=y && mouseY <= y+height;
        }

        public void mouseClicked(double mouseX, double mouseY){
            lines.forEach((integer, s) -> {
                if((mouseY>=integer && mouseY<integer+12) &&
                    (mouseX >= width/2F - MinecraftClient.getInstance().textRenderer.getWidth(s)/2F &&
                        mouseX<= width/2F + MinecraftClient.getInstance().textRenderer.getWidth(s)/2F)){
                    handleTextClick(Style.EMPTY.withClickEvent(effects.get(Formatting.strip(s))));
                }
            });
        }
    }

    private class SpacerTitle extends Credit {

        public SpacerTitle(String name) {
            super(name, "");
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            DrawUtil.drawCenteredString(matrices, MinecraftClient.getInstance().textRenderer, super.name, x, y, -128374);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return false;
        }
    }
}
