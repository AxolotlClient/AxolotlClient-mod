package io.github.axolotlclient.modules.motionblur;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.shader.GlUniform;
import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.AxolotlclientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlclientConfig.options.FloatOption;
import io.github.axolotlclient.AxolotlclientConfig.options.OptionCategory;
import io.github.axolotlclient.mixin.ShaderEffectAccessor;
import io.github.axolotlclient.modules.AbstractModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;

import java.io.IOException;

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
        if(currentBlur!=getBlur() && shader != null){
            ((ShaderEffectAccessor)shader).getPasses().forEach(shader -> {
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
        return MotionBlur.getInstance().strength.get()/100F;
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
