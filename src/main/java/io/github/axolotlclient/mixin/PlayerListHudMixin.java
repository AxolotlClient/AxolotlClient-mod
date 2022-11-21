/*
 * Copyright © 2021-2022 moehreag <moehreag@gmail.com> & Contributors
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
import io.github.axolotlclient.modules.hypixel.nickhider.NickHider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(PlayerListHud.class)
public abstract class PlayerListHudMixin extends DrawableHelper {

	MinecraftClient client = MinecraftClient.getInstance();
	private PlayerListEntry playerListEntry;

	@Inject(method = "getPlayerName", at = @At("HEAD"), cancellable = true)
	public void nickHider(PlayerListEntry playerEntry, CallbackInfoReturnable<String> cir){
		if(playerEntry.getProfile().getId()==MinecraftClient.getInstance().player.getUuid() &&
				NickHider.Instance.hideOwnName.get()){
			cir.setReturnValue(NickHider.Instance.hiddenNameSelf.get());
		} else if(playerEntry.getProfile().getId()!=MinecraftClient.getInstance().player.getUuid() &&
			NickHider.Instance.hideOtherNames.get()){
			cir.setReturnValue(NickHider.Instance.hiddenNameOthers.get());
		}
	}

	@ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/PlayerListHud;getPlayerName(Lnet/minecraft/client/network/PlayerListEntry;)Ljava/lang/String;"))
	public PlayerListEntry getPlayer(PlayerListEntry playerEntry){
		playerListEntry = playerEntry;
		return playerEntry;
	}

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;getStringWidth(Ljava/lang/String;)I", ordinal = 0))
	public int moveName(TextRenderer instance, String text){
		if(AxolotlClient.CONFIG.showBadges.get() && AxolotlClient.isUsingClient(playerListEntry.getProfile().getId())) return instance.getStringWidth(text)+10;
		return instance.getStringWidth(text);
	}

	@ModifyArgs(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow(Ljava/lang/String;FFI)I", ordinal = 1))
	public void getCoords(Args args){
		float x = args.get(1);
		float y = args.get(2);
		if(AxolotlClient.CONFIG.showBadges.get() && AxolotlClient.isUsingClient(playerListEntry.getProfile().getId())) {
			client.getTextureManager().bindTexture(AxolotlClient.badgeIcon);
			DrawableHelper.drawTexture((int) x, (int) y, 0, 0,  8, 8, 8, 8);
			args.set(1, x+10);
		}
	}

	@ModifyArgs(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow(Ljava/lang/String;FFI)I", ordinal = 2))
	public void getCoords2(Args args){
		float x=args.get(1);
		float y=args.get(2);
		if(AxolotlClient.CONFIG.showBadges.get() && AxolotlClient.isUsingClient(playerListEntry.getProfile().getId())) {
			client.getTextureManager().bindTexture(AxolotlClient.badgeIcon);
			DrawableHelper.drawTexture((int) x, (int) y, 0, 0,  8, 8, 8, 8);
			args.set(1, x+10);
		}
	}
}
