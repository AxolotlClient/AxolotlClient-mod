package io.github.axolotlclient.modules.freelook;

import com.mojang.blaze3d.platform.InputUtil;
import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.config.options.BooleanOption;
import io.github.axolotlclient.config.options.EnumOption;
import io.github.axolotlclient.config.options.OptionCategory;
import io.github.axolotlclient.modules.AbstractModule;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBind;
import net.minecraft.client.option.Perspective;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

public class Freelook extends AbstractModule {

    public static final Identifier ID = new Identifier("freelook");
    public static final Freelook INSTANCE = new Freelook();
    private static final KeyBind KEY = new KeyBind("key.freelook", InputUtil.KEY_V_CODE, "category.axolotlclient");

    private final MinecraftClient client = MinecraftClient.getInstance();

    private float yaw, pitch;
    public boolean active;

    private final OptionCategory category = new OptionCategory(ID, ID.getPath());
    public final BooleanOption enabled = new BooleanOption("enabled", false);
    private final EnumOption perspective = new EnumOption("perspective", Perspective.values(), Perspective.THIRD_PERSON_BACK);
    private final BooleanOption invert = new BooleanOption("invert", false);

    private Perspective previousPerspective;

    @Override
    public void init() {
        KeyBindingHelper.registerKeyBinding(KEY);
        category.add(enabled, perspective, invert);
        AxolotlClient.CONFIG.addCategory(category);
    }

    @Override
    public void tick() {

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

        yaw = camera.getYaw();
        pitch = camera.getPitch();
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
        MinecraftClient.getInstance().options.setPerspective(perspective);

    }

}
