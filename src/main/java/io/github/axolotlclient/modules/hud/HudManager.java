package io.github.axolotlclient.modules.hud;

import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.config.options.OptionCategory;
import io.github.axolotlclient.modules.AbstractModule;
import io.github.axolotlclient.modules.hud.gui.AbstractHudEntry;
import io.github.axolotlclient.modules.hud.gui.hud.*;
import io.github.axolotlclient.config.Color;
import io.github.axolotlclient.modules.hud.util.Rectangle;
import net.legacyfabric.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.legacyfabric.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This implementation of Hud modules is based on KronHUD.
 * https://github.com/DarkKronicle/KronHUD
 * Licensed under GPL-3.0
 */


public class HudManager extends AbstractModule {

    private final Map<Identifier, AbstractHudEntry> entries = new LinkedHashMap<>();

    private final OptionCategory hudCategory = new OptionCategory("hud");

    private final MinecraftClient client = MinecraftClient.getInstance();
    private static final HudManager INSTANCE = new HudManager();

    static KeyBinding key = new KeyBinding("key.openHud", 54, "category.axolotlclient");

    public static HudManager getInstance(){
        return INSTANCE;
    }

    @Override
    public void init(){

        Color.setupChroma();

        KeyBindingHelper.registerKeyBinding(key);

        AxolotlClient.config.add(HudEditScreen.snapping);
        AxolotlClient.CONFIG.addCategory(hudCategory);

        HudRenderCallback.EVENT.register((client, tickDelta)->render());

        add(new PingHud());
        add(new FPSHud());
        add(new CPSHud());
        add(new ArmorHud());
        add(new PotionsHud());
        add(new KeystrokeHud());
        add(new ToggleSprintHud());
        add(new IPHud());
        add(new IconHud());
        add(new SpeedHud());
        add(new ScoreboardHud());
        add(new CrosshairHud());
        add(new CoordsHud());
        add(new ActionBarHud());
        add(new BossBarHud());
        add(new ChatHud());
        add(new ArrowHud());
        add(new ItemUpdateHud());
        add(new PackDisplayHud());
        add(new RealTimeHud());
        add(new HotbarHUD());
        add(new MemoryHud());

        entries.forEach((identifier, abstractHudEntry) -> abstractHudEntry.init());
    }

    @Override
    public void tick(){
        if(key.isPressed()) MinecraftClient.getInstance().openScreen(new HudEditScreen());
        entries.values().forEach(abstractHudEntry -> {
            if(abstractHudEntry.tickable() && abstractHudEntry.isEnabled())abstractHudEntry.tick();
        });
    }

    public HudManager add(AbstractHudEntry entry) {
        entries.put(entry.getId(), entry);
        hudCategory.addSubCategory(entry.getAllOptions());
        return this;
    }

    public List<AbstractHudEntry> getEntries() {
        if (entries.size() > 0) {
            return new ArrayList<>(entries.values());
        }
        return new ArrayList<>();
    }

    public List<AbstractHudEntry> getMovableEntries() {
        if (entries.size() > 0) {
            return entries.values().stream().filter((entry) -> entry.isEnabled() && entry.movable()).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public AbstractHudEntry get(Identifier identifier) {
        return entries.get(identifier);
    }

    public void render() {
        client.profiler.push("Hud Modules");
        if (!(client.currentScreen instanceof HudEditScreen) && !client.options.debugEnabled) {
            for (AbstractHudEntry hud : getEntries()) {
                if (hud.isEnabled()) {
                    client.profiler.push(hud.getName());
                    hud.renderHud();
                    client.profiler.pop();
                }
            }
        }
        client.profiler.pop();
    }

    public Optional<AbstractHudEntry> getEntryXY(int x, int y) {
        for (AbstractHudEntry entry : getMovableEntries()) {
            Rectangle bounds = entry.getScaledBounds();
            if (bounds.x <= x && bounds.x + bounds.width >= x && bounds.y <= y && bounds.y + bounds.height >= y) {
                return Optional.of(entry);
            }
        }
        return Optional.empty();
    }

    public void renderPlaceholder() {
        for (AbstractHudEntry hud : getEntries()) {
            if (hud.isEnabled()) {
                hud.renderPlaceholder();
            }
        }
    }

    public List<Rectangle> getAllBounds() {
        ArrayList<Rectangle> bounds = new ArrayList<>();
        for (AbstractHudEntry entry : getMovableEntries()) {
            bounds.add(entry.getScaledBounds());
        }
        return bounds;
    }
}
