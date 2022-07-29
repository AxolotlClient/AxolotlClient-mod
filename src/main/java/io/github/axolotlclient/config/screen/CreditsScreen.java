package io.github.axolotlclient.config.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.config.Color;
import io.github.axolotlclient.config.ConfigManager;
import io.github.axolotlclient.mixin.AccessorSoundManager;
import io.github.axolotlclient.mixin.AccessorSoundSystem;
import io.github.axolotlclient.modules.hud.util.DrawUtil;
import io.github.axolotlclient.modules.hud.util.Rectangle;
import io.github.axolotlclient.util.Util;
import net.minecraft.client.MinecraftClient;
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
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import paulscode.sound.SoundSystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CreditsScreen extends Screen {

    private final Screen parent;

    private final List<Credit> credits = new ArrayList<>();

    private Overlay creditOverlay;

    private EntryListWidget creditsList;

    private final SoundInstance bgm = PositionedSoundInstance.method_7051(new Identifier("minecraft", "records.chirp"));

    public CreditsScreen(Screen parent){
        this.parent=parent;
    }

    @Override
    public void render(int mouseX, int mouseY, float tickDelta) {

        if(AxolotlClient.CONFIG.creditsBGM.get() && !client.getSoundManager().isPlaying(bgm)){
            if(((AccessorSoundSystem) ((AccessorSoundManager) MinecraftClient.getInstance().getSoundManager()).getSoundSystem()).getField_8196().get(bgm) == null) {
                MinecraftClient.getInstance().getSoundManager().play(bgm);
            }
        }

        if(this.client.world!=null)fillGradient(0,0, width, height, new Color(0xB0100E0E).getAsInt(), new Color(0x46212020).getAsInt());
        else renderDirtBackground(0);
        if(AxolotlClient.someNiceBackground.get()) { // Credit to pridelib for the colors
            DrawUtil.fill(0, 0, width, height/6, 0xFFff0018);
            DrawUtil.fill(0, height/6, width, height*2/6, 0xFFffa52c);
            DrawUtil.fill(0, height*2/6, width, height/2, 0xFFffff41);
            DrawUtil.fill(0, height*2/3, width, height*5/6, 0xFF0000f9);
            DrawUtil.fill(0, height/2, width, height*2/3, 0xFF008018);
            DrawUtil.fill(0, height*5/6, width, height, 0xFF86007d);
        }

        GlStateManager.enableAlphaTest();
        super.render(mouseX, mouseY, tickDelta);
        GlStateManager.disableAlphaTest();

        drawCenteredString(this.textRenderer, I18n.translate("credits"), width/2, 20, -1);

        if(creditOverlay!=null){
            creditOverlay.render();
        } else {
            creditsList.render(mouseX, mouseY, tickDelta);
        }
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        if(button.id==0){
            if(creditOverlay==null) {
                client.openScreen(parent);
                stopBGM();
            } else {
                creditOverlay=null;
            }
        } else if(button.id==1){
            AxolotlClient.CONFIG.creditsBGM.toggle();
            ConfigManager.save();
            stopBGM();
            button.message = I18n.translate("creditsBGM") + ": " + I18n.translate(AxolotlClient.CONFIG.creditsBGM.get() ? "options.on" : "options.off");
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
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
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int button) {
        creditsList.mouseReleased(mouseX, mouseY, button);
        super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        if(creditOverlay!=null)creditOverlay.init();
        super.resize(client, width, height);
    }

    @Override
    public void init() {
        this.buttons.add(new ButtonWidget(
                0, width/2 -75, height - 50 + 22, 150, 20,
                I18n.translate("back")));

        this.buttons.add(new ButtonWidget(1, 6, this.height-26, 100, 20,
                I18n.translate("creditsBGM")+ ": "+I18n.translate(AxolotlClient.CONFIG.creditsBGM.get()?"options.on":"options.off")
        ));

        credits.clear();
        initCredits();

        creditsList = new EntryListWidget(client, width, height, 50, height-50, 25) {

            @Override
            protected int getEntryCount() {
                return credits.size();
            }

            @Override
            public Entry getEntry(int index) {
                return credits.get(index);
            }

            @Override
            protected void renderList(int x, int y, int mouseX, int mouseY) {
                Util.applyScissor(new Rectangle(0, yStart, this.width, yEnd-yStart));
                super.renderList(x, y, mouseX, mouseY);
                GL11.glDisable(GL11.GL_SCISSOR_TEST);
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
                    int k = this.width / 2;
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
                    GlStateManager.popMatrix();
                    GlStateManager.disableBlend();
                }
            }
        };

    }

    private void initCredits(){

        credits.add(new SpacerTitle("- - - - - - "+I18n.translate("contributors")+" - - - - - -"));

        credits.add(new Credit("moehreag", "Author, Programming", "https://github.com/moehreag"));
        credits.add(new Credit("YakisikliBaran", "Turkish Translation"));
        credits.add(new Credit("TheKodeToad", "Contributor", "Motion Blur", "Free Perspective"));
        credits.add(new Credit("DragonEggBedrockBreaking", "Bugfixing", "Inspiration of new Features"));

        credits.add(new SpacerTitle("- - - - - - "+I18n.translate("other_people")+" - - - - - -"));

        credits.add(new Credit("DarkKronicle", "Author of KronHUD, the best HUD mod!"));
        credits.add(new Credit("AMereBagatelle", "Author of the excellent FabricSkyBoxes Mod"));

    }

    private void stopBGM(){
        if(((AccessorSoundSystem) ((AccessorSoundManager) MinecraftClient.getInstance().
                getSoundManager()).getSoundSystem()).
                getField_8196().get(bgm)!=null) {
            ((SoundSystem) ((AccessorSoundManager) MinecraftClient.getInstance()
                    .getSoundManager())
                    .getSoundSystem()
                    .field_8193)
                    .stop(
                            ((AccessorSoundSystem) ((AccessorSoundManager) MinecraftClient.getInstance()
                            .getSoundManager())
                            .getSoundSystem())
                            .getField_8196()
                            .get(bgm));
            ((SoundSystem) ((AccessorSoundManager) MinecraftClient.getInstance()
                    .getSoundManager())
                    .getSoundSystem()
                    .field_8193)
                    .removeSource(
                            ((AccessorSoundSystem) ((AccessorSoundManager) MinecraftClient.getInstance()
                            .getSoundManager())
                                    .getSoundSystem())
                                    .getField_8196()
                                    .get(bgm));
        }
    }

    @Override
    protected void keyPressed(char character, int code) {
        if(code==1){
            stopBGM();
            this.client.openScreen(null);
            if (this.client.currentScreen == null) {
                this.client.closeScreen();
            }
        }
    }

    @Override
    public void handleMouse() {
        creditsList.handleMouse();
        super.handleMouse();
    }

    private class Credit extends DrawUtil implements EntryListWidget.Entry {

        private final String name;
        private final String[] things;

        private boolean hovered;

        public Credit(String name, String... things){
            this.name=name;
            this.things=things;
        }

        @Override
        public void updatePosition(int index, int x, int y) {}

        @Override
        public void render(int index, int x, int y, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean hovered) {
            if(hovered) {
                drawVerticalLine(x - 100, y, y + 20, io.github.axolotlclient.config.Color.ERROR.getAsInt());
                drawVerticalLine(x + 100, y, y + 20, io.github.axolotlclient.config.Color.ERROR.getAsInt());
                drawHorizontalLine(x - 100, x + 100, y + 20, io.github.axolotlclient.config.Color.ERROR.getAsInt());
                drawHorizontalLine(x - 100, x + 100, y, io.github.axolotlclient.config.Color.ERROR.getAsInt());
            }
            this.hovered=hovered;
            drawCenteredString(MinecraftClient.getInstance().textRenderer, name, x, y + 5, hovered ? io.github.axolotlclient.config.Color.SELECTOR_RED.getAsInt() : -1);
        }

        @Override
        public boolean mouseClicked(int index, int mouseX, int mouseY, int button, int x, int y) {
            if(hovered) {
                creditOverlay = new Overlay(this);
                return true;
            }
            return false;
        }

        @Override
        public void mouseReleased(int index, int mouseX, int mouseY, int button, int x, int y) {}
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
            window = new Window(MinecraftClient.getInstance());
            width=window.getWidth()-200;
            height=window.getHeight()-100;

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

        public void render() {
            DrawUtil.fillRect(new io.github.axolotlclient.modules.hud.util.Rectangle(x, y, width, height), io.github.axolotlclient.config.Color.DARK_GRAY.withAlpha(127));
            DrawUtil.outlineRect(new Rectangle(x, y, width, height), io.github.axolotlclient.config.Color.BLACK);

            drawCenteredString(MinecraftClient.getInstance().textRenderer, credit.name, window.getWidth()/2, y+7, -16784327);


            lines.forEach((integer, s) ->
                    drawCenteredString(MinecraftClient.getInstance().textRenderer,
                            s, x+width/2, integer,
                            io.github.axolotlclient.config.Color.SELECTOR_GREEN.getAsInt())
            );
        }

        public boolean isMouseOver(int mouseX, int mouseY){
            return mouseX>=x && mouseX<=x+width && mouseY >=y && mouseY <= y+height;
        }

        public void mouseClicked(int mouseX, int mouseY){
            lines.forEach((integer, s) -> {
                if(mouseY>=integer && mouseY<integer+12){
                    handleTextClick(new LiteralText(s).setStyle(new Style().setClickEvent(effects.get(Formatting.strip(s)))));
                }
            });
        }
    }

    private class SpacerTitle extends Credit {

        public SpacerTitle(String name) {
            super(name, "");
        }

        @Override
        public void render(int index, int x, int y, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean hovered) {
            drawCenteredString(MinecraftClient.getInstance().textRenderer, super.name, x, y, -128374);
        }

        @Override
        public boolean mouseClicked(int index, int mouseX, int mouseY, int button, int x, int y) {return false;}
    }
}
