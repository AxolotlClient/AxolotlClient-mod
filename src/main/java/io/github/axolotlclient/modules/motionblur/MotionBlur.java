package io.github.axolotlclient.modules.motionblur;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.shader.GlUniform;
import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.mixin.AccessorShaderEffect;
import io.github.axolotlclient.modules.AbstractModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;

import java.io.IOException;

public class MotionBlur extends AbstractModule {

    public static Identifier ID = new Identifier("motion_blur");

    private final Identifier shaderLocation = new Identifier("minecraft:shaders/post/motion_blur.json");

    public ShaderEffect shader;
    private final MinecraftClient client = MinecraftClient.getInstance();
    private float currentBlur;

    private int lastWidth;

    private int lastHeight;

    @Override
    public void init() {
        AxolotlClient.runtimeResources.put(shaderLocation, new MotionBlurShader());
    }

    public void onUpdate() {
        if(shader == null ||
	        MinecraftClient.getInstance().getWindow().getWidth()!=lastWidth ||
	        MinecraftClient.getInstance().getWindow().getHeight()!=lastHeight) {
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
                    blendFactor.setFloat(getBlur());
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

    public class MotionBlurShader extends Resource {

	    public MotionBlurShader() {
		    super("", ()-> IOUtils.toInputStream(String.format("{" +
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
			    "}", getBlur()) , "utf-8"));
	    }
    }


}
