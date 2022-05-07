package io.github.moehreag.axolotlclient.mixin;

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

    @Shadow private float field_1149;

    @Shadow private float field_1150;

    @Shadow public int fontHeight;

    @Shadow private boolean field_1161;

    @Shadow private float field_1156;

    @Shadow private float field_1155;

    @Shadow private float field_1154;

    @Shadow private float field_1153;

    private int color;

    @Inject(method = "drawLayer", at = @At("TAIL"))
    public void getData(String text, float x, float y, int color, boolean shadow, CallbackInfoReturnable<Integer> cir){
        this.color=color;
    }

    @Inject(method = "method_950", at = @At("HEAD"), cancellable = true)
    public void gBreve(char c, boolean bl, CallbackInfoReturnable<Float> cir){
        if(c=='Ğ'){
            MinecraftClient.getInstance().getTextureManager().bindTexture(new Identifier("axolotlclient",
                    "textures/font/g_breve_capital.png"));

            if(bl) {
                float alpha = (float)(color >> 16 & 0xFF) / 255.0F;
                float red = (float)(color >> 8 & 0xFF) / 255.0F;
                float green = (float)(color & 0xFF) / 255.0F;
                float blue = (float)(color >> 24 & 0xFF) / 255.0F;

                GlStateManager.color4f(alpha, red, green, blue);
                //GlStateManager.color4f(this.field_1153, this.field_1154, this.field_1155, this.field_1156);
                drawTexture(this.field_1149 + 1,
                        this.field_1150 - this.fontHeight + 7
                );
                GlStateManager.color4f(this.field_1153, this.field_1154, this.field_1155, this.field_1156);
            }

            GlStateManager.color4f(this.field_1153, this.field_1154, this.field_1155, this.field_1156);
            drawTexture(this.field_1149,
                    this.field_1150 - this.fontHeight + 6
            );

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
