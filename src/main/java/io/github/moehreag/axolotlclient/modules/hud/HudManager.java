package io.github.moehreag.axolotlclient.modules.hud;

import io.github.moehreag.axolotlclient.Axolotlclient;
import io.github.moehreag.axolotlclient.modules.AbstractModule;
import io.github.moehreag.axolotlclient.modules.hud.gui.AbstractHudEntry;
import io.github.moehreag.axolotlclient.modules.hud.gui.hud.FPSHud;
import io.github.moehreag.axolotlclient.modules.hud.gui.hud.PingHud;
import io.github.moehreag.axolotlclient.modules.hud.util.Rectangle;
import net.legacyfabric.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.stream.Collectors;

public class HudManager extends AbstractModule {

    private final Map<Identifier, AbstractHudEntry> entries = new HashMap<>();
    private final MinecraftClient client = MinecraftClient.getInstance();
    private static final HudManager INSTANCE = new HudManager();

    private PingHud test1 = new PingHud();

    static KeyBinding key = new KeyBinding("key.openHud", 54, "category.axolotlclient");

    public void save(){

    }

    public static HudManager getINSTANCE(){
        return INSTANCE;
    }

    public void init(){

        KeyBindingHelper.registerKeyBinding(key);

        test1.toggle();
        add(test1);
        add(new FPSHud());
    }

    public static void tick(){
        if(key.isPressed()) MinecraftClient.getInstance().openScreen(new HudEditScreen());
    }

    public HudManager add(AbstractHudEntry entry) {
        entries.put(entry.getId(), entry);
        return this;
    }

    public List<AbstractHudEntry> getEntries() {
        if (entries.size() > 0) {
            return new ArrayList<>(entries.values());
        }
        return new ArrayList<>();
    }

    public List<AbstractHudEntry> getMoveableEntries() {
        if (entries.size() > 0) {
            return entries.values().stream().filter((entry) -> entry.isEnabled() && entry.movable()).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public AbstractHudEntry get(Identifier identifier) {
        return entries.get(identifier);
    }

    public void render() {
        if (!(client.currentScreen instanceof HudEditScreen) && !client.options.debugEnabled) {
            for (AbstractHudEntry hud : getEntries()) {
                if (hud.isEnabled()) {
                    hud.renderHud();
                }
            }
        }
    }

    public void renderPlaceholder() {
        for (AbstractHudEntry hud : getEntries()) {
            if (hud.isEnabled()) {
                hud.renderPlaceholder();
            }
        }
    }

    public Optional<AbstractHudEntry> getEntryXY(int x, int y) {
        for (AbstractHudEntry entry : getMoveableEntries()) {
            Rectangle bounds = entry.getScaledBounds();
            if (bounds.x <= x && bounds.x + bounds.width >= x && bounds.y <= y && bounds.y + bounds.height >= y) {
                return Optional.of(entry);
            }
        }
        return Optional.empty();
    }

    public List<Rectangle> getAllBounds() {
        ArrayList<Rectangle> bounds = new ArrayList<>();
        for (AbstractHudEntry entry : getMoveableEntries()) {
            bounds.add(entry.getScaledBounds());
        }
        return bounds;
    }
}
