package io.github.axolotlclient.config.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.config.ConfigManager;
import io.github.axolotlclient.mixin.AccessorSoundManager;
import io.github.axolotlclient.mixin.AccessorSoundSystem;
import io.github.axolotlclient.modules.hud.util.DrawUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.util.Identifier;
import paulscode.sound.SoundSystem;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class CreditsScreen extends Screen {

    private final Screen parent;

    private final Map<String, String[]> credits = new HashMap<>();
    private final Map<String, String[]> other = new HashMap<>();

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

        if(this.client.world!=null)fillGradient(0,0, width, height, new Color(0xB0100E0E, true).hashCode(), new Color(0x46212020, true).hashCode());
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

        drawCenteredString(this.textRenderer, I18n.translate("credits"), width/2, height/4-40, -1);

        int authorX = width/6;
        int textX = width/2;
        AtomicInteger y = new AtomicInteger(height / 4 + 24);
        textRenderer.draw(I18n.translate("contributors"), authorX-40, y.get()-12, -1);
        credits.forEach((author, text)->{
            textRenderer.draw(author, authorX, y.get(), -1);
            textRenderer.draw(text[0], textX, y.get(), -1);
            for(int i=1;i<text.length;i++){
                textRenderer.draw(text[i], textX+30, y.addAndGet(12), -1);
            }
            y.addAndGet(12);
        });

        textRenderer.draw(I18n.translate("other_people"), authorX-40, y.addAndGet(24), -1);
        y.addAndGet(12);
        other.forEach((person, text)->{
            textRenderer.draw(person, authorX, y.get(), -1);
            textRenderer.draw(text[0], textX, y.get(), -1);
            for(int i=1;i<text.length;i++){
                textRenderer.draw(text[i], textX+30, y.addAndGet(12), -1);
            }
            y.addAndGet(12);
        });
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        if(button.id==0){client.openScreen(parent);stopBGM();}
        if(button.id==1){
            AxolotlClient.CONFIG.creditsBGM.toggle();
            ConfigManager.save();
            stopBGM();
            button.message = I18n.translate("creditsBGM")+ ": "+I18n.translate(AxolotlClient.CONFIG.creditsBGM.get()?"options.on":"options.off");
        }
    }

    @Override
    public void init() {
        this.buttons.add(new ButtonWidget(
                0, width/2 -75, height - 50 + 22, 150, 20,
                I18n.translate("back")));

        initCredits();

        this.buttons.add(new ButtonWidget(1, 6, this.height-26, 100, 20,
                I18n.translate("creditsBGM")+ ": "+I18n.translate(AxolotlClient.CONFIG.creditsBGM.get()?"options.on":"options.off")
        ));
    }

    private void initCredits(){
        credits.put("moehreag", new String[]{"Author, Programming", "https://github.com/moehreag"});
        credits.put("YakisikliBaran", new String[]{"Turkish Translation"});
        credits.put("DragonEggBedrockBreaking", new String[]{"Bugfixing, inspiration of new Features"});
        credits.put("TheKodeToad", new String[]{"Letting me use your Motion Blur implementation"});
        credits.put("kuchenag", new String[]{"Art"});

        other.put("DarkKronicle", new String[]{"Author of KronHUD, the best HUD mod!"});
        other.put("AMereBagatelle", new String[]{"Author of the excellent FabricSkyBoxes Mod"});

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
}
