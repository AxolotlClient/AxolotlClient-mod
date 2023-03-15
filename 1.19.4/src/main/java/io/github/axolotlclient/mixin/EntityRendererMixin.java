/*
 * Copyright © 2021-2023 moehreag <moehreag@gmail.com> & Contributors
 *
 * This file is part of AxolotlClient.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 * For more information, see the LICENSE file.
 */

package io.github.axolotlclient.mixin;

import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.modules.hypixel.HypixelAbstractionLayer;
import io.github.axolotlclient.modules.hypixel.levelhead.LevelHead;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {

	@Inject(method = "renderLabelIfPresent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;computeVertices(Lnet/minecraft/text/Text;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)I", ordinal = 0))
	public void axolotlclient$addBadges(T entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
										CallbackInfo ci) {
		if (entity instanceof AbstractClientPlayerEntity && text.getString().contains(entity.getName().getString()))
			AxolotlClient.addBadge(entity, matrices);
	}

	@ModifyArg(method = "renderLabelIfPresent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;computeVertices(Lnet/minecraft/text/Text;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)I"), index = 8)
	public int axolotlclient$bgColor(int color) {
		if (AxolotlClient.CONFIG.nametagBackground.get()) {
			return color;
		} else {
			return 0;
		}
	}

	@ModifyArg(method = "renderLabelIfPresent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;computeVertices(Lnet/minecraft/text/Text;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)I"), index = 4)
	public boolean axolotlclient$enableShadows(boolean shadow) {
		return AxolotlClient.CONFIG.useShadows.get();
	}

	@Inject(method = "renderLabelIfPresent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;computeVertices(Lnet/minecraft/text/Text;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)I", ordinal = 1))
	public void axolotlclient$addLevel(T entity, Text string, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
									   CallbackInfo ci) {
		if (entity instanceof AbstractClientPlayerEntity) {
			if (MinecraftClient.getInstance().getCurrentServerEntry() != null
					&& MinecraftClient.getInstance().getCurrentServerEntry().address.contains("hypixel.net")) {
				if (HypixelAbstractionLayer.hasValidAPIKey() && LevelHead.getInstance().enabled.get()
						&& string.getString().contains(entity.getName().getString())) {
					TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
					String text = "Level: " + HypixelAbstractionLayer.getPlayerLevel(String.valueOf(entity.getUuid()), LevelHead.getInstance().mode.get());

					float x = -textRenderer.getWidth(text) / 2F;
					float y = string.getString().contains("deadmau5") ? -20 : -10;

					Matrix4f matrix4f = matrices.peek().getModel();
					MinecraftClient.getInstance().textRenderer.computeVertices(text, x, y,
							LevelHead.getInstance().textColor.get().getAsInt(), AxolotlClient.CONFIG.useShadows.get(),
							matrix4f, vertexConsumers, TextRenderer.TextLayerType.NORMAL, LevelHead.getInstance().background.get() ? 127 : 0,
							light);
				} else if (!HypixelAbstractionLayer.hasValidAPIKey()) {
					HypixelAbstractionLayer.loadApiKey();
				}
			}
		}
	}
}
