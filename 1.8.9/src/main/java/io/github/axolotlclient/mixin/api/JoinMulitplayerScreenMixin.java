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
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(MultiplayerScreen.class)
public class JoinMulitplayerScreenMixin extends Screen {

	@Shadow
	private Screen parent;
	@Unique
	private static final boolean WORLD_HOST_INSTALLED = FabricLoader.getInstance().isModLoaded("world-host");

	protected JoinMulitplayerScreenMixin() {
		super();
	}

	@Inject(method = "addButtons", at = @At(value = "HEAD"))
	private void addFriendsMultiplayerScreenButtons(CallbackInfo ci) {
		if (API.getInstance().isAuthenticated() && !WORLD_HOST_INSTALLED) {
			var serversButton = new ButtonWidget(-1, this.width / 2 - 102, 32, 100, 20, I18n.translate("api.servers"));
			buttons.add(serversButton);
			serversButton.active = false;
			ButtonWidget friendsCountButton = new ButtonWidget(274, width / 2 + 2, 32, 100, 20, I18n.translate("api.servers.friends", "..."));
			buttons.add(friendsCountButton);
			FriendRequest.getInstance().getOnlineFriendCount().thenAccept(count -> friendsCountButton.message = (I18n.translate("api.servers.friends", count)));
		}
	}

	@Inject(method = "buttonClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/multiplayer/MultiplayerServerListWidget;getCurrentServerIndex()I", ordinal = 0), cancellable = true)
	private void onButtonClick(ButtonWidget buttonWidget, CallbackInfo ci) {
		if (buttonWidget.id == 274) {
			minecraft.openScreen(new FriendsMultiplayerScreen(this.parent));
			ci.cancel();
		}
	}

	@WrapOperation(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/multiplayer/MultiplayerServerListWidget;updateBounds(IIII)V"))
	private void increaseHeaderSize(MultiplayerServerListWidget instance, int width, int height, int top, int bottom, Operation<Void> original) {
		if (API.getInstance().isAuthenticated() && !WORLD_HOST_INSTALLED) {
			top -= 32;
			top += 60;
		}
		original.call(instance, width, height, top, bottom);
	}

	@ModifyArgs(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/multiplayer/MultiplayerServerListWidget;<init>(Lnet/minecraft/client/gui/screen/multiplayer/MultiplayerScreen;Lnet/minecraft/client/Minecraft;IIIII)V"))
	private void increaseHeaderSize$2(Args args) {
		if (API.getInstance().isAuthenticated() && !WORLD_HOST_INSTALLED) {
			args.set(4, ((Integer) args.get(4)) - 32 + 60);
		}
	}

	@ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/multiplayer/MultiplayerScreen;drawCenteredString(Lnet/minecraft/client/render/TextRenderer;Ljava/lang/String;III)V"), index = 3)
	private int shiftTitle(int par3) {
		if (API.getInstance().isAuthenticated() && !WORLD_HOST_INSTALLED) {
			return 15;
		}
		return par3;
	}
}
