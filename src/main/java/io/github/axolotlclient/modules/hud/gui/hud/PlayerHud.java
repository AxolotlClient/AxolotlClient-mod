package io.github.axolotlclient.modules.hud.gui.hud;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.AxolotlclientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlclientConfig.options.DoubleOption;
import io.github.axolotlclient.AxolotlclientConfig.options.Option;
import io.github.axolotlclient.AxolotlclientConfig.options.OptionBase;
import io.github.axolotlclient.modules.hud.gui.entry.BoxHudEntry;
import io.github.axolotlclient.util.Hooks;
import lombok.Getter;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 * @license GPL-3.0
 */

public class PlayerHud extends BoxHudEntry {

    public static final Identifier ID = new Identifier("kronhud", "playerhud");

    private final DoubleOption rotation = new DoubleOption("axolotlclient.rotation", 0, 0, 360);
    private final BooleanOption dynamicRotation = new BooleanOption("axolotlclient.dynamicrotation", true);

    private float lastYawOffset = 0;
    private float yawOffset = 0;
    private float lastYOffset = 0;
    private float yOffset = 0;

    @Getter
    private static boolean currentlyRendering = false;

    public PlayerHud() {
        super(62, 94, true);
        Hooks.PLAYER_DIRECTION_CHANGE.register(this::onPlayerDirectionChange);
    }

    @Override
    public void renderComponent(float delta) {
        renderPlayer(getTruePos().x() + 31 * getScale(), getTruePos().y() + 86 * getScale(), delta);
    }

    @Override
    public void renderPlaceholderComponent(float delta) {
        renderPlayer(getTruePos().x() + 31 * getScale(), getTruePos().y() + 86 * getScale(), 0); // If delta was delta, it would start jittering
    }

    public void renderPlayer(double x, double y, float delta) {
        if (client.player == null) {
            return;
        }

        float lerpY = (lastYOffset + ((yOffset - lastYOffset) * delta));

        GlStateManager.color4f(1, 1, 1, 1);
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translated(x, y - lerpY, 1050);
        GlStateManager.scalef(1, 1, -1);

        GlStateManager.translatef(0, 0, 1000);
        float scale = getScale() * 40;
        GlStateManager.scalef(scale, scale, scale);


        GlStateManager.rotatef(180, 0, 0, 1);

        // Rotate to whatever is wanted. Also make sure to offset the yaw
        float deltaYaw = client.player.yaw;
        if (dynamicRotation.get()) {
            deltaYaw -= (lastYawOffset + ((yawOffset - lastYawOffset) * delta));
        }

        // Save these to set them back later
        float pastYaw = client.player.yaw;
        float pastBodyYaw = client.player.bodyYaw;
        float pastHeadYaw = client.player.headYaw;
        float pastPrevHeadYaw = client.player.prevHeadYaw;
        float pastPrevYaw = client.player.prevYaw;

        client.player.headYaw = client.player.yaw;
        client.player.prevHeadYaw = client.player.yaw;

        GlStateManager.rotatef(deltaYaw - 180 + rotation.get().floatValue(), 0, 1, 0);
        DiffuseLighting.enableNormally();
        EntityRenderDispatcher renderer = client.getEntityRenderManager();
        renderer.setYaw(180);
        renderer.pitch = 0;
        renderer.setRenderShadows(false);


        //VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();

        currentlyRendering = true;
        renderer.render(client.player, 0.0, 0.0, 0.0, 0, delta);
        currentlyRendering = false;
        //renderer.render(client.player, 0, 0, 0, delta, 15728880);

        renderer.setRenderShadows(true);
        GlStateManager.popMatrix();

        client.player.yaw = pastYaw;
        client.player.headYaw = pastHeadYaw;
        client.player.prevHeadYaw = pastPrevHeadYaw;
        client.player.prevYaw = pastPrevYaw;
        client.player.bodyYaw = pastBodyYaw;


        DiffuseLighting.disable();
        GlStateManager.disableRescaleNormal();
        GlStateManager.activeTexture(GLX.lightmapTextureUnit);
        GlStateManager.disableTexture();
        GlStateManager.activeTexture(GLX.textureUnit);
        //DiffuseLighting.setup3DGuiLighting();
    }

    public void onPlayerDirectionChange(float prevPitch, float prevYaw, float pitch, float yaw) {
        yawOffset += (yaw - prevYaw) / 2;
    }

    @Override
    public boolean tickable() {
        return true;
    }

    @Override
    public void tick() {
        lastYawOffset = yawOffset;
        yawOffset *= .93f;
        lastYOffset = yOffset;
        yOffset *= .8;
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public boolean movable() {
        return true;
    }

    @Override
    public List<Option<?>> getConfigurationOptions() {
        List<Option<?>> options = super.getConfigurationOptions();
        options.add(dynamicRotation);
        options.add(rotation);
        return options;
    }
}
