package io.github.moehreag.axolotlclient.config.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.moehreag.axolotlclient.Axolotlclient;
import io.github.moehreag.axolotlclient.config.ConfigManager;
import io.github.moehreag.axolotlclient.config.screen.widgets.CustomButtonWidget;
import io.github.moehreag.axolotlclient.mixin.AccessorSoundManager;
import io.github.moehreag.axolotlclient.mixin.AccessorSoundSystem;
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

        if(Axolotlclient.CONFIG.creditsBGM.get() && !client.getSoundManager().isPlaying(bgm)){
            if(((AccessorSoundSystem) ((AccessorSoundManager) MinecraftClient.getInstance().getSoundManager()).getSoundSystem()).getField_8196().get(bgm) == null) {
                MinecraftClient.getInstance().getSoundManager().play(bgm);
            }
        }

        if(this.client.world!=null)fillGradient(0,0, width, height, new Color(0xB0100E0E, true).hashCode(), new Color(0x46212020, true).hashCode());
        else renderDirtBackground(0);
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
            y.addAndGet(12);
        });
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        if(button.id==0){client.openScreen(parent);stopBGM();}
        if(button.id==1){
            Axolotlclient.CONFIG.creditsBGM.toggle();
            ConfigManager.save();
            stopBGM();
            client.openScreen(new CreditsScreen(parent));
        }
    }

    @Override
    public void init() {
        this.buttons.add(new CustomButtonWidget(
                0, width/2 -75, height - 50 + 22, 150, 20,
                I18n.translate("back"),new Identifier("axolotlclient", "textures/gui/button1.png")));

        initCredits();

        this.buttons.add(new CustomButtonWidget(1, 6, this.height-26, 100, 20,
                I18n.translate("creditsBGM")+ ": "+I18n.translate(Axolotlclient.CONFIG.creditsBGM.get()?"options.on":"options.off"),
                new Identifier("axolotlclient", "textures/gui/button2.png")
        ));
    }

    private void initCredits(){
        credits.put("moehreag", new String[]{"Author, Programming", "https://github.com/moehreag"});
        credits.put("YakisikliBaran", new String[]{"Turkish Translation"});
        credits.put("DragonEggBedrockBreaking", new String[]{"Bugfixing, inspiration of new Features"});
        credits.put("kuchenag", new String[]{"Art"});

        other.put("DarkKronicle", new String[]{"Author of KronHUD, the best HUD mod!"});
        other.put("AMereBagatelle", new String[]{"Author of the excellent FabricSkyBoxes Mod"});

    }

    private void stopBGM(){
        String source = ((AccessorSoundSystem) ((AccessorSoundManager) MinecraftClient.getInstance().getSoundManager()).getSoundSystem()).getField_8196().get(bgm);
        if(source!=null) {
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
