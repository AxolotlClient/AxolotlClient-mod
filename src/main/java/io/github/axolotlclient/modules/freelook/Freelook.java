package io.github.axolotlclient.modules.freelook;

import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.config.options.BooleanOption;
import io.github.axolotlclient.config.options.DisableReason;
import io.github.axolotlclient.config.options.EnumOption;
import io.github.axolotlclient.config.options.OptionCategory;
import io.github.axolotlclient.modules.AbstractModule;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.options.Perspective;
import net.minecraft.entity.Entity;
import org.lwjgl.glfw.GLFW;

public class Freelook extends AbstractModule {
    private static final Freelook INSTANCE = new Freelook();
    private static final KeyBinding KEY = new KeyBinding("key.freelook", GLFW.GLFW_KEY_V, "category.axolotlclient");

    private final MinecraftClient client = MinecraftClient.getInstance();

    private float yaw, pitch;
    public boolean active;

    private final OptionCategory category = new OptionCategory("freelook");
    public final BooleanOption enabled = new BooleanOption("enabled", false);
    private final EnumOption perspective = new EnumOption("perspective", Perspective.values(), Perspective.THIRD_PERSON_BACK);
    private final BooleanOption invert = new BooleanOption("invert", false);

    private Perspective previousPerspective;

    public static Freelook getInstance(){
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

        previousPerspective = client.options.getPerspective();
        setPerspective(Perspective.valueOf(perspective.get()));

        Entity camera = client.getCameraEntity();

        if(camera == null) camera = client.player;
        if(camera == null) return;

        yaw = camera.getYaw(1);
        pitch = camera.getPitch(1);
    }

    private void stop() {
        active = false;
        client.worldRenderer.scheduleTerrainUpdate();
        setPerspective(previousPerspective);
    }

    public boolean consumeRotation(double dx, double dy) {
        if(!active || !enabled.get()) return false;

        if(!invert.get()) dy = -dy;

        if(MinecraftClient.getInstance().options.getPerspective().isFrontView()||
            MinecraftClient.getInstance().options.getPerspective().isFirstPerson()) dy*=-1;

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

    private void setPerspective(Perspective perspective){
        MinecraftClient.getInstance().options.method_31043(perspective);
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
