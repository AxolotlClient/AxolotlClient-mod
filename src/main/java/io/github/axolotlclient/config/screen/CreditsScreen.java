package io.github.axolotlclient.config.screen;

import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.config.ConfigManager;
import io.github.axolotlclient.modules.hud.util.DrawUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class CreditsScreen extends Screen {

    private final Screen parent;

    private final Map<String, String[]> credits = new HashMap<>();
    private final Map<String, String[]> other = new HashMap<>();

    private final SoundInstance bgm = PositionedSoundInstance.master(SoundEvents.MUSIC_DISC_CHIRP, 1, 1);

    public CreditsScreen(Screen parent){
	    super(Text.of(""));
	    this.parent=parent;
    }


	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {

        if(AxolotlClient.CONFIG.creditsBGM.get() && !MinecraftClient.getInstance().getSoundManager().isPlaying(bgm)){

	        MinecraftClient.getInstance().getSoundManager().play(bgm);
        }

        if(MinecraftClient.getInstance().world!=null)fillGradient(matrices, 0,0, width, height, new Color(0xB0100E0E, true).hashCode(), new Color(0x46212020, true).hashCode());
        else renderBackgroundTexture(0);
        if(AxolotlClient.someNiceBackground.get()) { // Credit to pridelib for the colors
            DrawUtil.fill(matrices, 0, 0, width, height/6, 0xFFff0018);
            DrawUtil.fill(matrices, 0, height/6, width, height*2/6, 0xFFffa52c);
            DrawUtil.fill(matrices, 0, height*2/6, width, height/2, 0xFFffff41);
            DrawUtil.fill(matrices, 0, height*2/3, width, height*5/6, 0xFF0000f9);
            DrawUtil.fill(matrices, 0, height/2, width, height*2/3, 0xFF008018);
            DrawUtil.fill(matrices, 0, height*5/6, width, height, 0xFF86007d);
        }
		super.render(matrices, mouseX, mouseY, delta);


        drawCenteredText(matrices, this.textRenderer, new TranslatableText("credits"), width/2, height/4-40, -1);

        int authorX = width/6;
        int textX = width/2;
        AtomicInteger y = new AtomicInteger(height / 4 + 24);
        textRenderer.draw(matrices, I18n.translate("contributors"), authorX-40, y.get()-12, -1);
        credits.forEach((author, text)->{
            textRenderer.draw(matrices, author, authorX, y.get(), -1);
            textRenderer.draw(matrices, text[0], textX, y.get(), -1);
            for(int i=1;i<text.length;i++){
                textRenderer.draw(matrices, text[i], textX+30, y.addAndGet(12), -1);
            }
            y.addAndGet(12);
        });

        textRenderer.draw(matrices, I18n.translate("other_people"), authorX-40, y.addAndGet(24), -1);
        y.addAndGet(12);
        other.forEach((person, text)->{
            textRenderer.draw(matrices, person, authorX, y.get(), -1);
            textRenderer.draw(matrices, text[0], textX, y.get(), -1);
            for(int i=1;i<text.length;i++){
                textRenderer.draw(matrices, text[i], textX+30, y.addAndGet(12), -1);
            }
            y.addAndGet(12);
        });
    }

    @Override
    public void init() {
        this.addButton(new ButtonWidget(
			width/2 -75, height - 50 + 22, 150, 20,
	        new TranslatableText("back"), buttonWidget -> {MinecraftClient.getInstance().openScreen(parent); stopBGM();}));

        initCredits();

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
		MinecraftClient.getInstance().getSoundManager().stop(bgm);
    }

	@Override
	public void onClose() {
		stopBGM();
		super.onClose();
	}
}
