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
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TextRenderer.class)
public abstract class TextRendererMixin {

    // Pain at its finest

    private final Identifier texture_g = new Identifier("axolotlclient", "textures/font/g_breve_capital.png");

    @Shadow private float field_1149;

    @Shadow private float field_1150;

    @Shadow public int fontHeight;

    @Shadow private float field_1156;

    @Shadow private float field_1155;

    @Shadow private float field_1154;

    @Shadow private float field_1153;

    private boolean shouldHaveShadow;

    @Inject(method = "drawLayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;method_959(Ljava/lang/String;Z)V"))
    public void getData(String text, float x, float y, int color, boolean shadow, CallbackInfoReturnable<Integer> cir){
        if(text!=null) {
            shouldHaveShadow = shadow;
        }
    }

    @Inject(method = "method_950", at = @At("HEAD"), cancellable = true)
    public void gBreve(char c, boolean bl, CallbackInfoReturnable<Float> cir){
        if(c=='Ğ'){
            MinecraftClient.getInstance().getTextureManager().bindTexture(texture_g);

            if(!bl || shouldHaveShadow) {
                GlStateManager.color4f(this.field_1153 / 4, this.field_1154 / 4, this.field_1155 / 4, this.field_1156);
                drawTexture(this.field_1149 + 1,
                        this.field_1150 - this.fontHeight + 7
                );
            }

            GlStateManager.color4f(this.field_1153, this.field_1154, this.field_1155, this.field_1156);
            drawTexture(this.field_1149,
                    this.field_1150 - this.fontHeight + 6
            );

            GlStateManager.color4f(this.field_1153, this.field_1154, this.field_1155, this.field_1156);
            cir.setReturnValue(7.0F);
        }
    }

    @Inject(method = "method_949", at = @At(value = "HEAD"), cancellable = true)
    public void modifiedCharWidth(char c, CallbackInfoReturnable<Integer> cir){
        if(c=='Ğ'){
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
