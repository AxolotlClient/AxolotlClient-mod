package io.github.moehreag.axolotlclient.modules.motionblur;

import com.google.gson.JsonSyntaxException;
import io.github.moehreag.axolotlclient.AxolotlClient;
import io.github.moehreag.axolotlclient.config.options.FloatOption;
import io.github.moehreag.axolotlclient.mixin.AccessorShaderEffect;
import io.github.moehreag.axolotlclient.modules.AbstractModule;
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

    public static Identifier ID = new Identifier("motion_blur");

    public Identifier shader_location = new Identifier("minecraft:shaders/post/motion_blur.json");

    public ShaderEffect shader;
    //private final FloatOption blurStrength = new FloatOption("blurStrength", 0F, 1F, 0.5F);
    private float blur=0.5F;
    private final MinecraftClient client = MinecraftClient.getInstance();
    private float currentBlur;

    @Override
    public void init() {
        AxolotlClient.runtimeResources.put(shader_location, new MotionBlurShader());
    }

    @Override
    public void lateInit() {
        //AxolotlClient.CONFIG.motionBlur.add(blur);
    }

    public void onUpdate() {
        if (shader == null) {
            currentBlur=blur;
            try {
                shader = new ShaderEffect(client.getTextureManager(),
                        client.getResourceManager(), client.getFramebuffer(),
                        shader_location);
                shader.setupDimensions(client.width, client.height);
            } catch (JsonSyntaxException | IOException e) {
                AxolotlClient.LOGGER.error("Could not load motion blur", e);
            }
        }
        if(currentBlur!=blur){
            ((AccessorShaderEffect)shader).getPasses().forEach(shader -> {
                GlUniform blendFactor = shader.getProgram().getUniformByName("BlendFactor");
                if(blendFactor!=null){
                    blendFactor.method_6976(blur);
                }
            });
            currentBlur=blur;
        }
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
                    "}", blur));
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
