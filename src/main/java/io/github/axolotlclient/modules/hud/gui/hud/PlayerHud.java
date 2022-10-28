package io.github.axolotlclient.modules.hud.gui.hud;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.AxolotlclientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlclientConfig.options.DoubleOption;
import io.github.axolotlclient.AxolotlclientConfig.options.OptionBase;
import io.github.axolotlclient.modules.hud.gui.entry.BoxHudEntry;
import io.github.axolotlclient.util.Hooks;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.util.Identifier;

import java.util.List;

public class PlayerHud extends BoxHudEntry {

    public static final Identifier ID = new Identifier("kronhud", "playerhud");

    private final DoubleOption rotation = new DoubleOption("rotation", 0, 0, 360);
    private final BooleanOption dynamicRotation = new BooleanOption("dynamicrotation", true);

    private float lastYawOffset = 0;
    private float yawOffset = 0;
    private float lastYOffset = 0;
    private float yOffset = 0;

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
        //SurvivalInventoryScreen.renderEntity(getPos().x, getPos().y, height, getPos().x, getPos().y, client.player);

        float lerpY = (lastYOffset + ((yOffset - lastYOffset) * delta));

        //MatrixStack matrixStack = RenderSystem.getModelViewStack();
        GlStateManager.color4f(1, 1, 1, 1);
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translated(x, y - lerpY, 1050);
        GlStateManager.scalef(1, 1, -1);

        //RenderSystem.applyModelViewMatrix();
        //MatrixStack nextStack = new MatrixStack();
        //GlStateManager.pushMatrix();
        GlStateManager.translatef(0, 0, 1000);
        float scale = getScale() * 40;
        GlStateManager.scalef(scale, scale, scale);

        //Quaternion quaternion = Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0F);

        GlStateManager.rotatef(180, 0, 0, 1);
        //nextStack.multiply(quaternion);
        // Rotate to whatever is wanted. Also make sure to offset the yaw
        float deltaYaw = client.player.yaw;
        if (dynamicRotation.get()) {
            deltaYaw -= (lastYawOffset + ((yawOffset - lastYawOffset) * delta));
        }
        //nextStack.multiply(new Quaternion(new Vec3f(0, 1, 0), deltaYaw - 180 + rotation.get().floatValue(), true));

        // Save these to set them back later
        float pastYaw = client.player.yaw;
        float pastBodyYaw = client.player.bodyYaw;
        float pastHeadYaw = client.player.headYaw;
        float pastPrevHeadYaw = client.player.prevHeadYaw;
        float pastPrevYaw = client.player.prevYaw;

        GlStateManager.rotatef(deltaYaw - 180 + rotation.get().floatValue(), 0, 1, 0);
        DiffuseLighting.enableNormally();
        EntityRenderDispatcher renderer = client.getEntityRenderManager();
        renderer.setYaw(180);
        renderer.setRenderShadows(false);


        //VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();

        renderer.render(client.player, 0.0, 0.0, 0.0, 0.0F, 1.0F);
        //renderer.render(client.player, 0, 0, 0, delta, 15728880);
        //immediate.draw();
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
    public List<OptionBase<?>> getConfigurationOptions() {
        List<OptionBase<?>> options = super.getConfigurationOptions();
        options.add(dynamicRotation);
        options.add(rotation);
        return options;
    }
}
