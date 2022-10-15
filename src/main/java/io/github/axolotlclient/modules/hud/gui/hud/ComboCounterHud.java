package io.github.axolotlclient.modules.hud.gui.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

public class ComboCounterHud extends CleanHudEntry {
    public static Identifier ID = new Identifier("combocounterhud");

    private int count;
    private long lastHitTime;

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public String getValue() {
        if(count == 0){
            return I18n.translate("combocounter.no_hits");
        } else if (count == 1) {
            return I18n.translate("combocounter.one_hit");
        }
        return I18n.translate("combocounter.hits", count);
    }

    @Override
    public String getPlaceholder() {
        return I18n.translate("combocounter.no_hits");
    }

    @Override
    public void tick() {
        if(count > 0 && MinecraftClient.getTime() - lastHitTime > 2000){
            count = 0;
        }
    }

    @Override
    public boolean tickable() {
        return true;
    }

    public void onEntityDamaged(Entity e){
        if(e.equals(MinecraftClient.getInstance().player)){
            count = 0;
        } else {
            count++;
            lastHitTime = MinecraftClient.getTime();
        }
    }
}
