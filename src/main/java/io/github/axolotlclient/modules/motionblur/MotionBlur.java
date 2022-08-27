package io.github.axolotlclient.modules.motionblur;

import com.google.gson.JsonSyntaxException;
import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.mixin.AccessorShaderEffect;
import io.github.axolotlclient.modules.AbstractModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.resource.Resource;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;

public class MotionBlur extends AbstractModule {

    private static final MotionBlur Instance = new MotionBlur();

    private final Identifier shaderLocation = new Identifier("minecraft:shaders/post/motion_blur.json");

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
        AxolotlClient.runtimeResources.put(shaderLocation, new MotionBlurShader());
    }

    public void onUpdate() {
        if( (shader == null ||
	        MinecraftClient.getInstance().getWindow().getWidth()!=lastWidth ||
            MinecraftClient.getInstance().getWindow().getHeight()!=lastHeight ) &&
            MinecraftClient.getInstance().getWindow().getWidth() > 0 &&
            MinecraftClient.getInstance().getWindow().getHeight() > 0) {
            currentBlur=getBlur();
            try {
                shader = new ShaderEffect(client.getTextureManager(),
                        client.getResourceManager(), client.getFramebuffer(),
                        shaderLocation);
                shader.setupDimensions(MinecraftClient.getInstance().getWindow().getWidth(), MinecraftClient.getInstance().getWindow().getHeight());
            } catch (JsonSyntaxException | IOException e) {
                AxolotlClient.LOGGER.error("Could not load motion blur", e);
            }
        }
        if(currentBlur!=getBlur()){
            ((AccessorShaderEffect)shader).getPasses().forEach(shader -> {
                GlUniform blendFactor = shader.getProgram().getUniformByName("BlendFactor");
                if(blendFactor!=null){
                    blendFactor.set(getBlur());
                }
            });
            currentBlur=getBlur();
        }

        lastWidth = MinecraftClient.getInstance().getWindow().getWidth();
        lastHeight = MinecraftClient.getInstance().getWindow().getHeight();
    }

    private static float getBlur() {
        return AxolotlClient.CONFIG.motionBlurStrength.get()/100F;
    }

    public class MotionBlurShader implements Resource {

        @Override
        public Identifier getId() {
            return null;
        }

        @Override
        public InputStream getInputStream() {
            try {
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
                    "}", getBlur()) , "utf-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Nullable
        @Override
        public <T> T getMetadata(ResourceMetadataReader<T> resourceMetadataReader) {
            return null;
        }

        @Override
        public String getResourcePackName() {
            return null;
        }

        @Override
        public void close() {

        }
    }


}
