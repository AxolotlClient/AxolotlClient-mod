/*
 * Copyright © 2024 moehreag <moehreag@gmail.com> & Contributors
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

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.resource.CrossFrameResourcePool;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.modules.blur.MotionBlur;
import io.github.axolotlclient.modules.zoom.Zoom;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.Profiler;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

	@Shadow
	@Final
	private CrossFrameResourcePool resourcePool;

	@Shadow
	private boolean panoramicMode;

	@WrapOperation(method = "getFov", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;lerp(FFF)F"))
	private float disableDynamicFov(float delta, float start, float end, Operation<Float> original) {
		if (!AxolotlClient.CONFIG.dynamicFOV.get()) {
			return 1.0f;
		}
		return original.call(delta, start, end);
	}

	@WrapMethod(method = "getFov")
	private float getFov(Camera camera, float partialTick, boolean useFovSetting, Operation<Float> original) {
		if (this.panoramicMode) {
			return original.call(camera, partialTick, useFovSetting);
		}
		Zoom.update();
		return Zoom.getFov(original.call(camera, partialTick, useFovSetting), partialTick);
	}

	@Inject(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/GameRenderer;postEffectId:Lnet/minecraft/resources/ResourceLocation;", ordinal = 0))
	public void axolotlclient$worldMotionBlur(DeltaTracker tracker, boolean renderLevel, CallbackInfo ci) {
		axolotlclient$motionBlur(tracker, renderLevel, null);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;flush()V", ordinal = 1))
	public void axolotlclient$motionBlur(DeltaTracker tracker, boolean renderLevel, CallbackInfo ci) {
		if (ci != null && !MotionBlur.getInstance().inGuis.get()) {
			return;
		}

		Profiler.get().push("Motion Blur");

		if (MotionBlur.getInstance().enabled.get() && Minecraft.getInstance().isGameLoadFinished()) {
			MotionBlur blur = MotionBlur.getInstance();
			RenderSystem.resetTextureMatrix();
			blur.render(resourcePool);
		}

		Profiler.get().pop();
	}

	@Inject(method = "bobView",
		at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V"),
		cancellable = true)
	private void axolotlclient$minimalViewBob(PoseStack matrices, float tickDelta, CallbackInfo ci,
											  @Local(ordinal = 2) float g, @Local(ordinal = 3) float h) {
		if (AxolotlClient.CONFIG.minimalViewBob.get()) {
			g /= 2;
			h /= 2;
			matrices.translate(Mth.sin(g * (float) Math.PI) * h * 0.5F,
				-Math.abs(Mth.cos(g * (float) Math.PI) * h), 0.0F
			);
			matrices.mulPose(
				Axis.ZP.rotationDegrees(Mth.sin(g * (float) Math.PI) * h * 3.0F).get(new Matrix4f()));
			matrices.mulPose(
				Axis.XP.rotationDegrees(Math.abs(Mth.cos(g * (float) Math.PI - 0.2F) * h) * 5.0F)
					.get(new Matrix4f()));
			ci.cancel();
		}
	}

	@Inject(method = "bobHurt", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/client/Minecraft;getCameraEntity()Lnet/minecraft/world/entity/Entity;"),
		cancellable = true)
	private void axolotlclient$noHurtCam(PoseStack matrices, float tickDelta, CallbackInfo ci) {
		if (AxolotlClient.CONFIG.noHurtCam.get()) {
			ci.cancel();
		}
	}
}
