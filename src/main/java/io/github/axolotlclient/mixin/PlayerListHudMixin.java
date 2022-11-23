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

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.modules.hypixel.nickhider.NickHider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerListHud.class)
public abstract class PlayerListHudMixin {

	private GameProfile cachedPlayer;


	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/PlayerListEntry;getProfile()Lcom/mojang/authlib/GameProfile;"))
	public GameProfile getPlayerGameProfile(PlayerListEntry instance){
		cachedPlayer = instance.getProfile();
		return instance.getProfile();
	}

	MinecraftClient client = MinecraftClient.getInstance();
	private PlayerListEntry playerListEntry;

	@Inject(method = "getPlayerName", at = @At("HEAD"), cancellable = true)
	public void nickHider(PlayerListEntry playerEntry, CallbackInfoReturnable<String> cir){
		if(playerEntry.getProfile().getId()==MinecraftClient.getInstance().player.getUuid() &&
				NickHider.getInstance().hideOwnName.get()){
			cir.setReturnValue(NickHider.getInstance().hiddenNameSelf.get());
		} else if(playerEntry.getProfile().getId()!=MinecraftClient.getInstance().player.getUuid() &&
				NickHider.getInstance().hideOtherNames.get()){
			cir.setReturnValue(NickHider.getInstance().hiddenNameOthers.get());
		}
	}

	@ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/PlayerListHud;getPlayerName(Lnet/minecraft/client/network/PlayerListEntry;)Lnet/minecraft/text/Text;"))
	public PlayerListEntry getPlayer(PlayerListEntry playerEntry){
		playerListEntry = playerEntry;
		return playerEntry;
	}

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;getWidth(Lnet/minecraft/text/StringVisitable;)I"))
	public int moveName(TextRenderer instance, StringVisitable text){
		if(AxolotlClient.CONFIG.showBadges.get() && AxolotlClient.isUsingClient(playerListEntry.getProfile().getId())) return instance.getWidth(text)+10;
		return instance.getWidth(text);
	}

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;FFI)I"))
	public int moveName(TextRenderer instance, MatrixStack matrices, Text text, float x, float y, int color){
		if(AxolotlClient.CONFIG.showBadges.get() && AxolotlClient.isUsingClient(cachedPlayer.getId())){

			RenderSystem.setShaderTexture(0, AxolotlClient.badgeIcon);
			RenderSystem.setShaderColor(1,1,1,1);

			DrawableHelper.drawTexture(matrices, (int) x, (int) y, 8, 8, 0, 0, 8, 8, 8, 8);

			x+=9;
		}
		cachedPlayer=null;
		return instance.drawWithShadow(matrices, text, x, y, color);
	}

	@ModifyArg(method = "getPlayerName", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/PlayerListHud;applyGameModeFormatting(Lnet/minecraft/client/network/PlayerListEntry;Lnet/minecraft/text/MutableText;)Lnet/minecraft/text/Text;"), index = 1)
	public MutableText hideNames(MutableText name){
		if(NickHider.getInstance().hideOwnName.get()){
			return Text.literal(NickHider.getInstance().hiddenNameSelf.get());
		}
		if(NickHider.getInstance().hideOtherNames.get()){
			return Text.literal(NickHider.getInstance().hiddenNameOthers.get());
		}
		return name;
	}
}
