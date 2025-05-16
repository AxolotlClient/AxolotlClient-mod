/*
 * Copyright © 2025 moehreag <moehreag@gmail.com> & Contributors
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

package io.github.axolotlclient.mixin.api;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.axolotlclient.api.API;
import io.github.axolotlclient.api.multiplayer.FriendsMultiplayerScreen;
import io.github.axolotlclient.api.requests.FriendRequest;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.SelectServerScreen;
import net.minecraft.client.gui.widget.button.ButtonWidget;
import net.minecraft.client.gui.widget.list.multiplayer.ServerEntryListWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(SelectServerScreen.class)
public class JoinMulitplayerScreenMixin extends Screen {

	@Shadow
	@Final
	private Screen parent;
	@Unique
	private static final boolean WORLD_HOST_INSTALLED = FabricLoader.getInstance().isModLoaded("world-host");

	protected JoinMulitplayerScreenMixin(Text title) {
		super(title);
	}

	@Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/multiplayer/SelectServerScreen;addDrawableSelectableElement(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;", ordinal = 1))
	private void addFriendsMultiplayerScreenButtons(CallbackInfo ci) {
		if (API.getInstance().isAuthenticated() && !WORLD_HOST_INSTALLED) {
			addDrawableSelectableElement(ButtonWidget.builder(Text.translatable("api.servers"), button -> {

			}).position(this.width / 2 - 102, 32).width(100).build()).active = false;
			ButtonWidget friendsCountButton = addDrawableSelectableElement(ButtonWidget.builder(Text.translatable("api.servers.friends", "..."), button -> {
				client.setScreen(new FriendsMultiplayerScreen(this.parent));
			}).positionAndSize(width/2+2, 32, 100, 20).build());
			FriendRequest.getInstance().getOnlineFriendCount().thenAccept(count -> friendsCountButton.setMessage(Text.translatable("api.servers.friends", count)));
		}
	}

	@WrapOperation(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/list/multiplayer/ServerEntryListWidget;setDimensionsAndPosition(IIII)V"))
	private void increaseHeaderSize(ServerEntryListWidget instance, int width, int height, int x, int y, Operation<Void> original) {
		if (API.getInstance().isAuthenticated() && !WORLD_HOST_INSTALLED) {
			height += 32 - 60;
			y += -32 + 60;
		}
		original.call(instance, width, height, x, y);
	}

	@ModifyArgs(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/list/multiplayer/ServerEntryListWidget;<init>(Lnet/minecraft/client/gui/screen/multiplayer/SelectServerScreen;Lnet/minecraft/client/MinecraftClient;IIII)V"))
	private void increaseHeaderSize$2(Args args) {
		if (API.getInstance().isAuthenticated() && !WORLD_HOST_INSTALLED) {
			args.set(3, ((Integer)args.get(3)) + 32 - 60);
			args.set(4, ((Integer)args.get(4)) - 32 + 60);
		}
	}

	@ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawCenteredShadowedText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)V"), index = 3)
	private int shiftTitle(int par3) {
		if (API.getInstance().isAuthenticated() && !WORLD_HOST_INSTALLED) {
			return 15;
		}
		return par3;
	}
}
