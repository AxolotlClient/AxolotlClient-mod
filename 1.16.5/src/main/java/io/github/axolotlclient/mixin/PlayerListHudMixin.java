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

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.modules.hypixel.nickhider.NickHider;
import io.github.axolotlclient.modules.tablist.Tablist;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.ClientConnection;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerListHud.class)
public abstract class PlayerListHudMixin {

    private GameProfile cachedPlayer;

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/PlayerListEntry;getProfile()Lcom/mojang/authlib/GameProfile;"))
    public GameProfile axolotlclient$getPlayerGameProfile(PlayerListEntry instance) {
        cachedPlayer = instance.getProfile();
        return instance.getProfile();
    }

    @Shadow private Text header;
    @Shadow private Text footer;

    @Shadow
    protected abstract Text applyGameModeFormatting(PlayerListEntry par1, MutableText par2);

    MinecraftClient client = MinecraftClient.getInstance();

    private PlayerListEntry playerListEntry;

    @Inject(method = "getPlayerName", at = @At("HEAD"), cancellable = true)
    public void axolotlclient$nickHider(PlayerListEntry playerEntry, CallbackInfoReturnable<Text> cir) {
        assert MinecraftClient.getInstance().player != null;
        if (playerEntry.getProfile().equals(MinecraftClient.getInstance().player.getGameProfile())
                && NickHider.getInstance().hideOwnName.get()) {
            cir.setReturnValue(this.applyGameModeFormatting(playerEntry, new LiteralText(NickHider.getInstance().hiddenNameSelf.get())));
        } else if (!playerEntry.getProfile().equals(MinecraftClient.getInstance().player.getGameProfile())
                && NickHider.getInstance().hideOtherNames.get()) {
            cir.setReturnValue(this.applyGameModeFormatting(playerEntry, new LiteralText(NickHider.getInstance().hiddenNameOthers.get())));
        }
    }

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/PlayerListHud;getPlayerName(Lnet/minecraft/client/network/PlayerListEntry;)Lnet/minecraft/text/Text;"))
    public PlayerListEntry axolotlclient$getPlayer(PlayerListEntry playerEntry) {
        playerListEntry = playerEntry;
        return playerEntry;
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;getWidth(Lnet/minecraft/text/StringVisitable;)I"))
    public int axolotlclient$moveName(TextRenderer instance, StringVisitable text) {
        if (AxolotlClient.CONFIG.showBadges.get() && AxolotlClient.isUsingClient(playerListEntry.getProfile().getId()))
            return instance.getWidth(text) + 10;
        return instance.getWidth(text);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;FFI)I"))
    public int axolotlclient$moveName(TextRenderer instance, MatrixStack matrices, Text text, float x, float y, int color) {
        if (AxolotlClient.CONFIG.showBadges.get() && AxolotlClient.isUsingClient(cachedPlayer.getId())) {
            MinecraftClient.getInstance().getTextureManager().bindTexture(AxolotlClient.badgeIcon);
            RenderSystem.color4f(1, 1, 1, 1);

            DrawableHelper.drawTexture(matrices, (int) x, (int) y, 8, 8, 0, 0, 8, 8, 8, 8);

            x += 9;
        }
        cachedPlayer = null;
        return instance.drawWithShadow(matrices, text, x, y, color);
    }

    @ModifyArg(method = "getPlayerName", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/PlayerListHud;applyGameModeFormatting(Lnet/minecraft/client/network/PlayerListEntry;Lnet/minecraft/text/MutableText;)Lnet/minecraft/text/Text;"), index = 1)
    public MutableText axolotlclient$hideNames(MutableText par2) {
        if (NickHider.getInstance().hideOwnName.get()) {
            return new LiteralText(NickHider.getInstance().hiddenNameSelf.get());
        }
        if (NickHider.getInstance().hideOtherNames.get()) {
            return new LiteralText(NickHider.getInstance().hiddenNameOthers.get());
        }
        return par2;
    }

    @Inject(method = "renderLatencyIcon", at = @At("HEAD"), cancellable = true)
    private void axolotlclient$numericalPing(MatrixStack matrices, int width, int x, int y, PlayerListEntry entry, CallbackInfo ci){
        if(Tablist.getInstance().renderNumericPing(matrices, width, x, y, entry)){
            ci.cancel();
        }
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;isInSingleplayer()Z"))
    private boolean showPlayerHeads$1(MinecraftClient instance) {
        if (Tablist.getInstance().showPlayerHeads.get()) {
            return instance.isInSingleplayer();
        }
        return false;
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;isEncrypted()Z"))
    private boolean axolotlclient$showPlayerHeads$1(ClientConnection instance) {
        if (Tablist.getInstance().showPlayerHeads.get()) {
            return instance.isEncrypted();
        }
        return false;
    }

    @Inject(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/hud/PlayerListHud;header:Lnet/minecraft/text/Text;"))
    private void axolotlclient$setRenderHeaderFooter(MatrixStack matrices, int scaledWindowWidth, Scoreboard scoreboard, ScoreboardObjective objective, CallbackInfo ci){
        if(!Tablist.getInstance().showHeader.get()){
            header = null;
        }
        if(!Tablist.getInstance().showFooter.get()){
            footer = null;
        }
    }
}
