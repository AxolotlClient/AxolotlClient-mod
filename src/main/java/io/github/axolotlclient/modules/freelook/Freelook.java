package io.github.axolotlclient.modules.freelook;

import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.config.options.BooleanOption;
import io.github.axolotlclient.config.options.DisableReason;
import io.github.axolotlclient.config.options.EnumOption;
import io.github.axolotlclient.config.options.OptionCategory;
import io.github.axolotlclient.modules.AbstractModule;
import net.legacyfabric.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.entity.Entity;
import org.lwjgl.input.Keyboard;

public class Freelook extends AbstractModule {

    private static final Freelook INSTANCE = new Freelook();
    private static final KeyBinding KEY = new KeyBinding("key.freelook", Keyboard.KEY_V, "category.axolotlclient");

    private final MinecraftClient client = MinecraftClient.getInstance();

    private float yaw, pitch;
    private boolean active;

    private final OptionCategory category = new OptionCategory("freelook");
    private final BooleanOption enabled = new BooleanOption("enabled", false);
    private final EnumOption perspective = new EnumOption("perspective", Perspective.values(), Perspective.THIRD_PERSON_BACK.toString());
    private final BooleanOption invert = new BooleanOption("invert", false);

    private int previousPerspective;

    public static Freelook getInstance() {
        return INSTANCE;
    }

    @Override
    public void init() {
        KeyBindingHelper.registerKeyBinding(KEY);
        category.add(enabled, perspective, invert);
        AxolotlClient.CONFIG.addCategory(category);
    }

    @Override
    public void tick() {

        if(isForbidden()){
            enabled.setForceOff(true, DisableReason.BAN_REASON);
        } else if (!isForbidden() && enabled.getForceDisabled()){
            enabled.setForceOff(false, null);
        }

        if(!enabled.get()) return;

        if(KEY.isPressed()) {
            if(!active) {
                start();
            }
        } else if(active) stop();
    }

    private void start() {
        active = true;

        previousPerspective = client.options.perspective;
        client.options.perspective = Perspective.valueOf(perspective.get()).ordinal();

        Entity camera = client.getCameraEntity();

        if(camera == null) camera = client.player;
        if(camera == null) return;

        yaw = camera.yaw;
        pitch = camera.pitch;
    }

    private void stop() {
        active = false;
        client.worldRenderer.scheduleTerrainUpdate();
        client.options.perspective = previousPerspective;
    }

    public boolean consumeRotation(float dx, float dy) {
        if(!active || !enabled.get()) return false;

        if(!invert.get()) dy = -dy;

        yaw += dx * 0.15F;
        pitch += dy * 0.15F;

        if(pitch > 90) {
            pitch = 90;
        } else if(pitch < -90) {
            pitch = -90;
        }

        client.worldRenderer.scheduleTerrainUpdate();
        return true;
    }

    public float yaw(float defaultValue) {
        if(!active || !enabled.get()) return defaultValue;

        return yaw;
    }

    public float pitch(float defaultValue) {
        if(!active || !enabled.get()) return defaultValue;

        return pitch;
    }

    private boolean isForbidden(){
        for(String a: disallowed_servers){
            if(MinecraftClient.getInstance().getCurrentServerEntry() != null &&
                    MinecraftClient.getInstance().getCurrentServerEntry().address.contains(a)){
                return true;
            }
        }
        return false;
    }

    private static final String[] disallowed_servers = new String[]{
            "hypixel", "mineplex", "gommehd"
    };

}
