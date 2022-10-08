package io.github.axolotlclient.modules.motionblur;

import com.google.gson.JsonSyntaxException;
import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.AxolotlclientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlclientConfig.options.FloatOption;
import io.github.axolotlclient.AxolotlclientConfig.options.OptionCategory;
import io.github.axolotlclient.mixin.ShaderEffectAccessor;
import io.github.axolotlclient.modules.AbstractModule;
import io.github.axolotlclient.util.Logger;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.client.resource.ResourceMetadataProvider;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class MotionBlur extends AbstractModule {

    private static final MotionBlur Instance = new MotionBlur();

    private final Identifier shaderLocation = new Identifier("minecraft:shaders/post/motion_blur.json");

    public final BooleanOption enabled = new BooleanOption("enabled", false);
    public final FloatOption strength = new FloatOption("strength", 50F, 1F, 99F);
    public final BooleanOption inGuis = new BooleanOption("inGuis", false);

    public final OptionCategory category = new OptionCategory("motionBlur");

    public ShaderEffect shader;
    private final MinecraftClient client = MinecraftClient.getInstance();
    private float currentBlur;

    private int lastWidth;

    private int lastHeight;

    public static MotionBlur getInstance(){
        return Instance;
    }

    @Override
    public void init() {
        category.add(enabled, strength, inGuis);

        AxolotlClient.CONFIG.rendering.addSubCategory(category);

        AxolotlClient.runtimeResources.put(shaderLocation, new MotionBlurShader());
    }

    public void onUpdate() {
        if((shader == null || client.width!=lastWidth || client.height!=lastHeight) && client.height!=0 && client.width!=0) {
            currentBlur=getBlur();
            try {
                shader = new ShaderEffect(client.getTextureManager(),
                        client.getResourceManager(), client.getFramebuffer(),
                        shaderLocation);
                shader.setupDimensions(client.width, client.height);
            } catch (JsonSyntaxException | IOException e) {
                Logger.error("Could not load motion blur", e);
            }
        }
        if(currentBlur!=getBlur()){
            ((ShaderEffectAccessor)shader).getPasses().forEach(shader -> {
                GlUniform blendFactor = shader.getProgram().getUniformByName("BlendFactor");
                if(blendFactor!=null){
                    blendFactor.set(getBlur());
                }
            });
            currentBlur=getBlur();
        }

        lastWidth = client.width;
        lastHeight = client.height;
    }

    private static float getBlur() {
        return MotionBlur.getInstance().strength.get()/100F;
    }

    public class MotionBlurShader implements Resource {

        @Override
        public Identifier getId() {
            return null;
        }

        @Override
        public InputStream getInputStream() {
            return IOUtils.toInputStream(String.format("{" +
                    "    \"targets\": [" +
                    "        \"swap\"," +
                    "        \"previous\"" +
                    "    ]," +
                    "    \"passes\": [" +
                    "        {" +
                    "            \"name\": \"motion_blur\"," +
                    "            \"intarget\": \"minecraft:main\"," +
                    "            \"outtarget\": \"swap\"," +
                    "            \"auxtargets\": [" +
                    "                {" +
                    "                    \"name\": \"PrevSampler\"," +
                    "                    \"id\": \"previous\"" +
                    "                }" +
                    "            ]," +
                    "            \"uniforms\": [" +
                    "                {" +
                    "                    \"name\": \"BlendFactor\"," +
                    "                    \"values\": [ %s ]" +
                    "                }" +
                    "            ]" +
                    "        }," +
                    "        {" +
                    "            \"name\": \"blit\"," +
                    "            \"intarget\": \"swap\"," +
                    "            \"outtarget\": \"previous\"" +
                    "        }," +
                    "        {" +
                    "            \"name\": \"blit\"," +
                    "            \"intarget\": \"swap\"," +
                    "            \"outtarget\": \"minecraft:main\"" +
                    "        }" +
                    "    ]" +
                    "}", getBlur()));
        }

        @Override
        public boolean hasMetadata() {
            return false;
        }

        @Override
        public <T extends ResourceMetadataProvider> T getMetadata(String key) {
            return null;
        }

        @Override
        public String getResourcePackName() {
            return null;
        }
    }


}
