package io.github.axolotlclient.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TextRenderer.class)
public abstract class TextRendererMixin {

    // Pain at its finest

    @Shadow private float red;
    @Shadow private float green;
    @Shadow private float blue;
    @Shadow private float alpha;
    @Shadow private float x;
    @Shadow private float y;
    @Shadow public int fontHeight;
    private final Identifier texture_g = new Identifier("axolotlclient", "textures/font/g_breve_capital.png");

    private boolean shouldHaveShadow;

    @Inject(method = "drawLayer(Ljava/lang/String;FFIZ)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Ljava/lang/String;Z)V"))
    public void getData(String text, float x, float y, int color, boolean shadow, CallbackInfoReturnable<Integer> cir){
        if(text!=null) {
            shouldHaveShadow = shadow;
        }
    }

    @Inject(method = "drawLayerUnicode", at = @At("HEAD"), cancellable = true)
    public void gBreve(char c, boolean bl, CallbackInfoReturnable<Float> cir){
        if(c=='Ğ' && !MinecraftClient.getInstance().options.forcesUnicodeFont){
            MinecraftClient.getInstance().getTextureManager().bindTexture(texture_g);

            if(!bl || shouldHaveShadow) {
                GlStateManager.color4f(this.red / 4, this.green / 4, this.blue / 4, this.alpha);
                drawTexture(this.x + 1,
                        this.y - this.fontHeight + 7
                );
            }

            GlStateManager.color4f(this.red, this.green, this.blue, this.alpha);
            drawTexture(this.x,
                    this.y - this.fontHeight + 6
            );

            GlStateManager.color4f(this.red, this.green, this.blue, this.alpha);
            cir.setReturnValue(7.0F);
        }
    }

    @Inject(method = "getCharWidth", at = @At(value = "HEAD"), cancellable = true)
    public void modifiedCharWidth(char c, CallbackInfoReturnable<Integer> cir){
        if(c=='Ğ' && !MinecraftClient.getInstance().options.forcesUnicodeFont){
            cir.setReturnValue(7);
        }
    }

    private void drawTexture(float x, float y) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(x, y + 10, 0.0)
                .texture(0, 1)
                .next();
        bufferBuilder.vertex((x + 5), (y + 10), 0.0)
                .texture(1, 1)
                .next();
        bufferBuilder.vertex((x + 5), y, 0.0)
                .texture(1, 0)
                .next();
        bufferBuilder.vertex(x, y, 0.0)
                .texture(0,0)
                .next();
        tessellator.draw();
    }
}
