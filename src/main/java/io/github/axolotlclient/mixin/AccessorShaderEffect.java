package io.github.axolotlclient.mixin;

import net.minecraft.client.gl.PostProcessShader;
import net.minecraft.client.gl.ShaderEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(ShaderEffect.class)
public interface AccessorShaderEffect {

    @Accessor
    List<PostProcessShader> getPasses();
}
